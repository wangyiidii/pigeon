package cn.yiidii.pigeon.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtil {

    public static byte[] gzip(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data);
        gzip.finish();
        gzip.close();
        byte[] ret = bos.toByteArray();
        bos.close();
        return ret;
    }

    public static byte[] ungzip(byte[] data) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        GZIPInputStream gzip = new GZIPInputStream(bis);
        byte[] buf = new byte[1024];
        int num = -1;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((num = gzip.read(buf, 0, buf.length)) != -1) {
            bos.write(buf, 0, num);
        }
        gzip.close();
        bis.close();
        byte[] ret = bos.toByteArray();
        bos.flush();
        bos.close();
        return ret;
    }

}
