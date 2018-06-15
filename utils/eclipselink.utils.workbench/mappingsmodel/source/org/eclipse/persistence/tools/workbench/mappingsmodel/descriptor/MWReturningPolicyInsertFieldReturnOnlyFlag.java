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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ReturningPolicy;

/**
 * Associate a returning policy insert field with a "return only" flag;
 * the field is read-only and should never be null;
 * but the flag can change
 */
public abstract class MWReturningPolicyInsertFieldReturnOnlyFlag
    extends MWModel
{
    private volatile boolean returnOnly;
        public static final String RETURN_ONLY_PROPERTY = "returnOnly";


    // ********** constructors/initialization **********

    /** Default constructor - for TopLink use only */
    protected MWReturningPolicyInsertFieldReturnOnlyFlag() {
        super();
    }

    protected MWReturningPolicyInsertFieldReturnOnlyFlag(MWReturningPolicy parent) {
        super(parent);
    }

    protected void initialize(Node parent) {
        super.initialize(parent);
        this.returnOnly = false;
    }


    // ********** accessors **********

    public abstract MWDataField getField();

    public boolean isReturnOnly() {
        return this.returnOnly;
    }

    public void setReturnOnly(boolean returnOnly) {
        boolean old = this.returnOnly;
        this.returnOnly = returnOnly;
        this.firePropertyChanged(RETURN_ONLY_PROPERTY, old, returnOnly);
    }

    protected MWMappingDescriptor getOwningDescriptor() {
        return ((MWReturningPolicy) this.getParent()).getOwningDescriptor();
    }


    // *********** Runtime Conversion ********

    void adjustRuntimeReturningPolicy(ReturningPolicy returningPolicy) {
        // the field should never be null
        if (this.returnOnly) {
            returningPolicy.addFieldForInsertReturnOnly(this.getField().runtimeField());
        } else {
            returningPolicy.addFieldForInsert(this.getField().runtimeField());
        }
    }

    public void toString(StringBuffer sb) {
        sb.append(this.getField().fieldName());
        if (this.returnOnly) {
            sb.append(" - return only");
        }
    }

    /**
     * implement this so we sort predictably when saving to xml
     */
    public String displayString() {
        return this.getField().fieldName();
    }

}
