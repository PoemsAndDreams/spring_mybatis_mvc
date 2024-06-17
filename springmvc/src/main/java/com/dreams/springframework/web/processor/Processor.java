package com.dreams.springframework.web.processor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author PoemsAndDreams
 * @description //处理器
 */
public interface Processor {
    void setNextProcessor(Processor processor);

    void handle(HttpServletRequest request, HttpServletResponse response);

}
