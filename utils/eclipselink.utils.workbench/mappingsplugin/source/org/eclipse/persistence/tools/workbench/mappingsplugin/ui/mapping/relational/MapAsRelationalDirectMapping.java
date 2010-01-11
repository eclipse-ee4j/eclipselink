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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverterMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MapAsDirectAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


abstract class MapAsRelationalDirectMapping extends MapAsDirectAction {

	public MapAsRelationalDirectMapping(WorkbenchContext context) {
		super(context);
	}

	protected String[] selectedPropertyNames() {
		return new String[] {MWConverterMapping.CONVERTER_PROPERTY};
	}

	protected abstract String converterType();
	
	
	/**
	 * return whether the specified node is already morphed
	 */
	protected boolean nodeIsMorphed(MappingNode mappingNode) {
		return super.nodeIsMorphed(mappingNode) &&
				((MWDirectMapping) mappingNode.getMapping()).getConverter().getType() == this.converterType();
	}
}
