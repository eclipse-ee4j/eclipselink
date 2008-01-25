/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.Wildcard;

final class WildcardNodeStructure 
	extends SchemaComponentNodeStructure 
	implements SpecificParticleNodeStructure
{
	// **************** Constructors ******************************************
	
	WildcardNodeStructure(Wildcard wildcard) {
		super(wildcard);
	}
	
	// **************** SchemaComponentNodeStructure contract *****************
	
	public String displayString() {
		return "any";
	}
	
	// **************** ParticleTermNodeStructure contract ********************
	
	public void disengageParticle() {
		this.disengageComponent();
	}
	
	public ListIterator details(ListIterator particleDetails) {
		return particleDetails;
	}
}
