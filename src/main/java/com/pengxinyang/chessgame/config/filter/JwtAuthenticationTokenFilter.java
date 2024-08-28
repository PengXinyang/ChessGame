package com.pengxinyang.chessgame.config.filter;


import com.pengxinyang.chessgame.entity.User;
//import com.pengxinyang.chessgame.service.impl.user.UserDetailsImpl;
import com.pengxinyang.chessgame.utils.JsonWebTokenTool;
import com.pengxinyang.chessgame.utils.RedisTool;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter{

    @Autowired
    private JsonWebTokenTool jsonWebTokenTool;

    @Autowired
    private RedisTool redisTool;

    /**
     * token 认证过滤器，任何请求访问服务器都会先被这里拦截验证token合法性
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            // 通过开放接口过滤器后，如果没有可解析的token就放行
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);

        // 解析token
        boolean verifyToken = jsonWebTokenTool.verifyToken(token);
        if (!verifyToken) {
//            log.error("当前token已过期");
            response.addHeader("message", "not login"); // 设置响应头信息，给前端判断用
            response.setStatus(403);
//            throw new AuthenticationException("当前token已过期");
            return;
        }
        String userId = JsonWebTokenTool.getSubjectFromToken(token);
        String role = JsonWebTokenTool.getClaimFromToken(token, "role");

        // 从redis中获取用户信息
        User user = redisTool.getObject("security:" + role + ":" + userId, User.class);

        if (user == null) {
//            log.error("用户未登录");
            response.addHeader("message", "not login"); // 设置响应头信息，给前端判断用
            response.setStatus(403);
//            throw new AuthenticationException("用户未登录");
            return;
        }

        // 存入SecurityContextHolder，这里建议只供读取uid用，其中的状态等非静态数据可能不准，所以建议redis另外存值
        //UserDetailsImpl loginUser = new UserDetailsImpl(user);
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(loginUser, null, null);
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 放行
        filterChain.doFilter(request, response);
    }
}
