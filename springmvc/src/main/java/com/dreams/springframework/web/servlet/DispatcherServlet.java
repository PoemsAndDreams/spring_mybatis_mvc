package com.dreams.springframework.web.servlet;

import com.dreams.springframework.web.processor.EncodeProcessor;
import com.dreams.springframework.web.processor.RouteProcessor;
import com.dreams.springframework.web.processor.StaticResourceProcessor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author PoemsAndDreams
 * @description //前端控制器
 */
public class DispatcherServlet extends HttpServlet {
    private TemplateEngine templateEngine;

    private String contextConfigLocation;

    private String encoding;


    @Override
    public void init() throws ServletException {
        System.out.println("初始化开始！");
        this.contextConfigLocation = this.getServletConfig().getInitParameter("contextConfigLocation");
        this.encoding = this.getServletConfig().getInitParameter("encoding");

        if (contextConfigLocation == null || contextConfigLocation.isEmpty()) {
            System.err.println("contextConfigLocation 未配置或为空");
            return;
        }

        if (contextConfigLocation.contains("classpath:")) {
            contextConfigLocation = contextConfigLocation.replace("classpath:","")   ;
        }

        ServletContext context = this.getServletConfig().getServletContext();
        // 配置 Thymeleaf 模板解析器
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
        templateResolver.setPrefix("/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");

        // 创建 Thymeleaf 模板引擎
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("处理用户请求");
        EncodeProcessor encodeProcessor = new EncodeProcessor(this.encoding);
        StaticResourceProcessor staticResourceProcessor = new StaticResourceProcessor(this.contextConfigLocation);

        RouteProcessor routeProcessor = new RouteProcessor(templateEngine);
        encodeProcessor.setNextProcessor(staticResourceProcessor);
        // 加入路径处理器
        staticResourceProcessor.setNextProcessor(routeProcessor);
        encodeProcessor.handle(request,response);
    }
}
