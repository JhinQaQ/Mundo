我自己是很不喜欢做一个CRUD boy的，所以不管是在学语言本身还是在框架的学习中，我更倾向去配套学一些偏底层的东西，而不是单纯的去学业务的实现。

在深入一点的了解了一些概念和方法论之后，我想自己也试一试去写一个轻量级的框架，这样可能加深我对于Spring等框架的理解。

这篇笔记的起因是我看了GitHub上面的一个轻量级框架blade（https://github.com/lets-blade/blade），所以也想试试，能不能依葫芦画个瓢出来。

有预感这会是一个跨度有点大的笔记，所以我拿日期作为每段的标题了

### July  11

---

首先是给自己的框架起个名字，Mundo 好了，这个单词还比较好打

结构如下：


![Mundo1.png](https://i.loli.net/2019/07/11/5d26f1f2daa5658234.png)

#### Route类的设计

我自己不太喜欢去说路由这个词，我感觉有点不太好理解，所以就用Route来说。不知道为什么，大家都很少去解释路由这个词，可能是默认所有人都明白了。

> Route(路由)是指根据url分配到对应的处理程序，不同的请求地址会交给Route处理来转发给相应的控制器处理。（有点点像一个寻址过程，根据Route或者路由这个字面意思也能理解它的涵义）

```java
/*
 所有的请求在程序中是一个Route，
 path匹配，action执行，在controller当中。
 */
public class Route {

    private String path;

    private Method action;
    /*
    控制器
     */
    private Object controller;

    public Route(){

    }
    //接下来全是get，set
```

在这个框架里面我们使用一个filter来接受所有的请求，从filter过来的请求有很多，但是我需要知道请求对应的路由，整个路由匹配器吧，我们还得管理这些路由，所以再整个路由管理器





### July12

---

#### Controler

在MVC框架里面可能最重要的就是Controler的设计了，每个请求的接受是通过Controler来处理的。在这个框架里面，控制器是在Route对象的controler字段上，实际上每个请求过来是落在某个方法上去处理。

然后我这里是学的反射来动态调用方法的执行，似乎不是什么很好的方式，因为涉及到一些性能问题，但是太复杂的没了解到，所以先就这样。

控制器的处理部分是在Filter中

```java 
public class MundoFilter implements Filter {
    //logger 是记录器，用来记录这些信息

    private static final Logger logger =Logger.getLogger(MundoFilter.class.getName());

    private RouteMatcher routeMatcher = new RouteMatcher(new ArrayList<Route>());

    private ServletContext servletContext;

    private  void init (FilterConfig filterConfig )throws ServletException{
        Mundo mundo = Mundo.me();
        if (!mundo.isInit()){
            String className = filterConfig.getInitParameter("bootstrap");
            Bootstrap bootstrap = this.getBpptstrap();
            bootstrap.init(mundo);

            Routers routers = mundo.setRouter();
            if(null != routers){
                routeMatcher.setRoutes(routes.getRoutes());

            }
            servletContext = filterConfig.getServletContext();

            mundo.setInit(true);
        }
    }
   //bootstrap是启动类
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
            return ReflectUtil.invokeMethod(object, method, args);
        } else {
            return ReflectUtil.invokeMehod(object, method);
        }
    }
    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }
   }
```

好麻烦...写到后来自己有点懵，粘了不少源码

大概的逻辑是这个样子的

1.  接收用户请求

2.  查找路由

3.  找到即执行配置的方法

4.  找不到你看到的就是404

   #### 匹配路由

   

```java
/**
 * 路由匹配器，用于匹配路由
 * @author biezhi
 */
public class RouteMatcher {

    private List<Route> routes;

    public RouteMatcher(List<Route> routes) {
        this.routes = routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    /**
     * 根据path查找路由
     * @param path  请求地址
     * @return      返回查询到的路由
     */
    public Route findRoute(String path) {
        String cleanPath = parsePath(path);
        List<Route> matchRoutes = new ArrayList<Route>();
        for (Route route : this.routes) {
            if (matchesPath(route.getPath(), cleanPath)) {
                matchRoutes.add(route);
            }
        }
        // 优先匹配原则
        giveMatch(path, matchRoutes);

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
```

用正则去匹配路由的集合