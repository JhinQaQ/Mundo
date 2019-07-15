package com.jhin.mundo;

/*
Mundo中所有的配置在Mundo全局唯一对象完成，所以设计成单例

 */

import com.jhin.mundo.config.ConfigLoader;
import com.jhin.mundo.render.Render;
import com.jhin.mundo.route.Routers;
import lombok.Getter;
import lombok.Setter;
import org.omg.CORBA.Request;

import javax.xml.ws.Response;
import java.lang.reflect.Method;

public class Mundo {
    /*
    首先，我们存放路由
     */
    @Getter @Setter private Routers routers;

    private ConfigLoader configLoader;

    @Getter
    @Setter private boolean init = false;
    @Getter@Setter private Render render;

    private Mundo(){
        routers = new Routers();
        configLoader = new ConfigLoader();
    }

    public boolean isInit() {
        return init;
    }
    private static class MundoHolder{
        private static Mundo ME = new Mundo();
    }
    public static Mundo me(){
        return  MundoHolder.ME;
    }
    public Mundo addConf(String conf){
        configLoader.load(conf);
        return  this;
    }
    public String getConf(String name){
        return configLoader.getConf(name);
    }
    public Mundo addRoutes(Routers routers){
        this.routers.addRoute(routers.getRoutes());
        return this;
    }

    /**
     * 添加路由
     * @param path			映射的PATH
     * @param methodName	方法名称
     * @param controller	控制器对象
     * @return				返回Mario
     */
    public Mundo addRoute(String path, String methodName, Object controller){
        try {
            Method method = controller.getClass().getMethod(methodName, Request.class, Response.class);
            this.routers.addRoute(path, method, controller);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return this;
    }




}
