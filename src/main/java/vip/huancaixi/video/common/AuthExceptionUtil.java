package vip.huancaixi.video.common;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.*;
import lombok.extern.slf4j.Slf4j;
import vip.huancaixi.video.utils.Respond;
@Slf4j
public class AuthExceptionUtil {

    /**
     * 根据错误类型获取对应的CommonResult（只处理SaToken相关异常）
     *
     * @author xuyuxiang
     * @date 2021/10/11 15:52
     **/
    public static Respond getCommonResult(Exception e) {
        Respond commonResult;
        if (e instanceof NotLoginException) {

            // 如果是未登录异常 401
            NotLoginException notLoginException = (NotLoginException) e;
            commonResult = Respond.failSa(notLoginException.getMessage());
        } else if (e instanceof NotRoleException) {

            // 如果是角色异常 403
            NotRoleException notRoleException = (NotRoleException) e;
            commonResult = Respond.failSa("无此角色：" + notRoleException.getRole() +
                    "，接口地址：" + CommonServletUtil.getRequest().getServletPath());
        } else if (e instanceof NotPermissionException) {

            // 如果是权限异常 403
            NotPermissionException notPermissionException = (NotPermissionException) e;
            commonResult = Respond.failSa("无此权限：" + notPermissionException.getPermission());
        } else if (e instanceof DisableServiceException) {

            // 如果是被封禁异常 403
            DisableServiceException disableServiceException = (DisableServiceException) e;
            commonResult = Respond.failSa( "账号被封禁：" + disableServiceException.getDisableTime() + "秒后解封");
        } else if (e instanceof SaTokenException) {

            // 如果是SaToken异常 直接返回
            SaTokenException saTokenException = (SaTokenException) e;
            commonResult = Respond.fail(saTokenException.getMessage());
        } else {
            // 未知异常才打印
            e.printStackTrace();
            // 未知异常返回服务器异常（此处不可能执行进入，因为本方法处理的一定是SaToken的异常，此处仅为安全性考虑）
            commonResult = Respond.failError();
        }
        log.error(">>> {}，请求地址：{}", commonResult.getMessage(), SaHolder.getRequest().getUrl());
        return commonResult;
    }
}
