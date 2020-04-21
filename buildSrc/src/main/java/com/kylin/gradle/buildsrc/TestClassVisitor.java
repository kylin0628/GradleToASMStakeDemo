package com.kylin.gradle.buildsrc;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM7;

/**
 * @Description:ASM自定义处理类
 * @Auther: wangqi
 * CreateTime: 2020/4/16.
 */
public class TestClassVisitor extends ClassVisitor {


    public TestClassVisitor(ClassVisitor classVisitor) {
        super(ASM7, classVisitor);
    }

    //这个class类文件的入口方法
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        System.out.println("需要注入的类文件 = " + name);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }

    //暴露当前class下所有的方法(此处可以做方法拦截之类的操作)
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if ("tesStackMethoh".equals(name))
            return new TestMethodVisitor(methodVisitor, access, name, descriptor);
        return methodVisitor;
    }

    /**
     * class类注解文件的。
     *
     * @param descriptor
     * @param visible
     * @return
     */
    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationVisitor annotationVisitor = super.visitAnnotation(descriptor, visible);
        System.out.println("注解处理 = " + descriptor);
        return new TestAnnotationVisitor(annotationVisitor);
    }
}
