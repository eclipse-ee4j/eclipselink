/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorNode;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;



final class GenerateTablesFromSelectedDescriptorsAction extends AbstractGenerateTablesFromDescriptorsAction {

	GenerateTablesFromSelectedDescriptorsAction(WorkbenchContext context) {
		super(context);
	}
	
	protected void initialize() {
		super.initialize();
		initializeTextAndMnemonic("SELECTED_DESCRIPTORS_MENU_ITEM");
	//	initializeIcon("table.remove");
	}

	protected void execute() {
		generateTablesFromDescriptors(CollectionTools.collection(selectedDescriptors()));
	}
	
	private Iterator selectedDescriptors() {
		return new TransformationIterator(CollectionTools.iterator(selectedNodes())) {
			protected Object transform(Object next) {
				return ((DescriptorNode) next).getDescriptor();
			}
		};
	}
}
