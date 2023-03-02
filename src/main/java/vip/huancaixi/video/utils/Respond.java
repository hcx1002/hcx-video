package vip.huancaixi.video.utils;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Respond {
    private long code;
    private String message;
    private Object data;

    public static Respond success(){
        return new Respond(200, "操作成功", null);
    }
    public static Respond success(Object data){
        return new Respond(200, "操作成功", data);
    }
    public static Respond success(String message,Object data){
        return new Respond(200, message, data);
    }
    public static Respond fail(){
        return new Respond(400,"操作失败", null);
    }
    public static Respond failError(){
        return new Respond(500,"服务器错误", null);
    }
    public static Respond fail(String message){
        return new Respond(400,message, null);
    }
    public static Respond failSa(String message){
        return new Respond(401,message, null);
    }
    public static Respond fail(Long code,String message){
        return new Respond(code, message, null);
    }
}
