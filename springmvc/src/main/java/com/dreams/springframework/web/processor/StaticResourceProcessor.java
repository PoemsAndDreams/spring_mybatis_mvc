package com.dreams.springframework.web.processor;

import com.dreams.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author PoemsAndDreams
 * @description //静态资源处理器
 */
public class StaticResourceProcessor implements Processor {

    private Processor nextProcessor;

    private String configResources;


    public StaticResourceProcessor(String configResources) {
        this.configResources = configResources;
    }

    @Override
    public void setNextProcessor(Processor processor) {
        this.nextProcessor = processor;
    }


    public void handle(HttpServletRequest request, HttpServletResponse response) {
        try {
            //创建 SAXReader 对象
            SAXReader saxReader = new SAXReader();
            //获取class对象加载文件返回流
            InputStream resourceAsStream = StaticResourceProcessor.class.getClassLoader().getResourceAsStream(configResources);
            //获取document对象
            Document document = null;
            document = saxReader.read(resourceAsStream);
            //获取根节点
            Element rootElement = document.getRootElement();
            //获取扫描包
            Element element = rootElement.element("default-servlet-handler");

            if (element == null) {
                if (nextProcessor != null){
                    this.nextProcessor.handle(request,response);
                }
                return;
            }

            String requestURI = request.getRequestURI();
            boolean isStaticResource = requestURI.endsWith(".css") ||
                    requestURI.endsWith(".js") ||
                    requestURI.endsWith(".png") ||
                    requestURI.endsWith(".jpg");
            if (isStaticResource) {
                // 处理静态资源的逻辑
                // 交给tomcat默认servlet处理
                RequestDispatcher requestDispatcher = request.getServletContext().getNamedDispatcher("default");
                // 设置响应状态码
                response.setStatus(HttpServletResponse.SC_OK);
                if (requestURI.endsWith(".png") ) {
                    response.setContentType("image/png");
                }
                if (requestURI.endsWith(".jpg")){
                    response.setContentType("image/jpg");
                }
                requestDispatcher.forward(request,response);
            } else {
                // 处理其他类型的请求
                if (nextProcessor != null){
                    this.nextProcessor.handle(request,response);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
