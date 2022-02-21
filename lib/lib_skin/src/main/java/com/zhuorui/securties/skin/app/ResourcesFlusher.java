package com.zhuorui.securties.skin.app;

import android.content.res.Resources;
import android.os.Build;
import android.util.LongSparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * ResourcesFlusher 类或者接口名称
 * @company 深圳市卓锐网络科技有限公司
 * @description
 * @date 2021/01/05 16:05
 */
public final class ResourcesFlusher {

    private static final String TAG = "ResourcesFlusher";

    /**
     * 强制刷新 Resources 资源缓存
     *
     * @param resources .
     */
    public static void flush(@NonNull final Resources resources) {
        /**
         * 参考自
         * @see androidx.appcompat.app.ResourcesFlusher#flush(Resources)
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                flushNougats(resources);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flushMarshmallows(resources);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                flushLollipops(resources);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                flushJellyBeans(resources);
            }
        }
    }

    private static Field sDrawableCacheField;
    private static boolean sDrawableCacheFieldFetched;

    private static Class sThemedResourceCacheClazz;
    private static boolean sThemedResourceCacheClazzFetched;

    private static Field sThemedResourceCache_mUnthemedEntriesField;
    private static boolean sThemedResourceCache_mUnthemedEntriesFieldFetched;

    private static Field sResourcesImplField;
    private static boolean sResourcesImplFieldFetched;

    @RequiresApi(21)
    private static boolean flushLollipops(@NonNull final Resources resources) {
        if (!sDrawableCacheFieldFetched) {
            try {
                sDrawableCacheField = Resources.class.getDeclaredField("mDrawableCache");
                sDrawableCacheField.setAccessible(true);
            } catch (NoSuchFieldException e) {
            }
            sDrawableCacheFieldFetched = true;
        }
        if (sDrawableCacheField != null) {
            Map drawableCache = null;
            try {
                drawableCache = (Map) sDrawableCacheField.get(resources);
            } catch (IllegalAccessException e) {
            }
            if (drawableCache != null) {
                drawableCache.clear();
                return true;
            }
        }
        return false;
    }

    @RequiresApi(23)
    private static boolean flushMarshmallows(@NonNull final Resources resources) {
        if (!sDrawableCacheFieldFetched) {
            try {
                sDrawableCacheField = Resources.class.getDeclaredField("mDrawableCache");
                sDrawableCacheField.setAccessible(true);
            } catch (NoSuchFieldException e) {
            }
            sDrawableCacheFieldFetched = true;
        }

        Object drawableCache = null;
        if (sDrawableCacheField != null) {
            try {
                drawableCache = sDrawableCacheField.get(resources);
            } catch (IllegalAccessException e) {
            }
        }

        if (drawableCache == null) {
            // If there is no drawable cache, there's nothing to flush...
            return false;
        }

        return drawableCache != null && flushThemedResourcesCache(drawableCache);
    }

    @RequiresApi(24)
    private static boolean flushNougats(@NonNull final Resources resources) {
        if (!sResourcesImplFieldFetched) {
            try {
                sResourcesImplField = Resources.class.getDeclaredField("mResourcesImpl");
                sResourcesImplField.setAccessible(true);
            } catch (NoSuchFieldException e) {
            }
            sResourcesImplFieldFetched = true;
        }

        if (sResourcesImplField == null) {
            // If the mResourcesImpl field isn't available, bail out now
            return false;
        }

        Object resourcesImpl = null;
        try {
            resourcesImpl = sResourcesImplField.get(resources);
        } catch (IllegalAccessException e) {
        }

        if (resourcesImpl == null) {
            // If there is no impl instance, bail out now
            return false;
        }

        if (!sDrawableCacheFieldFetched) {
            try {
                sDrawableCacheField = resourcesImpl.getClass().getDeclaredField("mDrawableCache");
                sDrawableCacheField.setAccessible(true);
            } catch (NoSuchFieldException e) {
            }
            sDrawableCacheFieldFetched = true;
        }

        Object drawableCache = null;
        if (sDrawableCacheField != null) {
            try {
                drawableCache = sDrawableCacheField.get(resourcesImpl);
            } catch (IllegalAccessException e) {
            }
        }

        return drawableCache != null && flushThemedResourcesCache(drawableCache);
    }

    @RequiresApi(16)
    private static boolean flushThemedResourcesCache(@NonNull final Object cache) {
        if (!sThemedResourceCacheClazzFetched) {
            try {
                sThemedResourceCacheClazz = Class.forName("android.content.res" +
                        ".ThemedResourceCache");
            } catch (ClassNotFoundException e) {
            }
            sThemedResourceCacheClazzFetched = true;
        }

        if (sThemedResourceCacheClazz == null) {
            // If the ThemedResourceCache class isn't available, bail out now
            return false;
        }

        if (!sThemedResourceCache_mUnthemedEntriesFieldFetched) {
            try {
                sThemedResourceCache_mUnthemedEntriesField =
                        sThemedResourceCacheClazz.getDeclaredField("mUnthemedEntries");
                sThemedResourceCache_mUnthemedEntriesField.setAccessible(true);
            } catch (NoSuchFieldException ee) {
            }
            sThemedResourceCache_mUnthemedEntriesFieldFetched = true;
        }

        if (sThemedResourceCache_mUnthemedEntriesField == null) {
            // Didn't get mUnthemedEntries field, bail out...
            return false;
        }

        LongSparseArray unthemedEntries = null;
        try {
            unthemedEntries = (LongSparseArray)
                    sThemedResourceCache_mUnthemedEntriesField.get(cache);
        } catch (IllegalAccessException e) {
        }

        if (unthemedEntries != null) {
            unthemedEntries.clear();
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    static boolean flushJellyBeans(@NonNull final Resources resources) {
        if (!sDrawableCacheFieldFetched) {
            try {
                sDrawableCacheField = Resources.class.getDeclaredField("mDrawableCache");
                sDrawableCacheField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                // no-op
            }
            sDrawableCacheFieldFetched = true;
        }
        if (sDrawableCacheField != null) {
            LongSparseArray drawableCache = null;
            try {
                drawableCache = (LongSparseArray) sDrawableCacheField.get(resources);
            } catch (IllegalAccessException e) {
                // no-op
            }
            if (drawableCache != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    drawableCache.clear();
                    return true;
                }
            }
        }
        return false;
    }

}
