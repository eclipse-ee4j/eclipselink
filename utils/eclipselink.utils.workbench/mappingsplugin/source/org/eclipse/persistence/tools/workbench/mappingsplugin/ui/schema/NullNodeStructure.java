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

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.NullParticle;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullListIterator;


final class NullNodeStructure 
	extends SchemaComponentNodeStructure 
	implements SpecificParticleNodeStructure
{
	// **************** Constructors ******************************************
	
	NullNodeStructure(NullParticle nullParticle) {
		super(nullParticle);
	}
	
	// **************** SchemaComponentNodeStructure contract *****************
	
	public String displayString() {
		return "null";
	}
	
	
	// **************** ParticleTermNodeStructure contract ********************
	
	public void disengageParticle() {
		this.disengageComponent();
	}
	
	public ListIterator details(ListIterator particleDetails) {
		return NullListIterator.instance();
	}
}
