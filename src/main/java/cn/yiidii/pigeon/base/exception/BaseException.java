package cn.yiidii.pigeon.base.exception;

import cn.yiidii.pigeon.base.vo.ResultCodeEnum;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class BaseException extends RuntimeException {

    public BaseException(String message) {
        super(message);
    }

}
