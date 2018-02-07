package com.jonex.jvm.reference;

import java.lang.ref.WeakReference;

/**
 * Created by xubai on 2018/02/07 上午11:08.
 */
public class WeakReferenceTest {

    public static void main(String[] args) {
        WeakReference<Object> wr = new WeakReference<Object>(new Object());
        int i=0;
        Object obj = wr;
        while(true){
            System.out.println(i);
            if(wr.get() != null){
                i++;
                System.out.println("wr can reach, wr:"+wr.get()+" obj:"+obj);
            }else{
                System.out.println("wr is null, obj:"+obj);
                break;
            }
            System.gc();
        }
    }

}
