package com.jhin.mundo.util;

import java.util.Arrays;

public class ExceptionUtil {
    public static void makeRunTimeWhen(boolean flag,String message,Object ...args){
        if (flag){
            message = String.format(message,args);
            RuntimeException e = new RuntimeException(message);
            throw correctStackTrace(e);

        }
    }
    public static void makeRunTime(Throwable cause){
        RuntimeException e = new RuntimeException(cause);
        throw correctStackTrace(e);
    }


    private static RuntimeException correctStackTrace(RuntimeException e){
        StackTraceElement[] s = e.getStackTrace();
        if (null!=s && s.length>0){
            e.setStackTrace(Arrays.copyOfRange(s,1,s.length));
        }
        return  e;
    }
}
