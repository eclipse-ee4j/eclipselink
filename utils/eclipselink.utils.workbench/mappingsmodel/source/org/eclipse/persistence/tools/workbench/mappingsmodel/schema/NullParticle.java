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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import org.apache.xerces.impl.xs.XSParticleDecl;
import org.eclipse.persistence.oxm.XMLDescriptor;

public final class NullParticle
    extends AbstractParticle
{
    // **************** Static methods ****************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NullParticle.class);
        descriptor.getInheritancePolicy().setParentClass(AbstractParticle.class);

        return descriptor;
    }


    // **************** Constructors ******************************************

    /** Toplink use only */
    private NullParticle() {
        super();
    }

    NullParticle(AbstractSchemaModel parent) {
        super(parent);
    }


    // **************** MWParticle contract ***********************************

    public boolean isEquivalentTo(XSParticleDecl xsParticle) {
        return xsParticle == null;
    }
}
