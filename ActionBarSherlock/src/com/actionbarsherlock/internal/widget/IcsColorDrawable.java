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

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * A version of {@link android.graphics.drawable.ColorDrawable} that respects bounds.
 */
public class IcsColorDrawable extends Drawable {
    private int color;
    private final Paint paint = new Paint();

    public IcsColorDrawable(int color) {
        this.color = color;
    }

    @Override public void draw(Canvas canvas) {
        if ((color >>> 24) != 0) {
            paint.setColor(color);
            canvas.drawRect(getBounds(), paint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (alpha != (color >>> 24)) {
            color = (color & 0x00FFFFFF) & (alpha << 24);
            invalidateSelf();
        }
    }

    @Override public void setColorFilter(ColorFilter colorFilter) {
        //Ignored
    }

    @Override public int getOpacity() {
        return color >>> 24;
    }
}
