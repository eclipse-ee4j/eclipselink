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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;

import org.eclipse.persistence.descriptors.ClassDescriptor;


public final class MWNullDescriptorPolicy
    extends MWModel
    implements MWDescriptorPolicy, MWXmlNode
{
    protected MWNullDescriptorPolicy() {
        super();
    }

    public MWNullDescriptorPolicy(MWModel parent) {
        super(parent);
    }

    public MWMappingDescriptor getOwningDescriptor() {
        return (MWMappingDescriptor) this.getParent();
    }

    public void dispose() {
    }

    public MWDescriptorPolicy getPersistedPolicy() {
        return null;
    }

    public boolean isActive()
    {
        return false;
    }

    public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
        //nothing to set on the runtime descriptor
    }


    // **************** Model synchronization *********************************

    /** @see MWXmlNode#resolveXpaths() */
    public void resolveXpaths() {
        // Do nothing.  Null policy.
    }

    /** @see MWXmlNode#schemaChanged(SchemaChange) */
    public void schemaChanged(SchemaChange change) {
        // Do nothing.  Null policy.
    }
}
