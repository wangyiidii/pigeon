package cn.yiidii.pigeon.base.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yiidii
 * @desc This class is used to ...
 */
@AllArgsConstructor
@Getter
public enum ResultCodeEnum {
    //
    SUCCESS("000000", "成功"),
    //
    OPT_FAIL("000001", "操作失败"),
    //
    DELETE_FAIL("000002", "删除失败"),
    //
    UPDATE_FAIL("000003", "更新失败"),
    //
    SERVER_ERROR("000005", "服务器异常"),
    //
    ILLEGAL_ARGUMENT_ERROR("000006", "不合法参数异常"),

    //
    USERNAME_ALREADY_EXISTS("100001", "用户名已存在"),
    //
    USER_NOT_EXIST("100002", "用户不存在"),
    //
    USER_ALREADY_ACTIVED("100003", "用户已激活"),
    //
    INCORRECT_USERNAME_OR_PASSWORD("100004", "用户名或密码错误"),
    //
    TOKEN_EXPIRED("100005", "Token无效"),
    //
    DISABLEDACCOUNT("100006", "账户状态异常"),
    //
    NO_PERMISSION("100007", "没有权限");

    private String code;
    private String msg;
}
