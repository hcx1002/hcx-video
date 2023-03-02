package vip.huancaixi.video.common;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 通用分页请求
 *
 * @author xuyuxiang
 * @date 2021/12/18 14:43
 */
public class CommonPageRequest {

    private static final String PAGE_SIZE_PARAM_NAME = "size";

    private static final String PAGE_PARAM_NAME = "current";

    private static final Integer PAGE_SIZE_MAX_VALUE = 100;

    public static <T> Page<T> defaultPage() {
        return defaultPage(null);
    }

    public static <T> Page<T> defaultPage(List<OrderItem> orderItemList) {

        int size = 20;

        int page = 1;
        return new Page<>(page, size);
    }
}
