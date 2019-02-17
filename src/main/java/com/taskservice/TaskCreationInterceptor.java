package com.taskservice;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TaskCreationInterceptor extends HandlerInterceptorAdapter {

    private final TaskService service;

    public TaskCreationInterceptor(TaskService service) {
        this.service = service;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (HttpMethod.POST.matches(request.getMethod())) {
            service.updateTaskStatus();
        }
    }
}
