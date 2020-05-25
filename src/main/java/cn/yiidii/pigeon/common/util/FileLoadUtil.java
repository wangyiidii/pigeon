package cn.yiidii.pigeon.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Slf4j
public class FileLoadUtil {
    private static File getResFile(String filename) throws FileNotFoundException {
        File file = new File(filename);
        if (!file.exists()) { // 如果同级目录没有，则去config下面找
            log.debug("不在同级目录，进入config目录查找");
            file = new File("config/" + filename);
        }
        Resource resource = new FileSystemResource(file);
        if (!resource.exists()) { //config目录下还是找不到，那就直接用classpath下的
            log.debug("不在config目录，进入classpath目录查找");
            file = ResourceUtils.getFile("classpath:" + filename);
        }
        return file;
    }

    /**
     * 通过文件名获取classpath路径下的文件流
     *
     * @param
     * @return
     * @throws
     */
    private static FileInputStream getResFileStream(String filename) throws FileNotFoundException {
        FileInputStream fin = null;
        File file = getResFile(filename);
        log.info("getResFile path={}", file);
        fin = new FileInputStream(file);
        return fin;
    }
}
