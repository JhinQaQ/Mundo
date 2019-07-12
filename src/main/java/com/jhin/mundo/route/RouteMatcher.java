package com.jhin.mundo.route;

import lombok.Setter;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RouteMatcher {
    @Setter  private List<Route> routes;
    public RouteMatcher(List<Route> routes){
        this.routes=routes;
    }

    /*
    1.根据path来寻找路由
    @param path 请求地址
    @return 返回查询到的路由
     */

    public Route findRoute(String path){
        String cleanPath = parsePath(path);
        List<Route>matchRoutes = new ArrayList<Route>();
        for (Route route:this.routes ){
            if (matchesPath(route.getPath(),cleanPath)){
                matchRoutes.add(route);
            }

        }
        //优先匹配
        giveMatch(path,matchRoutes);


    return matchRoutes.size() > 0 ? matchRoutes.get(0) : null;
    }

    private void giveMatch(final String uri, List<Route> routes) {
        Collections.sort(routes, new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                if (o2.getPath().equals(uri)) {
                    return o2.getPath().indexOf(uri);
                }
                return -1;
            }
        });
    }

    private boolean matchesPath(String routePath, String pathToMatch) {
        routePath = routePath.replaceAll(PathUtil.VAR_REGEXP, PathUtil.VAR_REPLACE);
        return pathToMatch.matches("(?i)" + routePath);
    }

    private String parsePath(String path) {
        path = PathUtil.fixPath(path);
        try {
            URI uri = new URI(path);
            return uri.getPath();
        } catch (URISyntaxException e) {
            return null;
        }
    }



}
