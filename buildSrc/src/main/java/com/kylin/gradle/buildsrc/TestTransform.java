package com.kylin.gradle.buildsrc;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * @Description:Transform在编译过程中.class文件转换成.dex的时候触发（.class -->transform-->.dex）
 * @Auther: wangqi
 * CreateTime: 2020/4/16.
 */
public class TestTransform extends Transform {

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        //当前是否是增量编译(由isIncremental() 方法的返回和当前编译是否有增量基础)
        boolean isIncremental = transformInvocation.isIncremental();
        //消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        for (TransformInput input : inputs) {
            //Failed resolution of: Landroidx/appcompat/R$drawable;(不遍历处理的话会出现这个bug)
            for (JarInput jarInput : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                FileUtils.copyFile(jarInput.getFile(), dest);
            }

            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                FileUtils.copyDirectory(
                        directoryInput.getFile(),
                        outputProvider.getContentLocation(directoryInput.getName(), getInputTypes(), getScopes(), Format.DIRECTORY));

                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);

                replaceFileClass(directoryInput.getFile());
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                FileUtils.copyDirectory(directoryInput.getFile(), dest);
                System.out.println("dest: " + dest);
            }

        }
    }

    /**
     * 获取指定文件夹下面的.class文件
     *
     * @param replaceFile 替换文件夹的路径
     */
    private void replaceFileClass(File replaceFile) {
        File[] subFile = replaceFile.listFiles();
        assert subFile != null;
        for (File file : subFile) {
            // 判断是否为文件夹
            if (!file.isDirectory()) {
                hook(file.getAbsolutePath());
            } else {
                replaceFileClass(file);
            }
        }
    }

    @Override
    public String getName() {
        return "kylin0628";
    }

    /**
     * 筛选需要处理的文件
     * 代表了所有jar包，文件夹中，aar包中的.class文件和标准的java源文件，我们都进行筛选。
     *
     * @return
     */
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    /**
     * 插件作用域设置
     * TransformManager.SCOPE_FULL_PROJECT 插件作用域真个项目
     *
     * @return
     */
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY;
    }

    /**
     * 是否支持增量编译
     *
     * @return
     */
    @Override
    public boolean isIncremental() {
        return false;
    }

    /**
     * 替换文件，改变文件
     *
     * @param replaceFileAbsPath
     */
    private void hook(String replaceFileAbsPath) {
        System.out.println("replaceFileAbsPath = " + replaceFileAbsPath);
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        ClassReader classReader = null;
        try {
            inputStream = new FileInputStream(replaceFileAbsPath);
            classReader = new ClassReader(inputStream);

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassVisitor classVisitor = new TestClassVisitor(classWriter);

            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

            outputStream = new FileOutputStream(replaceFileAbsPath);
            outputStream.write(classWriter.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert outputStream != null;
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
