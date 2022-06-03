package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否完成登录
 * urlPatterns = "/*" 代表拦截哪些路径，这里是拦截所有的请求路径都拦截
 * filterName过滤器名称
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 路径匹配，检查本次请求是否放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        //用PATH_MATCHER检查每一个url和当前请求url是否一致
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //线程id
        long id = Thread.currentThread().getId();
        log.info("线程id:{}",id);

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse    ;

        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求：{}", requestURI);

        //可以放行的url
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                //静态资源可以放行
                "/backend/**",
                "/front/**",
                "/user/sendMsg", // 移动端发送短信
                "/user/login" // 移动端登录
        };

        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3、如果不需要处理，则直接放行
        if (check){
            log.info("本次请求{}不需要处理", requestURI);
            //对请求进行放行
            filterChain.doFilter(request, response);
            return;
        }

        //4-1、判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为{}", request.getSession().getAttribute("employee"));

            //设置当前线程的线程局部变量的值（id值），MetaObjectHandler中会用id值来完成公共字段的自动填充
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            // 在controller中，用户登录时会在session中以employee为名存储当前用户的id
            // 如果不为空，说明已经登录过，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        //4-2、判断登录状态，如果已登录，则直接放行
        //判断短信登录，登录之后会将状态存到session中并命名为user，所以我们这里取出对应的session判断登录状态
        if (request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为{}", request.getSession().getAttribute("user"));

            //设置当前线程的线程局部变量的值（id值），MetaObjectHandler中会用id值来完成公共字段的自动填充
            Long empId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(empId);

            // 在controller中，用户登录时会在session中以employee为名存储当前用户的id
            // 如果不为空，说明已经登录过，直接放行
            filterChain.doFilter(request, response);
            return;
        }


        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面相应数据
        log.info("用户未登录");
        //在backend/js/request.js文件中的响应拦截器中，如果res.data.msg === 'NOTLOGIN'，则直接跳转到登录页面
        //所以这里我们用输出流方式响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
}
