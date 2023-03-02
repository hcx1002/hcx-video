package vip.huancaixi.video.module.video.entity;


import lombok.Data;

@Data
public class VideoInfoUpdate {
    private String id;
    private String title;
    private Integer grade;  //等级
    private Integer status;
}
