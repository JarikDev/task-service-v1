package com;


import com.taskservice.TaskCreationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebApplicationConfig implements WebMvcConfigurer {

    private final TaskCreationInterceptor taskCreationInterceptor;

    public WebApplicationConfig(TaskCreationInterceptor taskCreationInterceptor) {
        this.taskCreationInterceptor = taskCreationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(taskCreationInterceptor).addPathPatterns("/task");
    }
}