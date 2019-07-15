package com.jhin.mundo.render;

import com.jhin.mundo.Const;
import com.jhin.mundo.Mundo;
import com.jhin.mundo.MundoContext;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JspRender implements Render{
    /*
    实现JSP的渲染实现
     */



    @Override
    public void render(String view, Writer writer) {
        String viewPath = this.getViewPath(view);

        HttpServletRequest servletRequest = MundoContext.me().getRequest().getRaw();
        HttpServletResponse servletResponse = MundoContext.me().getReponse().getRaw();
        try {
            servletRequest.getRequestDispatcher(viewPath).forward(servletRequest, servletResponse);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getViewPath(String view){
        Mundo mundo = Mundo.me();
        String viewPrfix = mundo.getConf(Const.VIEW_PREFIX_FIELD);
        String viewSuffix = mundo.getConf(Const.VIEW_SUFFIX_FIELD);

        if (null == viewSuffix || viewSuffix.equals("")) {
            viewSuffix = Const.VIEW_SUFFIX;
        }
        if (null == viewPrfix || viewPrfix.equals("")) {
            viewPrfix = Const.VIEW_PREFIX;
        }
        String viewPath = viewPrfix + "/" + view;
        if (!view.endsWith(viewSuffix)) {
            viewPath += viewSuffix;
        }
        return viewPath.replaceAll("[/]+", "/");
    }


}
