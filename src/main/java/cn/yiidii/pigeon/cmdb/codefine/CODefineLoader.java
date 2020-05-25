package cn.yiidii.pigeon.cmdb.codefine;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CODefineLoader {

    private static Map<String, CODefine> coDefineMap = new ConcurrentHashMap<>();
    private static Map<String, IndicatorDefine> indicatorDefineMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        init();
    }

    public static void init() {
        File file = new File(COConstant.CO_DEFINE);
        try {
            Resource resource = new FileSystemResource(file);
            if (!resource.exists()) { //classpath下
                DefaultResourceLoader loader = new DefaultResourceLoader();
                resource = loader.getResource(COConstant.CO_DEFINE);
            }
            file = resource.getFile();
        } catch (IOException e) {
            log.error("{} is not a correct codefine dir.", COConstant.CO_DEFINE);
            return;
        }
        File[] defineFileArr = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".xml")) {
                    return true;
                }
                return false;
            }
        });

        if (!Objects.isNull(defineFileArr) && defineFileArr.length > 0) {
            for (File defineFile : defineFileArr) {
                loadCODefine(defineFile);
            }
        }
        handle();
        DefineProxy.setCODefineMap(coDefineMap);
        DefineProxy.setIndDefineMap(indicatorDefineMap);
    }

    private static void loadCODefine(File f) {
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(f);
        } catch (DocumentException e) {
            log.error("load {} exception: {}", f.getName(), e.toString());
            return;
        }
        Element root = document.getRootElement();
        //res
        List<Element> defines = root.elements(COConstant.CO_DEFINE);
        defines.forEach(define -> {
            CODefine coDefine = parseResDefine(define);
            if (!Objects.isNull(coDefine))
                coDefineMap.put(coDefine.getName(), coDefine);
        });
        //ind
        List<Element> inds = root.elements(COConstant.CO_IND);
        inds.forEach(ind -> {
            IndicatorDefine indicatorDefine = parseIndicator(ind);
            if (!Objects.isNull(indicatorDefine)) ;

        });
    }

    private static CODefine parseResDefine(Element defineEle) {
        try {
            CODefine coDefine = new CODefine();
            //attrs
            BeanUtils.populate(coDefine, attr2Map(defineEle));
            //params
            List<Element> paramEles = defineEle.elements(COConstant.CO_PARAM);
            paramEles.forEach(paramEle -> {
                ParamDefine param = parseParam(paramEle);
                coDefine.addParam(param);
            });
            return coDefine;
        } catch (Exception e) {

        }
        return null;
    }

    private static IndicatorDefine parseIndicator(Element indEle) {
        try {
            IndicatorDefine indicatorDefine = new IndicatorDefine();
            //attrs
            BeanUtils.populate(indicatorDefine, attr2Map(indEle));
            //params
            List<Element> paramEles = indEle.elements(COConstant.CO_PARAM);
            paramEles.forEach(paramEle -> {
                ParamDefine param = parseParam(paramEle);
                indicatorDefine.addParam(param);
            });

            //metrics
            List<Element> metricEles = indEle.elements(COConstant.CO_METRIC);
            metricEles.forEach(metricEle -> {
                MetricDefine metric = parseMetric(metricEle);
                indicatorDefine.addMetric(metric);
            });

            //insert
            indicatorDefineMap.put(indicatorDefine.getName(), indicatorDefine);
        } catch (Exception e) {
        }
        return null;
    }

    private static ParamDefine parseParam(Element paramEle) {
        try {
            ParamDefine param = new ParamDefine();
            BeanUtils.populate(param, attr2Map(paramEle));
            return param;
        } catch (Exception e) {
        }
        return null;
    }

    private static MetricDefine parseMetric(Element metricEle) {
        try {
            MetricDefine metric = new MetricDefine();
            BeanUtils.populate(metric, attr2Map(metricEle));
            return metric;
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

    private static void handle() {
        Map<String, CODefine> coDefineMapTemp = new ConcurrentHashMap<>(coDefineMap);
        for (Map.Entry<String, CODefine> entry : coDefineMap.entrySet()) {
            CODefine coDefine = entry.getValue();
            String extend = coDefine.getExtend();
            if (coDefineMapTemp.containsKey(extend)) {
                coDefine.getParams().putAll(coDefineMapTemp.get(extend).getParams());
            }
        }
        Map<String, IndicatorDefine> indicatorDefineMapTemp = new HashMap<>(indicatorDefineMap);
        Iterator<IndicatorDefine> it = indicatorDefineMap.values().iterator();
        while (it.hasNext()) {
            IndicatorDefine indicatorDefine = it.next();
            String ref = indicatorDefine.getRef();
            String extend = indicatorDefine.getExtend();
            if (StringUtils.isNotBlank(extend) && indicatorDefineMapTemp.containsKey(extend)) {
                indicatorDefine.getParams().putAll(indicatorDefineMapTemp.get(extend).getParams());
            }
            System.out.println(coDefineMap == null);

            if (StringUtils.isNotBlank(ref) && coDefineMap.containsKey(ref)) {
                coDefineMap.get(ref).addIndicator(indicatorDefine);
            }
        }
        coDefineMapTemp.clear();
        indicatorDefineMapTemp.clear();
    }
}