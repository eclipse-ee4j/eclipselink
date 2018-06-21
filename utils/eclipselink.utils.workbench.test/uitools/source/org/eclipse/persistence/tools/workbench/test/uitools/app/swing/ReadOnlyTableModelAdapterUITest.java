/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.uitools.app.swing;

import org.eclipse.persistence.tools.workbench.test.uitools.app.swing.TableModelAdapterTests.PersonColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;


/**
 * Make it easy to test the table model adapter and
 * renderers without any editing allowed.
 */
public class ReadOnlyTableModelAdapterUITest extends TableModelAdapterUITest {

    public static void main(String[] args) throws Exception {
        new ReadOnlyTableModelAdapterUITest().exec(args);
    }

    protected ReadOnlyTableModelAdapterUITest() {
        super();
    }

    protected ColumnAdapter buildColumnAdapter() {
        return new PersonColumnAdapter() {
            public boolean isColumnEditable(int index) {
                return false;
            }
        };
    }

}
