package cn.yiidii.pigeon.lovebook.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author yiidii
 * @date 2020/3/26 23:13:08
 * @desc This class is used to ...
 */
@Data
@ToString
public class LoveBook {
    private int id;
    private String author;
    private String content;
    private Date createTime;
    private String host;
    private String ua;
}
