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
package org.eclipse.persistence.tools.workbench.uitools.app;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;


/**
 * Implementation of PropertyValueModel that simply holds on to an
 * object and uses it as the value.
 */
public class SimplePropertyValueModel
    extends AbstractModel
    implements PropertyValueModel
{
    /** The value. */
    protected Object value;


    /**
     * Construct a PropertyValueModel for the specified value.
     */
    public SimplePropertyValueModel(Object value) {
        super();
        this.value = value;
    }

    /**
     * Construct a PropertyValueModel with a starting value of null.
     */
    public SimplePropertyValueModel() {
        this(null);
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#buildDefaultChangeSupport()
     */
    protected ChangeSupport buildDefaultChangeSupport() {
        return new ValueModelChangeSupport(this);
    }


    /**
     * @see ValueModel#getValue()
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * @see PropertyValueModel#setValue(Object)
     */
    public void setValue(Object value) {
        Object old = this.value;
        this.value = value;
        this.firePropertyChanged(VALUE, old, value);
    }

    public void toString(StringBuffer sb) {
        sb.append(this.value);
    }

}
