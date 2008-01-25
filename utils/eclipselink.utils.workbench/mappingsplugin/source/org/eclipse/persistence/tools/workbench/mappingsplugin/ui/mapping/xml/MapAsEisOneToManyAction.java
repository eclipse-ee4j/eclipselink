/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.ChangeMappingTypeAction;


final class MapAsEisOneToManyAction
	extends ChangeMappingTypeAction
{
	MapAsEisOneToManyAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		initializeIcon("mapping.eisOneToMany");
		initializeText("MAP_AS_EIS_ONE_TO_MANY_ACTION");
		initializeMnemonic("MAP_AS_EIS_ONE_TO_MANY_ACTION");
		initializeToolTipText("MAP_AS_EIS_ONE_TO_MANY_ACTION.toolTipText");
	}

	protected MWMapping morphMapping(MWMapping mapping) {
		return mapping.asMWEisOneToManyMapping();
	}
	
	protected MWMapping addMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute) {
		return ((MWEisDescriptor) descriptor).addEisOneToManyMapping(attribute);
	}

	protected Class mappingClass() {
		return MWEisOneToManyMapping.class;
	}

}
