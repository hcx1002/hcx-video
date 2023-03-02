package vip.huancaixi.video.module.video.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import vip.huancaixi.video.module.user.entity.User;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author hcx
 * @since 2023-02-25
 */
@Getter
@Setter
@TableName("video_info")
public class VideoInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    @TableField(exist = false)
    private User user;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 真实m3u8地址
     */
    private String m3u8;

    /**
     * 切图
     */
    private String poster;

    /**
     * 视频时长
     */
    private String time;

    /**
     * 视频大小
     */
    private Long size;

    /**
     * 上传时间
     */
    private Date createTime;
    private Integer status;
    private Integer viewCount;
    private Integer grade;  //等级
}
