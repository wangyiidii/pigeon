package cn.yiidii.pigeon.agent.netty.util;

import cn.yiidii.pigeon.agent.netty.AgentConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
public class ScriptLoadUtil {

    public static byte[] getFileByte(String filename) throws FileNotFoundException, IOException {
        FileInputStream fis = getResFileStream(filename);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        fis.close();
        return data;
    }

    public static FileInputStream getResFileStream(String filename) throws FileNotFoundException {
        FileInputStream fis = null;
        File file = getResFile(filename);
        fis = new FileInputStream(file);
        return fis;
    }

    private static File getResFile(String filename) throws FileNotFoundException {
        filename = AgentConstant.SCRIPT_BASE + File.separator + filename;
        File file = new File(filename);
        if (!file.exists()) {
            file = new File(filename);
        }
        Resource resource = new FileSystemResource(file);
        if (!resource.exists()) {
            file = ResourceUtils.getFile("classpath:" + filename);
        }
        return file;
    }

    public static void main(String[] args) throws Exception {
        FileInputStream fis = getResFileStream("LinuxCPUSSHIndicator.sh");
        System.out.println();
    }

}
