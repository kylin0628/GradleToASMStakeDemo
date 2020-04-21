package com.kylin.gradle.gradletoasmstakedemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int result = tesStackMethoh(1, 11);
    }

    /**
     * 通过字节码插桩技术为该方法编译时期改变.class文件中的此方法添加log日志
     */
    @TestAnnotation(age = 10, name = "kylin")
    public int tesStackMethoh(int a, int b) {
        int addResult = a + b;
        return addResult;
    }
}
