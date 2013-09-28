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
package com.actionbarsherlock.internal.view.menu;

import android.graphics.drawable.Drawable;
import android.view.View;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class SubMenuWrapper extends MenuWrapper implements SubMenu {
    private final android.view.SubMenu mNativeSubMenu;
    private MenuItem mItem = null;

    public SubMenuWrapper(android.view.SubMenu nativeSubMenu) {
        super(nativeSubMenu);
        mNativeSubMenu = nativeSubMenu;
    }


    @Override
    public SubMenu setHeaderTitle(int titleRes) {
        mNativeSubMenu.setHeaderTitle(titleRes);
        return this;
    }

    @Override
    public SubMenu setHeaderTitle(CharSequence title) {
        mNativeSubMenu.setHeaderTitle(title);
        return this;
    }

    @Override
    public SubMenu setHeaderIcon(int iconRes) {
        mNativeSubMenu.setHeaderIcon(iconRes);
        return this;
    }

    @Override
    public SubMenu setHeaderIcon(Drawable icon) {
        mNativeSubMenu.setHeaderIcon(icon);
        return this;
    }

    @Override
    public SubMenu setHeaderView(View view) {
        mNativeSubMenu.setHeaderView(view);
        return this;
    }

    @Override
    public void clearHeader() {
        mNativeSubMenu.clearHeader();
    }

    @Override
    public SubMenu setIcon(int iconRes) {
        mNativeSubMenu.setIcon(iconRes);
        return this;
    }

    @Override
    public SubMenu setIcon(Drawable icon) {
        mNativeSubMenu.setIcon(icon);
        return this;
    }

    @Override
    public MenuItem getItem() {
        if (mItem == null) {
            mItem = new MenuItemWrapper(mNativeSubMenu.getItem());
        }
        return mItem;
    }
}
