package com.ulp.filter;

import com.ulp.bean.UserModel;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录验证过滤器
 * 拦截需要登录才能访问的页面
 */
@WebFilter(urlPatterns = {
    "/admin-homepage.jsp",
    "/teacher-homepage.jsp",
    "/student-homepage.jsp",
    "/dashboard",
    "/profile",
    "/admin-resource.jsp"
})
public class AuthenticationFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化过滤器
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        // 检查用户是否已登录
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        
        if (!isLoggedIn) {
            // 未登录，重定向到登录页面
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }
        
        // 已登录，继续处理请求
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // 清理资源
    }
}
