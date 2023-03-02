package vip.huancaixi.video.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vip.huancaixi.video.config.RabbitMQConfig;
import vip.huancaixi.video.module.video.entity.TranscodeConfig;
import vip.huancaixi.video.module.video.entity.VideoInfo;
import vip.huancaixi.video.module.video.service.IVideoInfoService;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class MQReceiver {
    @Value("${app.video-folder}")
    private String videoFolder;
    @Resource
    private IVideoInfoService videoInfoService;

    //消费消息
    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiver(String message) throws IOException {
        log.info("消费消息");
        JSONObject object = JSONUtil.parseObj(message);
        TranscodeConfig transcodeConfig = JSONUtil.toBean(object.getJSONObject("transcodeConfig"), TranscodeConfig.class);
        VideoInfo videoInfo = JSONUtil.toBean(object.getJSONObject("videoInfo"), VideoInfo.class);
        String title = videoInfo.getTitle();
        // 按照日期生成子目录
        String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        // 尝试创建视频目录
        Path targetFolder = Files.createDirectories(Paths.get(videoFolder, today, title));
        log.info("创建文件夹目录："+targetFolder);
        Files.createDirectories(targetFolder);
        // 执行转码操作
        log.info("开始转码");
        try {
            FFmpegUtils.transcodeToM3u8(object.getStr("tempFile"), targetFolder.toString(), transcodeConfig,videoInfo,object.getStr("key"));
            VideoInfo info = new VideoInfo();
            info.setPoster(String.join("/", "http://localhost", today, title, "poster.jpg"));
            info.setM3u8(String.join("/", "http://localhost", today, title, "index.m3u8"));
            info.setId(videoInfo.getId());
            info.setTime(videoInfo.getTime());
            info.setStatus(0);
            videoInfoService.updateById(info);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // 始终删除临时文件
            Files.delete(Paths.get(object.getStr("tempFile")));
        }
    }
}
