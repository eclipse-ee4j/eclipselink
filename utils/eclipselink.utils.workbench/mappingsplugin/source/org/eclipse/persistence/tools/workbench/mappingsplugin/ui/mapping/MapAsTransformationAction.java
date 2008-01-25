/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;


public final class MapAsTransformationAction 
	extends ChangeMappingTypeAction
{
	public MapAsTransformationAction(WorkbenchContext context) {
		super(context);
	}
	
	protected void initialize() {
		super.initialize();
		this.initializeIcon("mapping.transformation");
		this.initializeText("MAP_AS_TRANSFORMATION_ACTION");
		this.initializeMnemonic("MAP_AS_TRANSFORMATION_ACTION");
		this.initializeToolTipText("MAP_AS_TRANSFORMATION_ACTION.toolTipText");	
	}

	protected MWMapping morphMapping(MWMapping mapping) {
		return mapping.asMWTransformationMapping();
	}
	
	protected MWMapping addMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute) {
		return descriptor.addTransformationMapping(attribute);
	}

	protected Class mappingClass() {
		return MWTransformationMapping.class;
	}

}
