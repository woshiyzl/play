package com.play.proxy;
import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ProxyCglibMain {
    public static void main(String[] args) {
        String path = ProxyCglibMain.class.getResource("/").getPath() + "cglib";
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, path);
        System.out.println("输出目录是：{}"+path);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(CglibUser.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                if(method.getName()!=null && method.getName().equals("setUserName")) {
                    System.out.println("before");
                    String arg = (String) objects[0];
                    objects[0] = arg+"*test";
                    Object result = methodProxy.invokeSuper(o, objects);
                    return result;
                }else {
                    return methodProxy.invokeSuper(o, objects);
                }
            }
        });
        CglibUser user= (CglibUser) enhancer.create();
        user.setUserName("test");
        System.out.println(user.getUserName());
    }
}
