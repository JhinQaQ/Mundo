package com.jhin.mundo;

import com.jhin.mundo.route.Route;
import com.jhin.mundo.route.RouteMatcher;
import com.jhin.mundo.route.Routers;
import com.jhin.mundo.servlet.wrapper.*;
import com.jhin.mundo.util.PathUtil;
import com.jhin.mundo.util.ReflectUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/*
接收用户请求
查找路由
找到即执行配置的方法
找不到你看到的应该是404
 */

public class MundoFilter implements Filter {

    private static final Logger logger =Logger.getLogger(MundoFilter.class.getName());

    private RouteMatcher routeMatcher = new RouteMatcher(new ArrayList<Route>());

    private ServletContext servletContext;

    public void init (FilterConfig filterConfig )throws ServletException{
        Mundo mundo = Mundo.me();
        if (!mundo.isInit()){
            String className = filterConfig.getInitParameter("bootstrap");
            Bootstrap bootstrap = this.getBootstrap(className );
            bootstrap.init(mundo);

            Routers routers = mundo.getRouters();
            if(null != routers){
                routeMatcher.setRoutes(routers.getRoutes());

            }
            servletContext = filterConfig.getServletContext();

            mundo.setInit(true);
        }
    }

    private Bootstrap getBootstrap(String className) {
        if(null != className){
            try {
                Class<?> clazz = Class.forName(className);
                Bootstrap bootstrap = (Bootstrap) clazz.newInstance();
                return bootstrap;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("init bootstrap class error!");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 请求的uri
        String uri = PathUtil.getRelativePath(request);

        logger.info("Request URI：" + uri);

        Route route = routeMatcher.findRoute(uri);

        // 如果找到
        if (route != null) {
            // 实际执行方法
            handle(request, response, route);
        } else{
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

    private void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Route route){

        // 初始化上下文
        Request request = new Request(httpServletRequest);
        Response response = new Response(httpServletResponse);
        MundoContext.initContext(servletContext, request, response);

        Object controller = route.getController();
        // 要执行的路由方法
        Method actionMethod = route.getAction();
        // 执行route方法
        executeMethod(controller, actionMethod, request, response);
    }

    /**
     * 获取方法内的参数
     */
    private Object[] getArgs(Request request, Response response, Class<?>[] params){

        int len = params.length;
        Object[] args = new Object[len];

        for(int i=0; i<len; i++){
            Class<?> paramTypeClazz = params[i];
            if(paramTypeClazz.getName().equals(Request.class.getName())){
                args[i] = request;
            }
            if(paramTypeClazz.getName().equals(Response.class.getName())){
                args[i] = response;
            }
        }

        return args;
    }

    /**
     * 执行路由方法
     */
    private Object executeMethod(Object object, Method method, Request request, Response response){
        int len = method.getParameterTypes().length;
        method.setAccessible(true);
        if(len > 0){
            Object[] args = getArgs(request, response, method.getParameterTypes());
            return ReflectUtil.invokeMehod(object, method, args);
        } else {
            return ReflectUtil.invokeMehod(object, method);
        }
    }

}
