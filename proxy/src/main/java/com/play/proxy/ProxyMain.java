package com.play.proxy;

import java.lang.reflect.Proxy;

public class ProxyMain {
    public static void main(String[] args) {
        System.setProperty("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        IUser user = new UserImpl();
        IUser instance = (IUser) Proxy.newProxyInstance(IUser.class.getClassLoader(), new Class<?>[]{IUser.class}, (proxy, method, args1)->{
            if(method.getName()!=null && method.getName().equals("setUserName")){
                System.out.println("before");
                String arg = (String) args1[0];
                args1[0] = arg+"*test";
                return method.invoke(user, args1);
            }else{
                return method.invoke(user, args1);
            }
        });
        instance.setUserName("test");
        System.out.println(instance.getUserName());
    }
}
