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
package org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWTransactionalProjectDefaultsPolicy;

import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * This class describes the project defaults for an EIS project.
 *
 * @version 10.1.3
 */
public final class MWEisProjectDefaultsPolicy extends MWTransactionalProjectDefaultsPolicy
{

    public static XMLDescriptor buildDescriptor()
    {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWEisProjectDefaultsPolicy.class);
        descriptor.getInheritancePolicy().setParentClass(MWTransactionalProjectDefaultsPolicy.class);

        return descriptor;
    }

    private MWEisProjectDefaultsPolicy()
    {
        super();
    }

    MWEisProjectDefaultsPolicy(MWEisProject parent)
    {
        super(parent);
    }

}
