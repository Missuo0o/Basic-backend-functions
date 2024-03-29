package com.missuo.server.interceptor;

import com.missuo.common.constant.JwtClaimsConstant;
import com.missuo.common.constant.MessageConstant;
import com.missuo.common.context.BaseContext;
import com.missuo.common.exception.UserNotLoginException;
import com.missuo.common.properties.JwtProperties;
import com.missuo.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtTokenUserInterceptor implements HandlerInterceptor {

  @Autowired private JwtProperties jwtProperties;
  @Autowired private JwtUtil jwtUtil;
  @Autowired private RedisTemplate<Object, Object> redisTemplate;

  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    // Determine whether the currently intercepted method is the controller's method or other
    // resources
    if (!(handler instanceof HandlerMethod)) {
      return true;
    }

    String token = request.getHeader(jwtProperties.getUserTokenName());

    try {
      Claims claims = jwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
      Long userID = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
      Long id = (Long) redisTemplate.opsForValue().get("User_id" + userID);
      if (id == null) {
        throw new UserNotLoginException(MessageConstant.USER_NOT_LOGIN);
      } else {
        BaseContext.setCurrentId(userID);
        return true;
      }

    } catch (Exception ex) {
      //            response.setStatus(401);
      //            return false;
      throw new UserNotLoginException(MessageConstant.USER_NOT_LOGIN);
    }
  }
}
