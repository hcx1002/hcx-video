package vip.huancaixi.video.common;

import lombok.Getter;

@Getter
public enum CommonSortOrderEnum {

    /** 升序 */
    ASC("ASC"),

    /** 降序 */
    DESC("DESC");

    private final String value;

    CommonSortOrderEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = ASC.getValue().toLowerCase().equals(value) || DESC.getValue().toLowerCase().equals(value);
        if(!flag) {
            throw new CommonException("不支持该排序方式：{}", value);
        }
    }
}
