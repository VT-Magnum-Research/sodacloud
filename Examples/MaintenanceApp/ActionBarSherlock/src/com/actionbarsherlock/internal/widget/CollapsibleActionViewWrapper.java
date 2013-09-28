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

import android.view.View;
import android.widget.FrameLayout;
import com.actionbarsherlock.view.CollapsibleActionView;

/**
 * Wraps an ABS collapsible action view in a native container that delegates the calls.
 */
public class CollapsibleActionViewWrapper extends FrameLayout implements android.view.CollapsibleActionView {
    private final CollapsibleActionView child;

    public CollapsibleActionViewWrapper(View child) {
        super(child.getContext());
        this.child = (CollapsibleActionView) child;
        addView(child);
    }

    @Override public void onActionViewExpanded() {
        child.onActionViewExpanded();
    }

    @Override public void onActionViewCollapsed() {
        child.onActionViewCollapsed();
    }

    public View unwrap() {
        return getChildAt(0);
    }
}
