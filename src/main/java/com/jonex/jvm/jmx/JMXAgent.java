package com.jonex.jvm.jmx;

import com.jonex.jvm.jmx.service.HelloService;
import com.jonex.jvm.jmx.service.impl.HelloServiceImpl;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class JMXAgent {

    public static void main(String[] args) throws Exception{
        // MBeanServer对象获取
        // 或采用MBeanServerFactory.createMBeanServer()获取MBeanServer对象
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        // 构建ObjectName
        ObjectName name = new ObjectName("com.jonex:type=HelloServiceImpl");
        HelloService mbean = new HelloServiceImpl();
        // 将Mbean注册到MBeanServer
        mbs.registerMBean(mbean, name);
        //创建一个AdaptorServer，这个类将决定MBean的管理界面，这里用最普通的Html型界面。AdaptorServer其实也是一个MBean。
        // alpha:name=HelloWorld的名字是有一定规则的，格式为：“域名:name=MBean名称”，域名和MBean名称都可以任意取。
        ObjectName adapterName = new ObjectName("HelloAgent:name=htmladapter,port=8082"); //
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();
        server.registerMBean(adapter, adapterName);
        adapter.start();
        System.out.println("start.....");
    }
}
