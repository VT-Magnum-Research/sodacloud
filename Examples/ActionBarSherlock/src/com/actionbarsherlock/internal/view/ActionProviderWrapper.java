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
package com.actionbarsherlock.internal.view;

import com.actionbarsherlock.internal.view.menu.SubMenuWrapper;
import com.actionbarsherlock.view.ActionProvider;
import android.view.View;

public class ActionProviderWrapper extends android.view.ActionProvider {
    private final ActionProvider mProvider;


    public ActionProviderWrapper(ActionProvider provider) {
        super(null/*TODO*/); //XXX this *should* be unused
        mProvider = provider;
    }


    public ActionProvider unwrap() {
        return mProvider;
    }

    @Override
    public View onCreateActionView() {
        return mProvider.onCreateActionView();
    }

    @Override
    public boolean hasSubMenu() {
        return mProvider.hasSubMenu();
    }

    @Override
    public boolean onPerformDefaultAction() {
        return mProvider.onPerformDefaultAction();
    }

    @Override
    public void onPrepareSubMenu(android.view.SubMenu subMenu) {
        mProvider.onPrepareSubMenu(new SubMenuWrapper(subMenu));
    }
}
