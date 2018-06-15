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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;


/**
 * Subclasses DescriptorPackageNode so that we can build a different menu
 */
public final class RelationalDescriptorPackageNode
    extends DescriptorPackageNode {

    public RelationalDescriptorPackageNode(String name, ProjectNode parent, DescriptorNodeBuilder descriptorNodeBuilder) {
        super(name, parent, descriptorNodeBuilder);
    }

    protected void addToMenuDescription(GroupContainerDescription menuDescription, WorkbenchContext context) {

        menuDescription.add(buildClassActionGroup(context));
        menuDescription.add(buildRemoveActionGroup(context));
        menuDescription.add(buildUnmapActionGroup(context));
        menuDescription.add(buildAutomapActionGroup(context));
        menuDescription.add(buildExportJavaSourceActionGroup(context));
        menuDescription.add(this.buildOracleHelpMenuGroup(context));

    }

    protected MenuGroupDescription buildAutomapActionGroup(WorkbenchContext context) {
        MenuGroupDescription autoMapGroup = new MenuGroupDescription();
        autoMapGroup.add(this.getMappingsPlugin().getAutomapAction(context));
        return autoMapGroup;
    }
}
