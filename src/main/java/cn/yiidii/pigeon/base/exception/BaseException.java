package cn.yiidii.pigeon.base.exception;

import lombok.NoArgsConstructor;

/**
 * @author yiidii Wang
 * @desc This class is used to ...
 */
@NoArgsConstructor
public abstract class BaseException extends RuntimeException {

    public BaseException(String message) {
        super(message);
    }

}
