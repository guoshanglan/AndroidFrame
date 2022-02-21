/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zrlib.matisse.intermal.entity;

/**
 * 拍照图片保存策略
 */
public class CaptureStrategy {

    /**
     * 是否公共目录
     */
    public final boolean isPublic;
    /**
     * provider authorities
     */
    public final String authority;
    /**
     * 目录
     */
    public final String directory;

    public CaptureStrategy(boolean isPublic, String authority) {
        this(isPublic, authority, null);
    }

    public CaptureStrategy(boolean isPublic, String authority, String directory) {
        this.isPublic = isPublic;
        this.authority = authority;
        this.directory = directory;
    }
}
