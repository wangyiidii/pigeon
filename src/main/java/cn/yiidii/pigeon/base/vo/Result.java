package cn.yiidii.pigeon.base.vo;

import lombok.Data;

/**
 * @author yiidii Wang
 * @desc This class is used to ...
 */
@Data
public class Result<T> {

    private T data;
    private String code = "000000";
    private boolean success = true;
    private String message = "";

    public static <T> Result<T> success(T result) {
        Result<T> response = new Result<>();
        if (result instanceof String) {
            response.message = String.valueOf(result);
        } else {
            response.data = result;
        }
        return response;
    }

    public static <T> Result<T> success() {
        return new Result<>();
    }

    public static Result error() {
        Result result = new Result<>();
        result.code = ResultCodeEnum.SERVER_ERROR.getCode();
        result.message = ResultCodeEnum.SERVER_ERROR.getMsg();
        result.success = false;
        return result;
    }


    public static <T> Result<T> error(String code, String message) {
        Result<T> result = new Result<>();
        result.success = false;
        result.code = code;
        result.message = message;
        return result;
    }

    public static <T> Result<T> error(ResultCodeEnum code, String message) {
        Result<T> result = new Result<>();
        result.success = false;
        result.code = code.getCode();
        result.message = message;
        return result;
    }

    public static <T> Result<T> error(ResultCodeEnum code, T result) {
        Result<T> response = new Result<>();
        response.success = false;
        response.code = code.getCode();
        response.data = result;
        return response;
    }

    public static <T> Result<T> error(ResultCodeEnum code) {
        Result<T> result = new Result<>();
        result.success = false;
        result.code = code.getCode();
        return result;
    }

}
