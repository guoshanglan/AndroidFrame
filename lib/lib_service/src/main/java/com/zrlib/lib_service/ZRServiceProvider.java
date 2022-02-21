package com.zrlib.lib_service;

import android.text.TextUtils;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.LinkedHashMap;

/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/10/27
 * dest : ZRServiceProvider
 */
class ZRServiceProvider {

    private final LinkedHashMap<String, IProvider> map = new LinkedHashMap<>();

    private ZRServiceProvider() {
    }

    private static class SingletonHolder {
        private static final ZRServiceProvider INSTANCE = new ZRServiceProvider();
    }

    public static ZRServiceProvider getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public synchronized <H extends IProvider> H getService(String route) {
        H h = (H) map.get(route);
        if (h == null && !TextUtils.isEmpty(route)) {
            h = (H) ARouter.getInstance().build(route).navigation();
            if (h != null) {
                map.put(route, h);
            }
        }
        return h;
    }

}
