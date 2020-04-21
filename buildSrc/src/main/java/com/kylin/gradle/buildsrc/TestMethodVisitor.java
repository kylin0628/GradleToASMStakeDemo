package com.kylin.gradle.buildsrc;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * @Description:处理方法，分别在方法进入和退出的时候插入我们的代码
 * @Auther: wangqi
 * CreateTime: 2020/4/16.
 */
public class TestMethodVisitor extends AdviceAdapter {
    private int startTimeId = -1;

    private String methodName = null;

    /**
     * Constructs a new {@link AdviceAdapter}.
     *
     * @param api           the ASM API version implemented by this visitor. Must be one of {@link
     *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
     * @param methodVisitor the method visitor to which this adapter delegates calls.
     * @param access        the method's access flags (see {@link Opcodes}).
     * @param name          the method's name.
     * @param descriptor    the method's descriptor (see {@link Type Type}).
     */
    protected TestMethodVisitor(MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(ASM7, methodVisitor, access, name, descriptor);
        System.out.println("methodVisitor = " + descriptor.toString());
        //加一个toast
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
//        addStartTime();
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
//        addToast();
//        addLogThis();
//        addTimeDifference();
        if (annotationDes.equals("Lcom/kylin/gradle/gradletoasmstakedemo/TestAnnotation;")) {
            addLog();
        }

    }

    String annotationDes = "";

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        System.out.println("method descriptor = " + descriptor);
        annotationDes = descriptor;
        AnnotationVisitor annotationVisitor = super.visitAnnotation(descriptor, visible);
        if (descriptor.equals("Lcom/kylin/gradle/gradletoasmstakedemo/TestAnnotation;")) {
            return new TestAnnotationVisitor(annotationVisitor);
        }
        return annotationVisitor;
    }

    private void addStartTime() {
        startTimeId = newLocal(Type.LONG_TYPE);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitIntInsn(LSTORE, startTimeId);
    }

    //加记录end时间和计算差值代码
    private void addTimeDifference() {
        int durationId = newLocal(Type.LONG_TYPE);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitVarInsn(LLOAD, startTimeId);
        mv.visitInsn(LSUB);
        mv.visitVarInsn(LSTORE, durationId);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn("方法 " + methodName + "() 耗时： ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(LLOAD, durationId);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn(" ms");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    //加一个Log日志
    private void addLog() {
        mv.visitLdcInsn("TAGS");
        mv.visitLdcInsn("HelloWorld sayHello wangqi");
        mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);

    }

    private void addToast() {
        int addResultType = newLocal(Type.INT_TYPE);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn("a + b = ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(ILOAD, addResultType);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitInsn(ICONST_1);
        mv.visitMethodInsn(INVOKESTATIC, "android/widget/Toast", "makeText", "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/widget/Toast", "show", "()V", false);
    }

    private void addLogThis() {
        mv.visitLdcInsn("this = ");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(POP);
    }

}
