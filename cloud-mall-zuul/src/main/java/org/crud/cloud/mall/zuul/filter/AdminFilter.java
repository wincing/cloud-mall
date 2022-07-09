package org.crud.cloud.mall.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.crud.cloud.mall.zuul.feign.UserFeignClient;
import org.crudboy.cloud.mall.user.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.crudboy.cloud.mall.common.common.Constant.MALL_USER;

/**
 *  管理员过滤器
 */
@Component
public class AdminFilter extends ZuulFilter {

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

        if (requestURI.contains("/admin/login")) {
            return false;
        }
        if (requestURI.contains("admin")) {
            return true;
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        context.getResponse().setCharacterEncoding("UTF-8");
        HttpServletRequest request = context.getRequest();

        HttpSession session = request.getSession();
        User currentUser = (User)session.getAttribute(MALL_USER);
        if (currentUser == null) {
            context.setSendZuulResponse(false);
            context.setResponseBody("{\n" +
                    "    \"code\": 10007,\n" +
                    "    \"msg\": \"用户未登录\",\n" +
                    "    \"data\": null\n" +
                    "}");
            context.setResponseStatusCode(200);
            return null;
        }

        if (!userFeignClient.checkAdminRole(currentUser)) {
            context.setSendZuulResponse(false);
            context.setResponseBody("{\n" +
                    "    \"code\": 10009,\n" +
                    "    \"msg\": \"无管理员权限\",\n" +
                    "    \"data\": null\n" +
                    "}");
            context.setResponseStatusCode(200);
        }
        return null;
    }
}
