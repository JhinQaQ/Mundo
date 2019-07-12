package com.jhin.mundo;

import lombok.Getter;
import lombok.Setter;
import org.omg.CORBA.Request;

import javax.servlet.ServletContext;
import javax.xml.ws.Response;

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

    public static void initContext(ServletContext context,Request request ,Response reponse){
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
