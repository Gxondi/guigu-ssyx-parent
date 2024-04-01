package com.atguigu.ssyx.common.auth;

import com.atguigu.ssyx.common.constant.RedisConst;
import com.atguigu.ssyx.common.utils.JwtHelper;
import com.atguigu.ssyx.vo.user.LeaderAddressVo;
import com.atguigu.ssyx.vo.user.UserLoginVo;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserLoginInterceptor implements HandlerInterceptor {
    private RedisTemplate redisTemplate;
    UserLoginInterceptor(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.getLoginUserInfo(request);
        return true;
    }

    private void getLoginUserInfo(HttpServletRequest request) {
        //从请求头中获取
        String token = request.getHeader("token");
        if(!StringUtils.isEmpty(token)){
            Long userId = JwtHelper.getUserId(token);
            //从redis获取用户信息
            UserLoginVo userLoginVo = (UserLoginVo)redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + userId);
            if (userLoginVo != null) {
                //将用户信息放入上下文
                AuthContextHolder.setUserId(userId);
                AuthContextHolder.setWareId(userLoginVo.getWareId());
                AuthContextHolder.setUserLoginVo(userLoginVo);
            }
        }


    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
