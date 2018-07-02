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

import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.uitools.app.ExtendedListValueModelWrapper;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;

/**
 *
 */
public class ComboBoxModelAdapterUITest2 extends ComboBoxModelAdapterUITest {

    public static void main(String[] args) throws Exception {
        new ComboBoxModelAdapterUITest2().exec(args);
    }

    public ComboBoxModelAdapterUITest2() {
        super();
    }

    /**
     * add a null to the front of the list
     */
    protected ListValueModel buildColorListHolder() {
        // the default is to prepend the wrapped list with a null item
        return new ExtendedListValueModelWrapper(super.buildColorListHolder());
    }

    /**
     * use a different model that allows the color to be set to null
     */
    protected TestModel buildTestModel() {
        return new TestModel2();
    }

    /**
     * convert null to some text
     */
    protected ListCellRenderer buildComboBoxRenderer() {
        return new SimpleListCellRenderer() {
            protected String buildText(Object value) {
                return (value == null) ? "<none selected>" : super.buildText(value);
            }
        };
    }


protected static class TestModel2 extends TestModel {
    /**
     * null is OK here
     */
    public void checkColor(String color) {
        if (color == null) {
            return;
        }
        super.checkColor(color);
    }
}

}
