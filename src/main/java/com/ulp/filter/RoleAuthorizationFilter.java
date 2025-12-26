package com.ulp.filter;

import com.ulp.bean.UserModel;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 角色权限过滤器
 * 确保用户只能访问与其角色匹配的页面
 */
@WebFilter(urlPatterns = {
    "/admin_homepage.jsp",
    "/teacher_homepage.jsp",
    "/student_homepage.jsp"
})
public class RoleAuthorizationFilter implements Filter {
    
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
        
        if (session != null && session.getAttribute("user") != null) {
            UserModel user = (UserModel) session.getAttribute("user");
            String requestURI = httpRequest.getRequestURI();
            String role = user.getRole().toLowerCase();
            
            // 检查用户角色是否与请求的页面匹配
            boolean authorized = false;
            
            if (requestURI.endsWith("admin_homepage.jsp") && "admin".equals(role)) {
                authorized = true;
            } else if (requestURI.endsWith("teacher_homepage.jsp") && "teacher".equals(role)) {
                authorized = true;
            } else if (requestURI.endsWith("student_homepage.jsp") && "student".equals(role)) {
                authorized = true;
            }
            
            if (authorized) {
                // 角色匹配，继续处理
                chain.doFilter(request, response);
            } else {
                // 角色不匹配，重定向到正确的dashboard
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/dashboard");
            }
        } else {
            // 未登录，重定向到登录页面
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }
    
    @Override
    public void destroy() {
        // 清理资源
    }
}
