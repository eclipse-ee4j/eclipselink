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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;


public abstract class MapAsDirectCollectionAction 
						extends ChangeMappingTypeAction
{
	public MapAsDirectCollectionAction(WorkbenchContext context) {
		super(context);
	}
	
	// ************ ChangeMappingTypeAction implementation ***********
	
	protected MWMapping morphMapping(MWMapping mapping) {
		return (MWMapping) mapping.asMWDirectCollectionMapping();
	}
	
	protected MWMapping addMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute) {
		return (MWMapping) descriptor.addDirectCollectionMapping(attribute);
	}


}
