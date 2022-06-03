package com.itheima.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 */
public class BaseContext {
    //泛型是Long，因为我们要存储的是Long类型的id
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 当前线程保存传入的id
     * @param id 用户的id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 返回当前线层保存的id
     * @return 返回用户的id
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
