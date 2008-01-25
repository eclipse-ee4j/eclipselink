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
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.ChangeMappingTypeAction;


final class MapAsCompositeObjectAction 
	extends ChangeMappingTypeAction
{
	MapAsCompositeObjectAction(WorkbenchContext context) {
		super(context);
	}
	
	protected void initialize() {
		super.initialize();
		this.initializeIcon("mapping.compositeObject");
		this.initializeText("MAP_AS_COMPOSITE_OBJECT_ACTION");
		this.initializeMnemonic("MAP_AS_COMPOSITE_OBJECT_ACTION");
		this.initializeToolTipText("MAP_AS_COMPOSITE_OBJECT_ACTION.toolTipText");	
	}
	
	
	// ************ ChangeMappingTypeAction implementation ***********
	
	protected MWMapping morphMapping(MWMapping mapping) {
		return mapping.asMWCompositeObjectMapping();
	}
	
	protected MWMapping addMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute) {
		return ((MWXmlDescriptor) descriptor).addCompositeObjectMapping(attribute);
	}

	protected Class mappingClass() {
		return MWCompositeObjectMapping.class;
	}

}
