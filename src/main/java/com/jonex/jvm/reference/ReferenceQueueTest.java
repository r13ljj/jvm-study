package com.jonex.jvm.reference;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Created by xubai on 2018/02/07 下午1:56.
 */
public class ReferenceQueueTest {

    public static void main(String[] args) throws Exception{
        final ReferenceQueue referenceQueue = new ReferenceQueue();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Reference reference = referenceQueue.remove();
                        System.out.println(reference+"回收了");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        Object o = new Object();
        Reference root = new WeakReference(o, referenceQueue);
        System.out.println(root);
        o = null;
        System.gc();
        System.in.read();
    }

}
