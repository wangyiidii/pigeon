package cn.yiidii.pigeon.quratz.util;

import cn.yiidii.pigeon.quratz.entity.JobType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class JobTypeMgr {

    private static Set<JobType> jobTypes = new CopyOnWriteArraySet<>();

    static {
        loadJobTypes();
    }

    public static void main(String[] args) {
        System.out.println(JobTypeMgr.getInstance());
    }

    public static Set<JobType> getInstance() {
        return jobTypes;
    }

    public static void loadJobTypes() {
        Document document = null;
        try {
            File file = new File("quartz/JobTypes.xml"); // 从jar同级目录加载
            Resource resource = new FileSystemResource(file);
            if (!resource.exists()) { //classpath下
                DefaultResourceLoader loader = new DefaultResourceLoader();
                resource = loader.getResource("quartz/JobTypes.xml");
            }

            SAXReader reader = new SAXReader();
            document = reader.read(resource.getInputStream());
        } catch (Exception ex) {
            log.error("加载JobTypes.xml异常: " + ex.toString());
            return;
        }
        Element root = document.getRootElement();
        List<Element> types = root.elements("type");
        types.forEach(type -> {
            List<Attribute> attrs = type.attributes();
            Map<String, Object> map = new HashMap<>();
            attrs.forEach(attr -> {
                map.put(attr.getName(), attr.getValue().trim());
            });
            JobType jobType = new JobType();
            try {
                BeanUtils.populate(jobType, map);
                jobTypes.add(jobType);
            } catch (Exception e) {
                log.error("转换jobType异常: " + e.toString());
            }
        });
    }

}
