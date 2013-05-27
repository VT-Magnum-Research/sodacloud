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

import static android.view.View.MeasureSpec.EXACTLY;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.LinearLayout;
import com.actionbarsherlock.R;

public class FakeDialogPhoneWindow extends LinearLayout {
    final TypedValue mMinWidthMajor = new TypedValue();
    final TypedValue mMinWidthMinor = new TypedValue();

    public FakeDialogPhoneWindow(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SherlockTheme);

        a.getValue(R.styleable.SherlockTheme_windowMinWidthMajor, mMinWidthMajor);
        a.getValue(R.styleable.SherlockTheme_windowMinWidthMinor, mMinWidthMinor);

        a.recycle();
    }

    /* Stolen from PhoneWindow */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        final boolean isPortrait = metrics.widthPixels < metrics.heightPixels;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        boolean measure = false;

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, EXACTLY);

        final TypedValue tv = isPortrait ? mMinWidthMinor : mMinWidthMajor;

        if (tv.type != TypedValue.TYPE_NULL) {
            final int min;
            if (tv.type == TypedValue.TYPE_DIMENSION) {
                min = (int)tv.getDimension(metrics);
            } else if (tv.type == TypedValue.TYPE_FRACTION) {
                min = (int)tv.getFraction(metrics.widthPixels, metrics.widthPixels);
            } else {
                min = 0;
            }

            if (width < min) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(min, EXACTLY);
                measure = true;
            }
        }

        // TODO: Support height?

        if (measure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
