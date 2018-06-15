/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.uitools.app.swing;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

/**
 * This javax.swing.ButtonModel can be used to keep a listener
 * (e.g. a JCheckBox) in synch with a PropertyValueModel that
 * holds a boolean.
 *
 * Maybe not the richest class in our toolbox, but it was the
 * victim of refactoring....  -bjv
 */
public class CheckBoxModelAdapter extends ToggleButtonModelAdapter {


    // ********** constructors **********

    /**
     * Constructor - the boolean holder is required.
     */
    public CheckBoxModelAdapter(PropertyValueModel booleanHolder, boolean defaultValue) {
        super(booleanHolder, defaultValue);
    }

    /**
     * Constructor - the boolean holder is required.
     * The default value will be false.
     */
    public CheckBoxModelAdapter(PropertyValueModel booleanHolder) {
        super(booleanHolder);
    }

}
