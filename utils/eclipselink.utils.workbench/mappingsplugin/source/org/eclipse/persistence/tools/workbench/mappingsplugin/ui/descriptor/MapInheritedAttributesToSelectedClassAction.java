/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;


final class MapInheritedAttributesToSelectedClassAction extends AbstractMapInheritedAttributesAction
{
	MapInheritedAttributesToSelectedClassAction(WorkbenchContext context)
	{
		super(context);
	}

	protected void execute(MWMappingDescriptor descriptor) throws ClassNotFoundException
	{
		HierarchyClassSelector classSelector = new HierarchyClassSelector(descriptor.getMWClass(), getWorkbenchContext());
		classSelector.show();

		if (classSelector.wasConfirmed())
			descriptor.mapInheritedAttributesToClass(classSelector.getSelectedClass());
	}

	protected void initialize()
	{
		super.initialize();
		initializeTextAndMnemonic("MAP_INHERITED_ATTRIBUTES_TO_SELECTED_CLASS_MENU_ITEM");
	}
}