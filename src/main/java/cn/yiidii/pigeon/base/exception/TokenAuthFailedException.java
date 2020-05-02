package cn.yiidii.pigeon.base.exception;

import lombok.NoArgsConstructor;
import org.apache.shiro.authc.AuthenticationException;

@NoArgsConstructor
public class TokenAuthFailedException extends AuthenticationException {

    public TokenAuthFailedException(String message) {
        super(message);
    }


}