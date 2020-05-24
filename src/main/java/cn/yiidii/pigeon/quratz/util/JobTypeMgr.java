package cn.yiidii.pigeon.quratz.util;

import cn.yiidii.pigeon.quratz.entity.JobParam;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class JobTypeMgr {

    private static Map<Integer, JobType> jobTypeMap = new ConcurrentHashMap<>();

    static {
        loadJobTypes();
    }

    public static void main(String[] args) {
        System.out.println(JobTypeMgr.getInstance());
    }

    public static Map<Integer, JobType> getInstance() {
        return jobTypeMap;
    }

    public synchronized static void loadJobTypes() {
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
            JobType jobType = parseJobTypes(type);
            //params
            List<Element> paramEles = type.elements("param");
            List<JobParam> params = new ArrayList<>();
            paramEles.forEach(paramEle -> {
                JobParam param = parseParam(paramEle);
                params.add(param);
            });
            jobType.setJobParams(params);
            jobTypeMap.put(jobType.getType(), jobType);
        });
    }

    private static JobType parseJobTypes(Element paramEle) {
        try {
            JobType jobType = new JobType();
            BeanUtils.populate(jobType, attr2Map(paramEle));
            return jobType;
        } catch (Exception e) {
        }
        return null;
    }

    private static JobParam parseParam(Element paramEle) {
        try {
            JobParam param = new JobParam();
            BeanUtils.populate(param, attr2Map(paramEle));
            return param;
        } catch (Exception e) {
        }
        return null;
    }

    private static Map<String, String> attr2Map(Element e) {
        List<Attribute> attributes = e.attributes();
        Map<String, String> map = new HashMap<>();
        attributes.forEach(attribute -> {
            map.put(attribute.getName(), attribute.getValue());
        });
        return map;
    }

}
