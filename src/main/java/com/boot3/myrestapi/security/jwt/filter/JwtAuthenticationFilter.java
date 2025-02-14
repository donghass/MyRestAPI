package com.boot3.myrestapi.security.jwt.filter;

import com.boot3.myrestapi.security.jwt.service.JwtService;
import com.boot3.myrestapi.security.userInfo.UserInfoUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserInfoUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //  요청 해더 중에서 Authorization 해더 값을 가져온다
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Authorization 해더가 있다면 "Bearer eyJhbGciOiJIUzI1NiJ9" 에서 앞에 7자 제외한 토큰값 추출
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);   // 토큰을 디코딩하여 username(이메일) 추출, 여기서는 username 를 email로 설정해놓았음
        }

        // SecurityContect 에 저장된 Authentication 객체가 null 이면 username 넣어준다
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Entity(테이블) 에 저장된 인증 정보를 UserDetails 로 리턴해준다.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // SecurityContect 에 Authentication 를 저장
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}