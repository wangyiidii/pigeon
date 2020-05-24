package cn.yiidii.pigeon.shiro.service.impl;

import cn.yiidii.pigeon.base.RabbitConstant;
import cn.yiidii.pigeon.base.exception.ServiceException;
import cn.yiidii.pigeon.base.vo.ResultCodeEnum;
import cn.yiidii.pigeon.common.mail.dto.MailBean;
import cn.yiidii.pigeon.common.util.server.ServerUtil;
import cn.yiidii.pigeon.shiro.controller.form.UserLoginForm;
import cn.yiidii.pigeon.shiro.controller.form.UserRegForm;
import cn.yiidii.pigeon.shiro.entity.User;
import cn.yiidii.pigeon.shiro.mapper.UserMapper;
import cn.yiidii.pigeon.shiro.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ServerUtil serverUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public List<User> queryAllUser() throws ServiceException {
        return userMapper.queryAllUser();
    }

    @Override
    public User queryUserById(Integer id) {
        return userMapper.queryUserById(id);
    }

    @Override
    public User login(UserLoginForm userLoginForm) {
        User user = new User();
        BeanUtils.copyProperties(userLoginForm, user);
        User userExist = userMapper.queryUserByUsername(user.getUsername());
        if (null != userExist && StringUtils.equals(userExist.getPassword(), user.getPassword())) {
            return userExist;
        }
        return null;
    }

    @Override
    public User queryUserByUsername(String username) throws ServiceException {
        return getUserByUsername(username);
    }

    @Override
    public Integer reg(User user) throws ServiceException {
        User exist = userMapper.queryUserByUsername(user.getUsername());
        if (null != exist) {
            throw new ServiceException(ResultCodeEnum.USERNAME_ALREADY_EXISTS.getMsg());
        }
        user.setEmailCode(UUID.randomUUID().toString());
        Integer row = userMapper.insert(user);
        if (row != 1) {
            throw new ServiceException(ResultCodeEnum.OPT_FAIL.getMsg());
        }
        MailBean mail = new MailBean(user.getUsername() + " - 激活邮件",
                "<a href=\"" + serverUtil.getUrl() + "/activeAccount?username=" + user.getUsername() + "&code=" + user.getEmailCode() + "\">点我激活" + user.getUsername() + "</a>",
                new String[]{user.getEmail()},
                null,
                null,
                null,
                2,
                null,
                null
        );
        rabbitTemplate.convertAndSend(RabbitConstant.EMAIL_EXCHANGE, RabbitConstant.EMAIL_ROUTING_KEY, mail);
        return row;
    }

    @Override
    public Integer reg(UserRegForm userRegForm) {
        User user = new User();
        BeanUtils.copyProperties(userRegForm, user);
        return reg(user);
    }

    @Override
    public Integer update(User user) {
        return userMapper.update(user);
    }

    @Override
    public Integer del(Integer id) {
        return userMapper.del(id);
    }

    @Override
    public boolean activeAccount(String username, String code) throws ServiceException {
        User exist = getUserByUsername(username);
        if (exist.isState()) {
            throw new ServiceException(ResultCodeEnum.USER_ALREADY_ACTIVED.getMsg());
        }
        if (StringUtils.equals(exist.getEmailCode(), code)) {
            exist.setState(true);
            userMapper.update(exist);
            return true;
        }
        return false;
    }

    private User getUserByUsername(String username) throws ServiceException {
        return userMapper.queryUserByUsername(username);
    }
}
