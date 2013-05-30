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
package com.actionbarsherlock.internal;

import org.junit.Test;

import static com.actionbarsherlock.internal.ActionBarSherlockCompat.cleanActivityName;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ManifestParsingTest {
    @Test
    public void testFullyQualifiedClassName() {
        String expected = "com.other.package.SomeClass";
        String actual = cleanActivityName("com.jakewharton.test", "com.other.package.SomeClass");
        assertThat(expected, equalTo(actual));
    }

    @Test
    public void testFullyQualifiedClassNameSamePackage() {
        String expected = "com.jakewharton.test.SomeClass";
        String actual = cleanActivityName("com.jakewharton.test", "com.jakewharton.test.SomeClass");
        assertThat(expected, equalTo(actual));
    }

    @Test
    public void testUnqualifiedClassName() {
        String expected = "com.jakewharton.test.SomeClass";
        String actual = cleanActivityName("com.jakewharton.test", "SomeClass");
        assertThat(expected, equalTo(actual));
    }

    @Test
    public void testRelativeClassName() {
        String expected = "com.jakewharton.test.ui.SomeClass";
        String actual = cleanActivityName("com.jakewharton.test", ".ui.SomeClass");
        assertThat(expected, equalTo(actual));
    }
}
