package com.kylin.gradle.buildsrc;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * @Description:gradle自定义插件入口类
 * @Auther: wangqi
 * CreateTime: 2020/4/16.
 */
public class TestPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        System.out.println("testPlugin =  " + project.getDisplayName());
        AppExtension byType = project.getExtensions().getByType(AppExtension.class);
        project.getExtensions().create("testParams", TestBean.class);
        TestBean params = project.getExtensions().getByType(TestBean.class);
        System.out.println("TestBean =  name:" + params.name + "----age:" + params.age);
        byType.registerTransform(new TestTransform());
    }
}
