package com.itzkoictu.gotNow.security.config;

import com.itzkoictu.gotNow.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    @Autowired
    private  JwtUtils jwtUtils;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if(request instanceof ServletServerHttpRequest){
            HttpServletRequest servletRequest= ((ServletServerHttpRequest) request).getServletRequest();
            String token = servletRequest.getHeader("Authorization");

            if(token !=null && token.startsWith("Bearer ")){
                token= token.substring(7);
                Long userId = jwtUtils.getUserIdFromToken(token);
                List<String> roles= jwtUtils.getRolesFromToken(token);

                attributes.put("userId", userId);
                attributes.put("roles", roles);
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
