/*******************************************************************************
 * Copyright 2013 PAR Works, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.actionbarsherlock.internal.widget;

import java.util.Locale;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

public class CapitalizingButton extends Button {
    private static final boolean SANS_ICE_CREAM = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    private static final boolean IS_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;

    private static final int[] R_styleable_Button = new int[] {
        android.R.attr.textAllCaps
    };
    private static final int R_styleable_Button_textAllCaps = 0;

    private boolean mAllCaps;

    public CapitalizingButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R_styleable_Button);
        mAllCaps = a.getBoolean(R_styleable_Button_textAllCaps, true);
        a.recycle();
    }

    public void setTextCompat(CharSequence text) {
        if (SANS_ICE_CREAM && mAllCaps && text != null) {
            if (IS_GINGERBREAD) {
                setText(text.toString().toUpperCase(Locale.ROOT));
            } else {
                setText(text.toString().toUpperCase());
            }
        } else {
            setText(text);
        }
    }
}
