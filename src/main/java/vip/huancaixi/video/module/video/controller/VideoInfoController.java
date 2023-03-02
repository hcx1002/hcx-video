package vip.huancaixi.video.module.video.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vip.huancaixi.video.common.CommonPageRequest;
import vip.huancaixi.video.common.CommonSortOrderEnum;
import vip.huancaixi.video.module.user.entity.User;
import vip.huancaixi.video.module.user.service.IUserService;
import vip.huancaixi.video.module.video.entity.TranscodeConfig;
import vip.huancaixi.video.module.video.entity.VideoInfo;
import vip.huancaixi.video.module.video.entity.VideoInfoUpdate;
import vip.huancaixi.video.module.video.entity.VideoPageParams;
import vip.huancaixi.video.module.video.service.IVideoInfoService;
import vip.huancaixi.video.utils.MQSender;
import vip.huancaixi.video.utils.Respond;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hcx
 * @since 2023-02-25
 */
@RestController
@RequestMapping("/video")
public class VideoInfoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoInfoController.class);
    private Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

    @Resource
    private IVideoInfoService videoInfoService;
    @Resource
    private IUserService userService;

    @Resource
    private MQSender mqSender;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 上传视频进行切片处理，返回访问路径
     *
     * @param video
     * @param transcodeConfig
     * @return
     * @throws IOException
     */
    @PostMapping("upload")
    @SaCheckPermission("upload")
    public Respond upload(@RequestPart(name = "file") MultipartFile video,
                          @RequestPart(name = "config") TranscodeConfig transcodeConfig) throws IOException {
        String loginIdAsString = StpUtil.getLoginIdAsString();
        LOGGER.info("文件信息：title={}, size={}", video.getOriginalFilename(), video.getSize());
        LOGGER.info("转码配置：{}", transcodeConfig);
        // 原始文件名称，也就是视频的标题
        String title = video.getOriginalFilename();
        // io到临时文件
        Path tempFile = tempDir.resolve(title);
        video.transferTo(tempFile);
        // 删除后缀
        title = title.substring(0, title.lastIndexOf("."));
        // 创建视频信息实体
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setUserId(loginIdAsString);
        videoInfo.setTitle(title);
        videoInfo.setCreateTime(new Date());
        videoInfo.setSize(video.getSize());
        videoInfo.setStatus(2); //视频处理中
        videoInfoService.save(videoInfo);   //保存后返回ID

        //创建id 用于获取进度接口
        String id = SecureUtil.md5(IdUtil.simpleUUID());
        String key = "transcodingVideo:" + loginIdAsString + ":" +videoInfo.getId()+":"+id;
        videoInfo.setM3u8(id);
        //发送到rabbitmq转码视频
        JSONObject object = JSONUtil.createObj();
        object.putOnce("tempFile", tempFile.toString());
        object.putOnce("transcodeConfig", JSONUtil.parseObj(transcodeConfig));
        object.putOnce("videoInfo", JSONUtil.parseObj(videoInfo));
        object.putOnce("key", key);
        mqSender.videoMessage(object.toString());
        return Respond.success(videoInfo);

    }

    @PostMapping("page")
    public Respond page(@RequestBody VideoPageParams params) {
        QueryWrapper<VideoInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VideoInfo::getStatus,1);   //只获取公开的视频
        if (ObjectUtil.isNotEmpty(params.getTitle())) {
            queryWrapper.lambda().eq(VideoInfo::getTitle, params.getTitle());
        }
        if (ObjectUtil.isAllNotEmpty(params.getSortField(), params.getSortOrder())) {
            CommonSortOrderEnum.validate(params.getSortOrder());
            queryWrapper.orderBy(true, params.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), params.getSortField());
        } else {
            queryWrapper.lambda().orderByDesc(VideoInfo::getCreateTime);
        }
        Page<VideoInfo> objectPage = CommonPageRequest.defaultPage();
        if (ObjectUtil.isNotEmpty(params.getSize())) {
            objectPage.setSize(params.getSize());
        }
        if (ObjectUtil.isNotEmpty(params.getCurrent())) {
            objectPage.setCurrent(params.getCurrent());
        }
        Page<VideoInfo> page = videoInfoService.page(objectPage, queryWrapper);
        page.getRecords().forEach(res -> {
            if (ObjectUtil.isNotEmpty(res.getUserId())) {
                res.setUser(userService.getById(res.getUserId()));
            }
        });
        return Respond.success(page);
    }

    @PostMapping("updateVideoInfo")
    @SaCheckLogin
    public Respond updateVideoInfo(@RequestBody VideoInfoUpdate videoInfo) {
        if (ObjectUtil.isEmpty(videoInfo)) return Respond.fail();
        VideoInfo info = new VideoInfo();
        // 防止视频转码中状态被修改
        if (videoInfo.getStatus()==2) videoInfo.setStatus(null);
        BeanUtil.copyProperties(videoInfo, info);
        User byId = userService.getById(StpUtil.getLoginIdAsString());
        info.setUserId(byId.getId());
        videoInfoService.updateById(info);
        return Respond.success(videoInfoService.getById(info.getId()));
    }

    @GetMapping("getVideoById")
    public Respond getVideoById(String videoId) {
        if (ObjectUtil.isEmpty(videoId)) return Respond.fail("参数为空");
        VideoInfo videoInfo = videoInfoService.getById(videoId);
        if (ObjectUtil.isEmpty(videoInfo)) return Respond.fail("视频不存在");
        if (ObjectUtil.isNotEmpty(videoInfo.getUserId())) videoInfo.setUser(userService.getById(videoInfo.getUserId()));
        String loginIdAsString = (String) StpUtil.getLoginIdDefaultNull();
        if (ObjectUtil.isNotEmpty(loginIdAsString)) {
            User user = userService.getById(loginIdAsString);
            if (videoInfo.getGrade() > user.getVip()) return Respond.fail("该视频需要 VIP" + videoInfo.getGrade() + " 才可观看");
            else return Respond.success(videoInfo);

        }
        // 1 默认所有人可观看
        if (videoInfo.getGrade() == 1) return Respond.success(videoInfo);

        return Respond.fail("视频需要登录才可观看");
    }

    @GetMapping("getUserVideoByMi")
    public Respond getUserVideoByMi() {
        String loginIdAsString = StpUtil.getLoginIdAsString();
        if (ObjectUtil.isNotEmpty(loginIdAsString)) {
            User user = userService.getById(loginIdAsString);
            List<VideoInfo> list = videoInfoService.list(new QueryWrapper<VideoInfo>().lambda().eq(VideoInfo::getUserId, user.getId()).orderByDesc(VideoInfo::getCreateTime));
            return Respond.success(list);
        }
        return Respond.fail("未登录");
    }

    @GetMapping("getUserVideo")
    public Respond getUserVideo(String userId) {
        if (ObjectUtil.isEmpty(userId)) return Respond.fail();
        User user = userService.getById(userId);
        if (ObjectUtil.isEmpty(user)) return Respond.fail("用户不存在");
        List<VideoInfo> list = videoInfoService.list(new QueryWrapper<VideoInfo>().lambda().eq(VideoInfo::getUserId, userId));
        return Respond.success(list);
    }

    /**
     * 设置视频的播放量
     * @param videoId
     * @return
     */
    @GetMapping("setVideoView")
    public Respond setVideoView(String videoId) {
        if (ObjectUtil.isEmpty(videoId)) return Respond.fail();
        String loginIdAsString = (String) StpUtil.getLoginIdDefaultNull();
        if (ObjectUtil.isNotEmpty(loginIdAsString)) {
            videoInfoService.update(new UpdateWrapper<VideoInfo>().lambda().eq(VideoInfo::getId, videoId).setSql("view_count = view_count+1"));
        }
        return Respond.success();
    }

    @GetMapping("delVideo")
    public Respond delVideo(String videoId) {
        if (ObjectUtil.isEmpty(videoId)) return Respond.fail();
        String loginIdAsString = StpUtil.getLoginIdAsString();
        if (ObjectUtil.isNotEmpty(loginIdAsString)) {
            videoInfoService.removeById(videoId);
        }
        return Respond.success();
    }

    /**
     * 获取视频转码的进度
     * @param id
     * @param videoId
     * @return
     */
    @GetMapping("getTranscodingVideo/{id}")
    public Respond getTranscodingVideo(@PathVariable String id,String videoId){
        String loginIdAsString = StpUtil.getLoginIdAsString();
        double process = (double) redisTemplate.opsForValue().get("transcodingVideo:" + loginIdAsString + ":" + videoId+":"+id);
        if (ObjectUtil.isEmpty(process)) return Respond.fail("当前视频不存在");
        JSONObject obj = JSONUtil.createObj();
        obj.putOnce("videoId",videoId);
        obj.putOnce("process",process);
        return Respond.success(obj);
    }
}
