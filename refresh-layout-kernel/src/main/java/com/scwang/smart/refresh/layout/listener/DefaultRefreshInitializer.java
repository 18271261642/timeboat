package com.scwang.smart.refresh.layout.listener;

import android.content.Context;


import com.scwang.smart.refresh.layout.api.RefreshLayout;

import androidx.annotation.NonNull;

/**
 * 默认全局初始化器
 * Created by scwang on 2018/5/29 0029.
 */
public interface DefaultRefreshInitializer {
    void initialize(@NonNull Context context, @NonNull RefreshLayout layout);
}
