package com.jhin.mundo;

import com.jhin.mundo.servlet.wrapper.Request;
import com.jhin.mundo.servlet.wrapper.Response;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.ServletContext;

public class MundoContext {
    private static final ThreadLocal<MundoContext> CONTEXT_THREAD_LOCAL = new ThreadLocal<MundoContext>();

    @lombok.Setter
    @Getter
    private ServletContext context;
    @lombok.Setter@lombok.Getter
    private Request request;
    @Setter @Getter private Response reponse;

    private MundoContext(){

    }
    public  static MundoContext me(){
        return CONTEXT_THREAD_LOCAL.get();
    }

    public static void initContext(ServletContext context, com.jhin.mundo.servlet.wrapper.Request request , com.jhin.mundo.servlet.wrapper.Response reponse){
        MundoContext mundoContext = new MundoContext();
        mundoContext.context = context;
        mundoContext.request = request;
        mundoContext.reponse = reponse;
        CONTEXT_THREAD_LOCAL.set(mundoContext);
    }
    public static void remove(){
        CONTEXT_THREAD_LOCAL.remove();
    }
}
