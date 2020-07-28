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

import org.eclipse.persistence.tools.workbench.utility.NullModel;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Implementation of ValueModel that can be subclassed and used for
 * returning a static value, but still allows listeners to be added.
 * Listeners will NEVER be notified of any changes, because there should be none.
 * Subclasses need only implement the #getValue() method to
 * return the static value required by the client code. This class is
 * really only useful for simplifying the building of anonymous inner
 * classes that implement the PropertyValueModel interface:
 *     private PropertyValueModel buildAbstractModifierHolder() {
 *         return new AbstractReadOnlyPropertyValueModel() {
 *             public Object getValue() {
 *                 return Modifier.ABSTRACT;
 *             }
 *         };
 *     }
 */
public abstract class AbstractReadOnlyPropertyValueModel
    extends NullModel
    implements PropertyValueModel
{
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    protected AbstractReadOnlyPropertyValueModel() {
        super();
    }


    // ********** PropertyValueModel implementation **********

    /**
     * @see PropertyValueModel#setValue(Object)
     */
    public void setValue(Object value) {
        throw new UnsupportedOperationException();
    }


    // ********** Object overrides **********

    /**
     * @see Object#toString()
     */
    public String toString() {
        return StringTools.buildToStringFor(this, this.getValue());
    }

}
