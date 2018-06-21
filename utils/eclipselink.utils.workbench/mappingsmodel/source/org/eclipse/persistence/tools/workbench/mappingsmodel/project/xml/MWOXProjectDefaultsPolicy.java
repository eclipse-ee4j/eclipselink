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
package org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;

import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * This class describes the project defaults for an OX project.
 *
 * @version 10.1.3
 */
public final class MWOXProjectDefaultsPolicy extends MWProjectDefaultsPolicy
{
    public static XMLDescriptor buildDescriptor()
    {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWOXProjectDefaultsPolicy.class);
        descriptor.getInheritancePolicy().setParentClass(MWProjectDefaultsPolicy.class);

        return descriptor;
    }

    private MWOXProjectDefaultsPolicy()
    {
        super();
    }

    MWOXProjectDefaultsPolicy(MWOXProject parent)
    {
        super(parent);
    }
}
