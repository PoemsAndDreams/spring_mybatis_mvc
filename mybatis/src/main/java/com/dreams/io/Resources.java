package com.dreams.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author PoemsAndDreams
 * @description Resources类
 */
public class Resources {

    /**
     * 将配置文件加载为字节流
     * @param resource
     * @return
     * @throws IOException
     */
    public static InputStream getResourceAsStream(String resource) throws IOException {
        // 使用当前类的 ClassLoader 来加载资源文件
        ClassLoader classLoader = Resources.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(resource);

        if (inputStream == null) {
            // 如果资源文件不存在，则抛出 IOException
            throw new IOException("Resource not found: " + resource);
        }

        return inputStream;
    }
}
