package com.jhin.mundo.render;

import java.io.Writer;
public interface Render {
    /*
    从渲染到视图
     */
    public void render(String view, Writer writer) ;
}
