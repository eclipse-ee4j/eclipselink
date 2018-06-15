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

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class SimpleContent
    extends Content
{
    // **************** Constructors ******************************************

    /** Toplink use only */
    protected SimpleContent() {
        super();
    }

    SimpleContent(ExplicitComplexTypeDefinition parent) {
        super(parent);
    }


    // **************** Behavior **********************************************

    boolean hasTextContent() {
        return true;
    }

    boolean containsWildcard() {
        return false;
    }


    // **************** TopLink methods ***************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SimpleContent.class);
        descriptor.getInheritancePolicy().setParentClass(Content.class);

        return descriptor;
    }
}
