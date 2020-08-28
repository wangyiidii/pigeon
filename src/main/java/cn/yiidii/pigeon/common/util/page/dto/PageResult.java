package cn.yiidii.pigeon.common.util.page.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author yiidii
 * @date 2020/3/26 22:51:19
 * @desc 分页封装对象
 */
@Data
@AllArgsConstructor
public class PageResult<T> {
    private Long total = 0L;
    private Integer current = 1;
    private Integer pageCount = 0;
    private List<T> list;
}
