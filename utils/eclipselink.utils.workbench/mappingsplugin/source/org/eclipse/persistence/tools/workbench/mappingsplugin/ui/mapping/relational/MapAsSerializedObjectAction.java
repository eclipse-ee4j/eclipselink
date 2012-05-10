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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


final class MapAsSerializedObjectAction extends MapAsRelationalDirectMapping {

	public MapAsSerializedObjectAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		this.initializeIcon("mapping.serialized");
		this.initializeText("MAP_AS_SERIALIZED_OBJECT_ACTION");
		this.initializeMnemonic("MAP_AS_SERIALIZED_OBJECT_ACTION");
		this.initializeToolTipText("MAP_AS_SERIALIZED_OBJECT_ACTION.toolTipText");
	}

	protected MappingNode morphNode(MappingNode selectedNode) {
		MappingNode mappingNode = super.morphNode(selectedNode);
		((MWDirectMapping) mappingNode.getMapping()).setSerializedObjectConverter();
		return mappingNode;
	}

	protected String converterType() {
		return MWConverter.SERIALIZED_OBJECT_CONVERTER;
	}

}
