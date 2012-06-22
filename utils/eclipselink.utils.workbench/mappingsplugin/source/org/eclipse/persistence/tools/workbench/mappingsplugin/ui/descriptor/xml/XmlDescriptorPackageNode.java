/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;


/**
 * Subclasses DescriptorPackageNode so that we can build a different menu
 */
public final class XmlDescriptorPackageNode extends DescriptorPackageNode {

	public XmlDescriptorPackageNode(String name, ProjectNode parent, DescriptorNodeBuilder descriptorNodeBuilder) {
		super(name, parent, descriptorNodeBuilder);
	}
	
	protected void addToMenuDescription(GroupContainerDescription menuDescription, WorkbenchContext context) {
				
		menuDescription.add(buildClassActionGroup(context));
		menuDescription.add(buildRemoveActionGroup(context));
		menuDescription.add(buildUnmapActionGroup(context));
		menuDescription.add(buildExportJavaSourceActionGroup(context));
		menuDescription.add(this.buildOracleHelpMenuGroup(context));

	}
	

}
