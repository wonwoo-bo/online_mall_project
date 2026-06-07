package com.mall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 需要登录的接口
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns(
                    // 用户模块需要登录的接口
                    "/api/user/info", "/api/user/info/**", 
                    "/api/user/password", "/api/user/address/**",
                    "/api/user/member", "/api/user/member/**",
                    "/api/user/role", "/api/user/role/**",
                    // 购物车订单模块
                    "/api/cart/**", 
                    "/api/order/**", 
                    "/api/pay/**",
                    // 商品模块需要登录的接口
                    "/api/favorites/**", 
                    "/api/history/**", 
                    "/api/returns/**",
                    // 管理员模块需要登录的接口
                    "/api/admin/info", "/api/admin/info/**",
                    "/api/admin/password", "/api/admin/list",
                    "/api/admin/add", "/api/admin/**/status",
                    "/api/admin/merchants/**", "/api/admin/configs/**",
                    // 商家模块需要登录的接口
                    "/api/merchant/info", "/api/merchant/info/**",
                    "/api/merchant/account/**",
                    "/api/merchant/dashboard", "/api/merchant/products/**",
                    "/api/merchant/orders/**", "/api/merchant/reviews/**",
                    "/api/merchant/promotions/**", "/api/merchant/coupons/**",
                    "/api/merchant/brand/**", "/api/merchant/brands/**",
                    "/api/merchant/spec/**", "/api/merchant/specs/**",
                    "/api/merchant/returns/**", "/api/merchant/refunds/**", 
                    "/api/merchant/recycle/**",
                    "/api/merchant/statistics/**", "/api/merchant/order-center/**",
                    "/api/merchant/advanced/**"
                )
                .excludePathPatterns(
                    // 公开接口
                    "/api/user/register", 
                    "/api/user/login",
                    "/api/user/member/types",
                    "/api/products/**", 
                    "/api/categories/**",
                    "/api/reviews",
                    "/api/reviews/**",
                    // 管理员登录接口
                    "/api/admin/login",
                    // 商家登录注册接口
                    "/api/merchant/register",
                    "/api/merchant/login",
                    // 文件上传接口
                    "/api/upload",
                    "/api/uploads/**"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get("uploads").toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
