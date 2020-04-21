package com.kylin.gradle.buildsrc;

import org.objectweb.asm.AnnotationVisitor;

import static org.objectweb.asm.Opcodes.ASM7;

/**
 * @Description:.class 注解处理
 * @Auther: wangqi
 * CreateTime: 2020/4/17.
 */
public class TestAnnotationVisitor extends AnnotationVisitor {

    public TestAnnotationVisitor(AnnotationVisitor annotationVisitor) {
        super(ASM7, annotationVisitor);
    }

    @Override
    public void visit(String name, Object value) {
        super.visit(name, value);
        System.out.println("TestAnnotationVisitor visit = " + name + "  value = " + value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        System.out.println("TestAnnotationVisitor visitAnnotation = " + name + "  descriptor = " + descriptor);
        return super.visitAnnotation(name, descriptor);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        System.out.println("TestAnnotationVisitor visitArray = " + name);
        return super.visitArray(name);
    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {
        super.visitEnum("newname", descriptor, value);
        System.out.println("TestAnnotationVisitor visitEnum = " + name + "  descriptor = " + descriptor + "value = " + value);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        System.out.println("TestAnnotationVisitor visitEnd");
    }
}
