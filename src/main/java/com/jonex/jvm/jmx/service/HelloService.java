package com.jonex.jvm.jmx.service;

public interface HelloService {
    public void sayHello();
    public int add(int x, int y);
    public String getName();
    public int getCacheSize();
    public void setCacheSize(int size);
}
