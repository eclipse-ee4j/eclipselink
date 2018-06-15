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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;


final class RemoveInheritedAttributesAction extends AbstractMapInheritedAttributesAction
{
    RemoveInheritedAttributesAction(WorkbenchContext context)
    {
        super(context);
    }

    protected void execute(MWMappingDescriptor descriptor) throws ClassNotFoundException
    {
        descriptor.removeInheritedAttributes();
    }

    protected void initialize()
    {
        super.initialize();
        initializeTextAndMnemonic("REMOVE_INHERITED_ATTRIBUTES_MENU_ITEM");
    }
}
