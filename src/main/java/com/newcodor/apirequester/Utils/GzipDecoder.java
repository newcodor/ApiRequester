package com.newcodor.apirequester.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class GzipDecoder {

    public static String decompressGzip(InputStream gzipInputStream) throws IOException {
        try (GZIPInputStream gis = new GZIPInputStream(gzipInputStream);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int len;
            // 读取解压后的数据并写入字节数组输出流
            while ((len = gis.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            // 将字节数组转换为字符串（使用UTF-8编码）
            return baos.toString(StandardCharsets.UTF_8.name());
        }
    }

    public static void main(String[] args) {
        // 示例：假设 responseInputStream 是来自HTTP响应的GZIP流
        try (InputStream responseInputStream = getGzipResponseStream()) {
            String decompressedData = decompressGzip(responseInputStream);
            System.out.println("解压后的内容: " + decompressedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 模拟获取GZIP响应流的方法（实际中替换为真实HTTP连接的输入流）
    private static InputStream getGzipResponseStream() {
        // 示例返回一个空的GZIP流，实际应从HTTP连接获取
        return new InputStream() {
            @Override
            public int read() {
                return -1; // 示例代码，实际需要真实数据
            }
        };
    }
}