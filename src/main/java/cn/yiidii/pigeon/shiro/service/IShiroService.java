package cn.yiidii.pigeon.shiro.service;

import java.util.Map;

public interface IShiroService {

    public Map<String, String> loadFilterChains();

    public void reloadFilterChains();


}
