/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cisdi.qingzhu.webview.entity;

import androidx.annotation.StyleRes;

import com.cisdi.qingzhu.webview.R;

public final class ThemeSpec {

    @StyleRes
    public int themeId = R.style.WebTheme_QZ;

    private ThemeSpec() {
    }

    public static ThemeSpec getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static ThemeSpec getCleanInstance() {
        ThemeSpec selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }

    private void reset() {
        themeId = R.style.WebTheme_QZ;
    }

    private static final class InstanceHolder {
        private static final ThemeSpec INSTANCE = new ThemeSpec();
    }
}
