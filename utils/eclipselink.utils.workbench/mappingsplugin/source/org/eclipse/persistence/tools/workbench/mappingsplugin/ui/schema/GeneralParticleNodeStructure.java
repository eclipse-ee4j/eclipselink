/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWModelGroup;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWParticle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.NullParticle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.Wildcard;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


final class GeneralParticleNodeStructure 
	extends SchemaComponentNodeStructure 
{
	private SpecificParticleNodeStructure specificParticleStructure;
	
	
	// **************** Constructors ******************************************
	
	GeneralParticleNodeStructure(MWParticle particle) {
		super(particle);
		this.specificParticleStructure = this.buildSpecificParticleStructure(particle);
	}
	
	
	// **************** Initialization ****************************************
	
	private SpecificParticleNodeStructure buildSpecificParticleStructure(MWParticle particle) {
		SpecificParticleNodeStructure specificParticleStructure;
		
		if (particle instanceof MWElementDeclaration) {
			specificParticleStructure = new ElementDeclarationNodeStructure((MWElementDeclaration) particle);
		}
		else if (particle instanceof MWModelGroup) {
			specificParticleStructure = new ModelGroupNodeStructure((MWModelGroup) particle);
		}
		else if (particle instanceof Wildcard) {
			specificParticleStructure = new WildcardNodeStructure((Wildcard) particle);
		}
		else if (particle instanceof NullParticle) {
			specificParticleStructure = new NullNodeStructure((NullParticle) particle);
		}
		else {
			throw new IllegalArgumentException("Unsupported particle type.");
		}
		
		specificParticleStructure.addPropertyChangeListener(Displayable.DISPLAY_STRING_PROPERTY, this.buildDisplayStringChangeListener());
		return specificParticleStructure;
	}
	
	private PropertyChangeListener buildDisplayStringChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				GeneralParticleNodeStructure.this.firePropertyChanged(DISPLAY_STRING_PROPERTY, evt.getOldValue(), evt.getNewValue());
			}
		};
	}
	
	
	// **************** SchemaComponentNodeStructure contract *****************
	
	void disengageComponent() {
		super.disengageComponent();
		this.specificParticleStructure.disengageParticle();
	}
	
	public String displayString() {
		return this.specificParticleStructure.displayString();
	}
	
	protected ListIterator componentDetails() {
		SchemaComponentDetail[] details = new SchemaComponentDetail[2];
		
		details[0] = this.buildMinOccursDetail();
		details[1] = this.buildMaxOccursDetail();
		
		return this.specificParticleStructure.details(CollectionTools.listIterator(details));
	}
	
	SchemaComponentDetail buildMinOccursDetail() {
		return new SchemaComponentDetail(this.getComponent()) {
			protected String getName() {
				return "minOccurs";
			}
			
			protected String getValueFromComponent() {
				return String.valueOf(((MWParticle) this.component).getMinOccurs());
			}
		};
	}
	
	SchemaComponentDetail buildMaxOccursDetail() {
		return new SchemaComponentDetail(this.getComponent()) {
			protected String getName() {
				return "maxOccurs";
			}
			
			protected String getValueFromComponent() {
				int maxOccurs = ((MWParticle) this.component).getMaxOccurs();
				return (maxOccurs == MWXmlSchema.INFINITY) ? "unbounded" : String.valueOf(maxOccurs);
			}
		};
	}
}
