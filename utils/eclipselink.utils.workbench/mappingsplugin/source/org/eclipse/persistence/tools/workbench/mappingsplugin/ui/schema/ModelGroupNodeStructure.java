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

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWModelGroup;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementListIterator;


final class ModelGroupNodeStructure 
	extends SchemaComponentNodeStructure 
	implements SpecificParticleNodeStructure
{
	// **************** Constructors ******************************************
	
	ModelGroupNodeStructure(MWModelGroup modelGroup) {
		super(modelGroup);
	}
	
	
	// **************** SchemaComponentNode contract **************************
	
	protected ListIterator componentDetails() {
		return new SingleElementListIterator(this.buildCompositorDetail());
	}
	
	public String displayString() {
		return ((MWModelGroup) this.getComponent()).getCompositor();
	}
	
	
	// **************** ParticleTermNodeStructure contract ********************
	
	public void disengageParticle() {
		this.disengageComponent();
	}
	
	public ListIterator details(ListIterator particleDetails) {
		return new CompositeListIterator(this.details(), particleDetails);
	}
	
	
	// **************** Internal **********************************************
	
	SchemaComponentDetail buildCompositorDetail() {
		return new SchemaComponentDetail(this.getComponent()) {
			protected String getName() {
				return "compositor";
			}
			
			protected String getValueFromComponent() {
				return ((MWModelGroup) this.component).getCompositor();
			}
		};
	}
}
