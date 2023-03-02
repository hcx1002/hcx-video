package vip.huancaixi.video.module.user.service.impl;

import vip.huancaixi.video.module.user.entity.User;
import vip.huancaixi.video.module.user.mapper.UserMapper;
import vip.huancaixi.video.module.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hcx
 * @since 2023-02-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
