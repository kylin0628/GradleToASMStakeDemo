package com.kylin.gradle.gradletoasmstakedemo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description:
 * @Auther: wangqi
 * CreateTime: 2020/4/19.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface TestAnnotation {
    int age() default 0;

    String name() default "wangqi";
}
