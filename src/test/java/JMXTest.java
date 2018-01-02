import javax.management.*;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.MemoryUsage;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JMXTest {

    public static void main(String[] args) {
        try {
            String jmxURL = "service:jmx:rmi:///jndi/rmi://localhost:8080/jmxrmi";//tomcat jmx url
            JMXServiceURL serviceURL = new JMXServiceURL(jmxURL);

            Map map = new HashMap();
            //String[] credentials = new String[] { "monitorRole" , "QED" };
            map.put("jmx.remote.credentials", new String[] { "monitorRole" , "QED" });

            JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL);
            //JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL, map);
            MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();

            ObjectName threadObjectName = new ObjectName("Catalina:type=ThreadPool,name=\"http-nio-81\"");
            MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(threadObjectName);
            //tomcat的线程数对应的属性值
            String attrName = "currentThreadCount";
            MBeanAttributeInfo[] mbAttributes = mBeanInfo.getAttributes();
            System.out.println("currentThreadCount:"+mBeanServerConnection.getAttribute(threadObjectName, attrName));

            //heap
            for(int j=0;j <mBeanServerConnection.getDomains().length;j++){
                System.out.println("###########"+mBeanServerConnection.getDomains()[j]);
            }

            Set MBeanset = mBeanServerConnection.queryMBeans(null, null);
            System.out.println("MBeanset.size() : " + MBeanset.size());
            Iterator MBeansetIterator = MBeanset.iterator();
            while (MBeansetIterator.hasNext()) {
                ObjectInstance objectInstance = (ObjectInstance)MBeansetIterator.next();
                ObjectName objectName = objectInstance.getObjectName();
                String canonicalName = objectName.getCanonicalName();
                System.out.println("canonicalName : " + canonicalName);
                if (canonicalName.equals("Catalina:host=localhost,type=Cluster"))      {
                    // Get details of cluster MBeans
                    System.out.println("Cluster MBeans Details:");
                    System.out.println("=========================================");
                    //getMBeansDetails(canonicalName);
                    String canonicalKeyPropList = objectName.getCanonicalKeyPropertyListString();
                }
            }

            //------------------------- system ----------------------
            ObjectName runtimeObjName = new ObjectName("java.lang:type=Runtime");
            System.out.println("厂商:"+ (String)mBeanServerConnection.getAttribute(runtimeObjName, "VmVendor"));
            System.out.println("程序:"+ (String)mBeanServerConnection.getAttribute(runtimeObjName, "VmName"));
            System.out.println("版本:"+ (String)mBeanServerConnection.getAttribute(runtimeObjName, "VmVersion"));
            Date starttime=new Date((Long)mBeanServerConnection.getAttribute(runtimeObjName, "StartTime"));
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("启动时间:"+df.format(starttime));
            Long timespan=(Long)mBeanServerConnection.getAttribute(runtimeObjName, "Uptime");
            System.out.println("连续工作时间:"+JMXTest.formatTimeSpan(timespan));

            //------------------------ JVM -------------------------
            //堆使用率
            ObjectName heapObjName = new ObjectName("java.lang:type=Memory");
            MemoryUsage heapMemoryUsage =  MemoryUsage.from((CompositeDataSupport)mBeanServerConnection.getAttribute(heapObjName, "HeapMemoryUsage"));
            long maxMemory = heapMemoryUsage.getMax();//堆最大
            long commitMemory = heapMemoryUsage.getCommitted();//堆当前分配
            long usedMemory = heapMemoryUsage.getUsed();
            System.out.println("maxMemory:"+maxMemory+" commitMemory:"+commitMemory+" usedMemory:"+usedMemory+" heap:"+(double)usedMemory*100/commitMemory+"%");//堆使用率
            MemoryUsage nonheapMemoryUsage =  MemoryUsage.from((CompositeDataSupport)mBeanServerConnection.getAttribute(heapObjName, "NonHeapMemoryUsage"));
            long noncommitMemory = nonheapMemoryUsage.getCommitted();
            long nonusedMemory = heapMemoryUsage.getUsed();
            System.out.println("noncommitMemory:"+noncommitMemory+" nonusedMemory:"+nonusedMemory+" nonheap:"+(double)nonusedMemory*100/noncommitMemory+"%");

            //
            ObjectName permObjName = new ObjectName("java.lang:type=MemoryPool,name=Tenured Gen");//Tenured Generation老年代
            MemoryUsage permGenUsage =  MemoryUsage.from((CompositeDataSupport)mBeanServerConnection.getAttribute(permObjName, "Usage"));
            long committed = permGenUsage.getCommitted();//持久堆大小
            long used = heapMemoryUsage.getUsed();//
            System.out.println("perm gen:"+(double)used*100/committed+"%");//持久堆使用率

            //-------------------- Session ---------------
            ObjectName managerObjName = new ObjectName("Catalina:type=Manager,*");
            Set<ObjectName> s=mBeanServerConnection.queryNames(managerObjName, null);
            for (ObjectName obj:s){
                System.out.println("应用名:"+obj.getKeyProperty("path"));
                ObjectName objname=new ObjectName(obj.getCanonicalName());
                System.out.println("最大会话数:"+ mBeanServerConnection.getAttribute( objname, "maxActiveSessions"));
                System.out.println("会话数:"+ mBeanServerConnection.getAttribute( objname, "activeSessions"));
                System.out.println("活动会话数:"+ mBeanServerConnection.getAttribute( objname, "sessionCounter"));
            }

            //----------------- Thread Pool ----------------
            ObjectName threadpoolObjName = new ObjectName("Catalina:type=ThreadPool,*");
            Set<ObjectName> s2=mBeanServerConnection.queryNames(threadpoolObjName, null);
            for (ObjectName obj:s2){
                System.out.println("端口名:"+obj.getKeyProperty("name"));
                ObjectName objname=new ObjectName(obj.getCanonicalName());
                System.out.println("最大线程数:"+ mBeanServerConnection.getAttribute( objname, "maxThreads"));
                System.out.println("当前线程数:"+ mBeanServerConnection.getAttribute( objname, "currentThreadCount"));
                System.out.println("繁忙线程数:"+ mBeanServerConnection.getAttribute( objname, "currentThreadsBusy"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String formatTimeSpan(long span){
        long minseconds = span % 1000;

        span = span /1000;
        long seconds = span % 60;

        span = span / 60;
        long mins = span % 60;

        span = span / 60;
        long hours = span % 24;

        span = span / 24;
        long days = span;
        return (new Formatter()).format("%1$d天 %2$02d:%3$02d:%4$02d.%5$03d", days,hours,mins,seconds,minseconds).toString();
    }
}
