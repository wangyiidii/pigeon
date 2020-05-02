package cn.yiidii.pigeon.shiro.service;


import cn.yiidii.pigeon.shiro.controller.form.UserLoginForm;
import cn.yiidii.pigeon.shiro.controller.form.UserRegForm;
import cn.yiidii.pigeon.shiro.entity.User;

import java.util.List;

public interface IUserService {
    List<User> queryAllUser();

    User queryUserById(Integer id);

    User login(UserLoginForm userLoginForm);

    User queryUserByUsername(String username);

    Integer reg(User user);

    Integer reg(UserRegForm userRegForm);

    Integer update(User user);

    Integer del(Integer id);

    boolean activeAccount(String username, String code);
}
