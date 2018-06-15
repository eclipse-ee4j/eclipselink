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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class MWEisDescriptorInheritancePolicy
    extends MWXmlDescriptorInheritancePolicy
{
    // **************** Constructors ******************************************

    /** For TopLink use only */
    private MWEisDescriptorInheritancePolicy() {
        super();
    }

    MWEisDescriptorInheritancePolicy(MWEisDescriptor descriptor) {
        super(descriptor);
    }


    // **************** Problem handling **************************************

    protected String descendantDescriptorTypeMismatchProblemString() {
        return ProblemConstants.DESCRIPTOR_EIS_INHERITANCE_DESCRIPTOR_TYPES_DONT_MATCH;
    }

    protected boolean checkDescendantsForDescriptorTypeMismatch() {
        for (Iterator stream = this.descendentDescriptors(); stream.hasNext(); ) {
            MWEisDescriptor currentDescriptor = (MWEisDescriptor) stream.next();

            if ((currentDescriptor.isRootDescriptor() != ((MWEisDescriptor) getOwningDescriptor()).isRootDescriptor())) {
                return true;
            }
        }

        return false;
    }


    // **************** TopLink methods ***************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.getInheritancePolicy().setParentClass(MWXmlDescriptorInheritancePolicy.class);
        descriptor.setJavaClass(MWEisDescriptorInheritancePolicy.class);
        return descriptor;
    }
}
