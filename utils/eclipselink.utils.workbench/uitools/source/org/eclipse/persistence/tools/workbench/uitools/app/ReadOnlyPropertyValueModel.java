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
package org.eclipse.persistence.tools.workbench.uitools.app;

/**
 * Implementation of ValueModel that can be used for
 * returning a static value, but still allows listeners to be added.
 * Listeners will NEVER be notified of any changes, because there should be none.
 */
public class ReadOnlyPropertyValueModel
    extends AbstractReadOnlyPropertyValueModel
{
    /** The value. */
    protected Object value;

    private static final long serialVersionUID = 1L;


    /**
     * Construct a read-only PropertyValueModel for the specified value.
     */
    public ReadOnlyPropertyValueModel(Object value) {
        super();
        this.value = value;
    }


    // ********** ValueModel implementation **********

    /**
     * @see ValueModel#getValue()
     */
    public Object getValue() {
        return this.value;
    }

}
