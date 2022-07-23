package org.crud.cloud.mall.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.crud.cloud.mall.zuul.feign.UserFeignClient;
import org.crudboy.cloud.mall.common.exception.MallException;
import org.crudboy.cloud.mall.common.exception.MallExceptionEnum;
import org.crudboy.cloud.mall.user.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.crudboy.cloud.mall.common.common.Constant.MALL_TOKEN;

/**
 *  用户鉴权过滤器
 */
@Component
@Slf4j
public class UserFilter extends ZuulFilter {

    @Autowired
    UserFeignClient userFeignClient;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String requestURI = request.getRequestURI();
        if (requestURI.contains("image") || requestURI.contains("pay")) {
            return false;
        }

        return requestURI.contains("update") || requestURI.contains("delete") ||
                requestURI.contains("add") || requestURI.contains("order") ||
                requestURI.contains("cart");
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        context.getResponse().setCharacterEncoding("UTF-8");
        HttpServletRequest request = context.getRequest();

        String token = request.getHeader(MALL_TOKEN);
        log.info("token {} is {}", MALL_TOKEN,  token);
        User currentUser = null;
        if (token != null && token.length() != 0) {
            Integer userId = userFeignClient.get(token);
            log.info("get user id: [{}]", userId);
            currentUser = userFeignClient.getUserById(userId);
        }
        if (currentUser == null || currentUser.getId() == null) {
            context.setSendZuulResponse(false);
            context.setResponseBody(new MallException(MallExceptionEnum.NEED_LOGIN)
                    .toString());
            context.setResponseStatusCode(200);
        }
        log.info("the user trying login: {} ------------", currentUser);
        return null;
    }
}
