package com.jonex.jvm.jmx;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;

public class JMXServer {

    public static void main(String[] args) {
        //JMXConnectorServer service
        try {
            // MBeanServer对象获取
            // 或采用MBeanServerFactory.createMBeanServer()获取MBeanServer对象
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            //这句话非常重要，不能缺少！注册一个端口，绑定url后，客户端就可以使用rmi通过url方式来连接JMXConnectorServer
            LocateRegistry.createRegistry(8888);
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:8888/server");
            // 注意：server为MBeanServer对象（Mbean注册到此对象）
            JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
            System.out.println("....................begin rmi start.....");
            cs.start();
            System.out.println("....................rmi start.....");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
