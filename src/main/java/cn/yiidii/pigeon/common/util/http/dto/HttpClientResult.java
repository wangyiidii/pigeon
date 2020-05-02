package cn.yiidii.pigeon.common.util.http.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Desc: 封装httpClient响应结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HttpClientResult {

    private int code;//响应状态码

    private String content;//响应数据

}
