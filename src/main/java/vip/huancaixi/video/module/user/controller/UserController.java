package vip.huancaixi.video.module.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import vip.huancaixi.video.module.user.entity.User;
import vip.huancaixi.video.module.user.service.IUserService;
import vip.huancaixi.video.utils.Respond;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hcx
 * @since 2023-02-25
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private IUserService userService;
    @PostMapping("login")
    public Respond getUser(@RequestBody User user){
        if (ObjectUtil.isEmpty(user)||ObjectUtil.isEmpty(user.getUsername())||ObjectUtil.isEmpty(user.getPassword())){
            return Respond.fail("不能为空");
        }
        User one = userService.getOne(new QueryWrapper<User>().lambda().eq(User::getUsername, user.getUsername()).eq(User::getPassword, user.getPassword()).last("limit 1"));
        if (ObjectUtil.isNotEmpty(one)){
            StpUtil.login(one.getId());
            StpUtil.getTokenSession().set("loginUser", one);
            return Respond.success(StpUtil.getTokenInfo().getTokenValue());
        }
        return Respond.fail("账号或密码错误");
    }
    @PostMapping("regis")
    public Respond regis(@RequestBody User user){
        if (ObjectUtil.isEmpty(user)||ObjectUtil.isEmpty(user.getUsername())||ObjectUtil.isEmpty(user.getPassword())){
            return Respond.fail("不能为空");
        }
        User user1 = new User();
        user1.setUsername(user.getUsername());
        user1.setPassword(user.getPassword());
        user1.setPermission("upload");  // 默认拥有上传权限
        user1.setVip(1);
        User one = userService.getOne(new QueryWrapper<User>().lambda().eq(User::getUsername, user.getUsername()).last("limit 1"));
        if (ObjectUtil.isNotEmpty(one)) return Respond.fail("用户名已存在！");
        String body = HttpRequest.get("https://api.uomg.com/api/rand.avatar?sort=%E7%94%B7&format=json").execute().body();
        JSONObject object = JSONUtil.parseObj(body);
        user1.setAvatar(object.getStr("imgurl")); //设置从网络获取的头像
        userService.save(user1);
        StpUtil.login(user1.getId());
        StpUtil.getTokenSession().set("loginUser", user1); //保存到session
        return Respond.success(StpUtil.getTokenInfo().getTokenValue());

    }
    @GetMapping("logout")
    public Respond logout(){
        if (!StpUtil.isLogin()) return Respond.fail("未登录");
        StpUtil.logout();
        return Respond.success("退出成功");
    }
    @GetMapping("info")
    public Respond getUserInfo(){
        String loginIdAsString = StpUtil.getLoginIdAsString();
        if (ObjectUtil.isEmpty(loginIdAsString)) return Respond.fail("未登录");
        User byId = userService.getById(loginIdAsString);
        if (ObjectUtil.isEmpty(byId)) return Respond.fail("用户信息为空");
        return Respond.success(byId);
    }
}
