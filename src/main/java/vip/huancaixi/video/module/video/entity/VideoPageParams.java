package vip.huancaixi.video.module.video.entity;

import lombok.Data;

@Data
public class VideoPageParams {

    /** 当前页 */
    private Integer current;

    /** 每页条数 */
    private Integer size;

    /** 排序字段 */
    private String sortField;

    /** 排序方式 */
    private String sortOrder;

    /** 关键词 */
    private String searchKey;

    /** 用户名 */
    private String title;

}
