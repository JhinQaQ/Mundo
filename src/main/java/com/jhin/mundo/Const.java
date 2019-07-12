package com.jhin.mundo;

import java.util.concurrent.Callable;

/*
这个类是常量类
 */
public class Const {
    private Const(){

    }
    //默认字符集
    public static  final String DEFAULT_CHAR_SET ="utf-8";
    //当前版本号
    public static final String MUNDO_VERSION ="1.0.0";
    //读取试图前缀的字段
    public static final String VIEW_PREFIX_FIELD = "mundo.view.prefix";

    /**
     * 读取视图后缀的字段
     */
    public static final String VIEW_SUFFIX_FIELD = "mario.view.suffix";

    /**
     * 视图前缀
     */
    public static final String VIEW_PREFIX = "/WEB-INF/";

    /**
     * 视图后缀
     */
    public static final String VIEW_SUFFIX = ".jsp";
}
