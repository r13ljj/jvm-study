package com.jonex.jvm.reference;

import java.lang.ref.SoftReference;

/**
 * Created by xubai on 2018/02/07 上午11:25.
 *
 * jvm参数设置：
 * -Xmx10m -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintHeapAtGC -Xloggc:gc.log
 */
public class SoftReferenceTest {

    public static void main(String[] args) {
        Student stu1 = new Student(13, "jonex");
        SoftReference<Student> sr = new SoftReference<Student>(stu1);
        stu1 = null;
        System.out.println("soft reference object clear, sr:"+sr.get());

        System.gc();
        System.out.println("soft reference 1 collection, sr:"+sr.get());

        //分配一个大对象
        byte[] bigObject = new byte[1024 * 977 * 7];
        System.gc();

        System.out.println("soft reference after allocate big object, sr:"+sr.get());

    }

    static class Student{
        private Integer id;
        private String name;

        public Student() {
        }

        public Student(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
