package com.logni.account.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;


@Slf4j
@Configuration
public class TokenInterceptor implements HandlerInterceptor {

    @Resource(name = "requestScopeTokenData")
    private UserData requestData;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        parseTokenCreateUserData(request);
        return true;
    }


    private void parseTokenCreateUserData(HttpServletRequest request){
        final String requestTokenHeader = request.getHeader("Authorization");

        String jwtToken = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                requestData.setUserId(jwtTokenUtil.getUsernameFromToken(jwtToken));
                requestData.setUserName(jwtTokenUtil.getUsernameFromToken(jwtToken));
                requestData.setWallet(jwtTokenUtil.getIdFromToken(jwtToken));
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                log.error("JWT Token has expired");
            }
        } else {
            log.info("JWT Token does not begin with Bearer String or No Token");
        }
    }


}