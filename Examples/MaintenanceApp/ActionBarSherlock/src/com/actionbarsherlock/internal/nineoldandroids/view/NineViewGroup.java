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
package com.actionbarsherlock.internal.nineoldandroids.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.actionbarsherlock.internal.nineoldandroids.view.animation.AnimatorProxy;

public abstract class NineViewGroup extends ViewGroup {
    private final AnimatorProxy mProxy;

    public NineViewGroup(Context context) {
        super(context);
        mProxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
    }
    public NineViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mProxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
    }
    public NineViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mProxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
    }

    @Override
    public void setVisibility(int visibility) {
        if (mProxy != null) {
            if (visibility == GONE) {
                clearAnimation();
            } else if (visibility == VISIBLE) {
                setAnimation(mProxy);
            }
        }
        super.setVisibility(visibility);
    }

    public float getAlpha() {
        if (AnimatorProxy.NEEDS_PROXY) {
            return mProxy.getAlpha();
        } else {
            return super.getAlpha();
        }
    }
    public void setAlpha(float alpha) {
        if (AnimatorProxy.NEEDS_PROXY) {
            mProxy.setAlpha(alpha);
        } else {
            super.setAlpha(alpha);
        }
    }
    public float getTranslationX() {
        if (AnimatorProxy.NEEDS_PROXY) {
            return mProxy.getTranslationX();
        } else {
            return super.getTranslationX();
        }
    }
    public void setTranslationX(float translationX) {
        if (AnimatorProxy.NEEDS_PROXY) {
            mProxy.setTranslationX(translationX);
        } else {
            super.setTranslationX(translationX);
        }
    }
    public float getTranslationY() {
        if (AnimatorProxy.NEEDS_PROXY) {
            return mProxy.getTranslationY();
        } else {
            return super.getTranslationY();
        }
    }
    public void setTranslationY(float translationY) {
        if (AnimatorProxy.NEEDS_PROXY) {
            mProxy.setTranslationY(translationY);
        } else {
            super.setTranslationY(translationY);
        }
    }
}
