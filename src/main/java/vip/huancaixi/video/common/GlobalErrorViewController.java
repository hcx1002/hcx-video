package vip.huancaixi.video.common;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.huancaixi.video.utils.Respond;

import javax.servlet.http.HttpServletRequest;

@RestController
public class GlobalErrorViewController {

    /**
     * Error页面视图，直接响应JSON
     *
     * @author xuyuxiang
     * @date 2022/2/11 16:11
     **/
    @RequestMapping("/errorView")
    public Respond globalError(HttpServletRequest request) {
        Respond commonResult = Respond.fail();
        Object model = request.getAttribute("model");
        if(ObjectUtil.isNotEmpty(model)) {
            JSONObject errorObj = JSONUtil.parseObj(model);
            Integer code = errorObj.getInt("code");
            String msg = errorObj.getStr("msg");
            if(ObjectUtil.isAllNotEmpty(code, msg)) {
                commonResult.setCode(code);
                commonResult.setMessage(msg);
            } else if(ObjectUtil.isNotEmpty(msg)) {
                commonResult.setMessage(msg);
            } else {
                commonResult = Respond.fail();
            }
            Exception exception = (Exception) model;
            exception.printStackTrace();
        }
        return commonResult;
    }
}
