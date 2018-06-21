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
 * A read-only property value model for when you
 * don't need to support a value.
 */
public final class NullPropertyValueModel
    extends AbstractReadOnlyPropertyValueModel
{

    private static final long serialVersionUID = 1L;

    // singleton
    private static NullPropertyValueModel INSTANCE;

    /**
     * Return the singleton.
     */
    public static synchronized PropertyValueModel instance() {
        if (INSTANCE == null) {
            INSTANCE = new NullPropertyValueModel();
        }
        return INSTANCE;
    }

    /**
     * Ensure non-instantiability.
     */
    private NullPropertyValueModel() {
        super();
    }


    // ********** PropertyValueModel implementation **********

    /**
     * @see ValueModel#getValue()
     */
    public Object getValue() {
        return null;
    }


    // ********** Object overrides **********

    /**
     * @see Object#toString()
     */
    public String toString() {
        return "NullPropertyValueModel";
    }

    /**
     * Serializable singleton support
     */
    private Object readResolve() {
        return instance();
    }

}
