/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;



final class GenerateTablesFromAllDescriptorsAction extends AbstractGenerateTablesFromDescriptorsAction {

	GenerateTablesFromAllDescriptorsAction(WorkbenchContext context) {
		super(context);
	}
	
	protected void initialize() {
		super.initialize();
		initializeTextAndMnemonic("ALL_DESCRIPTORS_LABEL_MENU_ITEM");
	//	initializeIcon("table.remove");
	}

	protected void execute() {
		generateTablesFromDescriptors(CollectionTools.collection(allDescriptors()));
	}
	
	private Iterator allDescriptors() {
		return getSelectedProject().descriptors();
	}
	
	private MWProject getSelectedProject() {
		return (MWProject) selectedNodes()[0].getProjectRoot().getValue();
		
	}

}
