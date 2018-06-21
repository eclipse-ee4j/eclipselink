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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class KeyRefIdentityConstraint
    extends IdentityConstraint
{
    /** The key identity constraint used by this key ref identity constraint */
    private volatile String refer;

    /** Toplink use only */
    private KeyRefIdentityConstraint() {
        super();
    }

    KeyRefIdentityConstraint(MWModel parent) {
        super(parent);
    }
    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(KeyRefIdentityConstraint.class);
        descriptor.getInheritancePolicy().setParentClass(IdentityConstraint.class);

        descriptor.addDirectMapping("refer", "refer/text()");

        return descriptor;
    }

    void reload(org.apache.xerces.impl.xs.identity.IdentityConstraint idConstraint) {
        super.reload(idConstraint);

        this.refer = idConstraint.getRefKey().getName();

    }
}
