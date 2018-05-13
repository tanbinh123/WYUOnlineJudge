package com.zoctan.api.core.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证请求的Token
 *
 * @author Zoctan
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Resource
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        // 解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Content-Length, Authorization, Accept, X-Requested-With");
        // 明确允许通过的方法，不建议使用*
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Expose-Headers", "*");
        // axios 预请求后，直接返回
        // 返回码必须为 200 否则视为请求失败
        if ("OPTIONS".equals(request.getMethod())) {
            return;
        }

        final String token = this.jwtUtil.getTokenFromRequest(request);
        if (token == null) {
            this.log.info("Anonymous request URL<{}> Method<{}>", request.getRequestURL(), request.getMethod());
        } else {
            final String username = this.jwtUtil.getUsername(token);
            this.log.info("JwtFilter => user<{}> token : {}", username, token);
            this.log.info("request URL<{}> Method<{}>", request.getRequestURL(), request.getMethod());

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (this.jwtUtil.validateToken(token)) {
                    final UsernamePasswordAuthenticationToken authentication = this.jwtUtil.getAuthentication(username, token);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    this.log.info("JwtFilter => user<{}> is authorized, set security context", username);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
