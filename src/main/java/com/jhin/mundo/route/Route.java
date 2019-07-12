package com.jhin.mundo.route;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
/*
 所有的请求在程序中是一个Route，
 path匹配，action执行，在controller当中。
 */
public class Route {

    @Getter
    @Setter
    private String path;

    @Getter
    @Setter
    private Method action;
    /*
    控制器
     */
    @Getter
    @Setter
    private Object controller;

    public Route() {

    }
}

