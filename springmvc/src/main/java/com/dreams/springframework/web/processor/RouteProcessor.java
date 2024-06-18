package com.dreams.springframework.web.processor;

import com.dreams.springframework.web.servlet.HandlerAdapter;
import com.dreams.springframework.web.servlet.HandlerMapping;
import org.thymeleaf.TemplateEngine;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author PoemsAndDreams
 * @description //路径处理器
 */
public class RouteProcessor implements Processor {

    private TemplateEngine templateEngine;
    private Processor nextProcessor;

    public RouteProcessor(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public void setNextProcessor(Processor processor) {
        this.nextProcessor = processor;
    }


    public void handle(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 获取完整的请求URI
            String requestURI = request.getRequestURI();

            // 获取上下文路径
            String contextPath = request.getContextPath();

            // 去除上下文路径，得到相对路径
            if (requestURI.startsWith(contextPath)) {
                requestURI = requestURI.substring(contextPath.length());
            }
            requestURI = requestURI.endsWith("/")?requestURI.substring(0, requestURI.length() - 1):requestURI;

            ServletContext servletContext = request.getServletContext();
            Map<String, HandlerMapping> pathMethodMap = (Map<String, HandlerMapping>) servletContext.getAttribute("pathMethodMap");

            if (!pathMethodMap.containsKey(requestURI)) {
                // 返回404，not found
                response.setContentType("text/html;charset=utf-8");
                response.sendError(HttpServletResponse.SC_NOT_FOUND,"路径未定义异常：没有找到该路径！");

            }

            // 处理请求--处理器适配器
            HandlerAdapter handlerAdapter = new HandlerAdapter(templateEngine,pathMethodMap.get(requestURI), request, response);

            //执行
            handlerAdapter.execute();

            if (nextProcessor != null){
                this.nextProcessor.handle(request,response);
            }

        } catch (Exception e) {
            try {
                response.setContentType("text/html;charset=utf-8");
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"系统内部异常");
            } catch (IOException ex) {
                e.printStackTrace();
            }
            e.printStackTrace();
        }
    }

}
