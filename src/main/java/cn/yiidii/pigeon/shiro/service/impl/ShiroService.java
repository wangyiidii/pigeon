package cn.yiidii.pigeon.shiro.service.impl;

import cn.yiidii.pigeon.shiro.service.IShiroService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ShiroService implements IShiroService {

    public Map<String, String> loadFilterChains() {
        Map<String, String> filterChains = new LinkedHashMap<>();
        //filterChains.put("/third/**", "anon");
//        filterChains.put("/**", "anon");
        filterChains.put("/common/**", "anon");
        filterChains.put("/optlog", "anon");
        filterChains.put("/login", "anon");
        filterChains.put("/reg", "anon");
        filterChains.put("/activeAccount", "anon");
        //放行swagger
        filterChains.put("/swagger-ui.html", "anon");
        filterChains.put("/swagger-resources", "anon");
        filterChains.put("/swagger-resources/configuration/security", "anon");
        filterChains.put("/swagger-resources/configuration/ui", "anon");
        filterChains.put("/webjars/springfox-swagger-ui/**", "anon");
        //knife4j
        filterChains.put("/doc.html", "anon");
        filterChains.put("/swagger-resources", "anon");
        filterChains.put("/v2/api-docs", "anon");
        filterChains.put("/v2/api-docs-ext", "anon");
        filterChains.put("/webjars/**", "anon");

        // 所有请求通过我们自己的JWT Filter
        filterChains.put("/**", "jwt");

        return filterChains;
    }

    public void reloadFilterChains() {
    }

}
