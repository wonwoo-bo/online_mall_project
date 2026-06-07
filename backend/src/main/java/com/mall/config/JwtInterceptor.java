package com.mall.config;

import com.mall.common.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        
        if (token == null || !token.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":401,\"message\":\"未登录\",\"data\":null}");
            writer.flush();
            writer.close();
            return false;
        }

        token = token.substring(7);
        
        if (!JwtUtil.validateToken(token)) {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":401,\"message\":\"token失效\",\"data\":null}");
            writer.flush();
            writer.close();
            return false;
        }

        Integer userId = JwtUtil.getUserIdFromToken(token);
        String username = JwtUtil.getUsernameFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        
        return true;
    }
}
