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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;


public class SimpleContactProject extends LegacySimpleContactProject {

    public SimpleContactProject() {
        super();
    }

    @Override
    public MWTableDescriptor initializePersonDescriptor(){
        MWTableDescriptor descriptor = super.initializePersonDescriptor();
        ((MWVariableOneToOneMapping) descriptor.mappingNamed("contact")).setUseProxyIndirection();

        return descriptor;
    }

}
