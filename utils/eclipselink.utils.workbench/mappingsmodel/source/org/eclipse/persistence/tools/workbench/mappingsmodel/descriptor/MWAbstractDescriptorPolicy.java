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

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

public abstract class MWAbstractDescriptorPolicy extends MWModel implements MWDescriptorPolicy
{
    protected MWAbstractDescriptorPolicy() {    // private-protected
        // for TopLink use only
        super();
    }

    protected MWAbstractDescriptorPolicy(MWModel parent) {
        super(parent);
    }

    public MWMappingDescriptor getOwningDescriptor() {
        return (MWMappingDescriptor) this.getParent();
    }

    public void dispose()
    {
        // NO-OP here.  If an Advanced Policy requires
        // that clean-up duties be performed on the
        // owning descriptor prior to removal, override
        // this method and perform logic here.
        // do not forget to place a call to
        // super.cleanDescriptor() if this method is overridden.
    }

    /**
     * Default implementation.  There is nothing to persist initially.
     */
    public MWDescriptorPolicy getPersistedPolicy()
    {
        return null;
    }

    public boolean isActive()
    {
        return false;
    }
}
