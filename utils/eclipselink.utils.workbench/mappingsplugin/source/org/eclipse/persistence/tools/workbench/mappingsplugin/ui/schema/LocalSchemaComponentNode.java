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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWAttributeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWParticle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaComponent;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractTreeNodeValueModel;


final class LocalSchemaComponentNode 
	extends SchemaComponentNode 
{
	// **************** Constructors ******************************************
	
	LocalSchemaComponentNode(AbstractTreeNodeValueModel parent, MWSchemaComponent component) {
		super(parent, component);
	}
	
	
	// **************** Initialization ****************************************
	
	protected SchemaComponentNodeStructure buildStructure(MWSchemaComponent component) {
		SchemaComponentNodeStructure structure;
		
		if (component instanceof MWAttributeDeclaration) {
			structure = new AttributeDeclarationNodeStructure((MWAttributeDeclaration) component);
		}
		else if (component instanceof MWParticle) {
			structure = new GeneralParticleNodeStructure((MWParticle) component);
		}
		else {
			throw new IllegalArgumentException("Unsupported local schema component.");
		}
		
		structure.addPropertyChangeListener(DISPLAY_STRING_PROPERTY, this.buildDisplayStringChangeListener());
		return structure;
	}
}
