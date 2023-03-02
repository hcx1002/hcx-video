package vip.huancaixi.video.common;

import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import vip.huancaixi.video.utils.Respond;

import java.util.List;

/**
 * 全局异常处理工具类，将异常转为通用结果
 *
 * @author xuyuxiang
 * @date 2021/12/18 16:44
 */
@Slf4j
public class GlobalExceptionUtil {

    /**
     * 根据错误类型获取对应的CommonResult
     *
     * @author xuyuxiang
     * @date 2021/10/11 15:52
     **/
    public static Respond getCommonResult(Exception e) {
        Respond commonResult = null;
        if (e instanceof HttpRequestMethodNotSupportedException) {
            // 如果是请求方法异常 405
            String method = CommonServletUtil.getRequest().getMethod();
            if (HttpMethod.GET.toString().equals(method)) {
                commonResult = Respond.fail("请求方法应为POST");
            } else if(HttpMethod.POST.toString().equals(method)) {
                commonResult = Respond.fail("请求方法应为GET" );
            } else {
                commonResult = Respond.fail("请求方法仅支持GET或POST");
            }
        } else if (e instanceof HttpMessageNotReadableException) {

            // 如果是参数传递格式不支持异常 415
            if (e.getMessage().contains("JSON parse error")) {
                e.printStackTrace();
                //JSON格式转换错误特殊提示
                commonResult = Respond.fail("参数格式错误");
            } else {
                commonResult = Respond.fail("请使用JSON方式传参");
            }
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            e.printStackTrace();
            // 如果是JSON参数格式错误异常 415
            commonResult = Respond.fail("参数格式错误");
        } else if (e instanceof MethodArgumentNotValidException) {

            // 如果是参数校验异常（MethodArgumentNotValidException） 415
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) e;
            commonResult = Respond.fail(getArgNotValidMessage(methodArgumentNotValidException.getBindingResult()));
        } else if (e instanceof BindException) {

            // 如果是参数校验异常（BindException） 415
            BindException bindException = (BindException) e;
            commonResult = Respond.fail( getArgNotValidMessage(bindException.getBindingResult()));
//        } else if (e instanceof ConstraintViolationException) {

            // 如果是参数校验异常（ConstraintViolationException） 415
//            ConstraintViolationException constraintViolationException = (ConstraintViolationException) e;
//            commonResult = Respond.fail(HttpStatus.HTTP_UNSUPPORTED_TYPE, getArgNotValidMessage(constraintViolationException.getConstraintViolations()), null);
        } else if (e instanceof MissingServletRequestParameterException) {

            // 如果是参数校验异常（MissingServletRequestParameterException） 415
            MissingServletRequestParameterException missingServletRequestParameterException = (MissingServletRequestParameterException) e;
            commonResult = Respond.fail(missingServletRequestParameterException.getMessage());
        }
        else if (e instanceof MultipartException) {

            //文件上传错误特殊提示
            commonResult = Respond.fail("请使用multipart/form-data方式上传文件");
        } else if (e instanceof MissingServletRequestPartException) {

            //文件上传错误特殊提示
            commonResult = Respond.fail("请选择要上传的文件并检查文件参数名称是否正确");
        } else if (e instanceof SaTokenException) {

            // 如果是SaToken相关异常，则由AuthExceptionUtil处理
            return AuthExceptionUtil.getCommonResult(e);
        } else if(e instanceof MyBatisSystemException) {

            // 如果是MyBatisSystemException
            Throwable cause = e.getCause();
            if (cause instanceof PersistenceException) {
                Throwable secondCause = cause.getCause();
                if (secondCause instanceof CommonException) {
                    CommonException commonException = (CommonException) secondCause;
                    commonResult = Respond.fail(commonException.getMsg());
                } else {
                    e.printStackTrace();
                    commonResult = Respond.failError();
                }
            } else {
                e.printStackTrace();
                commonResult = Respond.failError();
            }
        } else if (e instanceof CommonException) {

            // 通用业务异常，直接返回给前端
            CommonException commonException = (CommonException) e;
            commonResult = Respond.fail( commonException.getMsg());
        }  else {
            // 未知异常打印详情
            e.printStackTrace();

            // 未知异常返回服务器异常
            commonResult = Respond.failError();
        }
        log.error(">>> {}，请求地址：{}", commonResult.getMessage(), CommonServletUtil.getRequest().getRequestURL());
        return commonResult;
    }

    /**
     * 获取请求参数不正确的提示信息，多个信息，拼接成用逗号分隔的形式
     *
     * @author xuyuxiang
     * @date 2021/10/12 11:14
     **/
    public static String getArgNotValidMessage(BindingResult bindingResult) {
        if (ObjectUtil.isNull(bindingResult)) {
            return "";
        }
        StringBuilder stringBuilder = StrUtil.builder();

        // 多个错误用逗号分隔
        List<ObjectError> allErrorInfos = bindingResult.getAllErrors();
        for (ObjectError error : allErrorInfos) {
            stringBuilder.append(StrUtil.COMMA).append(error.getDefaultMessage());
        }

        // 最终把首部的逗号去掉
        return StrUtil.removePrefix(stringBuilder.toString(), StrUtil.COMMA);
    }
}
