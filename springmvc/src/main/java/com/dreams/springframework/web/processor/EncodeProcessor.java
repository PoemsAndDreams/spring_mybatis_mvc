package com.dreams.springframework.web.processor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author PoemsAndDreams
 * @description //静态资源处理器
 */
public class EncodeProcessor implements Processor {

    private Processor nextProcessor;

    private String encodeConfig;

    public EncodeProcessor(String encodeConfig) {
        this.encodeConfig = encodeConfig;
    }

    @Override
    public void setNextProcessor(Processor processor) {
        this.nextProcessor = processor;
    }


    public void handle(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 设置编码
            request.setCharacterEncoding(encodeConfig);
            // 设置响应码
            response.setContentType("text/html;charset=" + encodeConfig);

            if (nextProcessor != null){
                this.nextProcessor.handle(request,response);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
