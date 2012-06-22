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

import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaTypeDefinition;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeListIterator;


final class ElementDeclarationNodeStructure
	extends NamedSchemaComponentNodeStructure
	implements SpecificParticleNodeStructure
{
	// **************** Constructors ******************************************
	
	ElementDeclarationNodeStructure(MWElementDeclaration elementDeclaration) {
		super(elementDeclaration);
	}
	
	
	// **************** SchemaComponentNodeStructure contract *****************
	
	protected ListIterator componentDetails() {
		return new CompositeListIterator(this.nameDetails(), this.elementDetails());
	}
	
	
	// **************** NamedSchemaComponentNodeStructure contract ************
	
	Integer topLevelOrderIndex() {
		return new Integer(0);
	}
	
	// **************** ParticleTermNodeStructure contract ********************
	
	public void disengageParticle() {
		this.disengageComponent();
	}
	
	public ListIterator details(ListIterator particleDetails) {
		return new CompositeListIterator(this.nameDetails(), particleDetails, this.elementDetails());
	}
	
	
	// **************** Internal **********************************************
	
	private ListIterator elementDetails() {
		SchemaComponentDetail[] details = new SchemaComponentDetail[6];
		
		details[0] = this.buildTypeDetail();
		details[1] = this.buildSubstitutionGroupDetail();
		details[2] = this.buildAbstractDetail();
		details[3] = this.buildDefaultDetail();
		details[4] = this.buildFixedDetail();
		details[5] = this.buildNillableDetail();
		
		return CollectionTools.listIterator(details);
	}
	
	SchemaComponentDetail buildTypeDetail() {
		return new SchemaComponentQNamedDetail(this.getComponent()) {
			protected String getName() {
				return "type";
			}
			
			protected MWNamedSchemaComponent getQNamedComponent() {
				MWSchemaTypeDefinition type = ((MWElementDeclaration) this.component).getType();
				return (type.getName() == null) ? null : type;
			}
		};
	}
	
	SchemaComponentDetail buildSubstitutionGroupDetail() {
		return new SchemaComponentQNamedDetail(this.getComponent()) {
			protected String getName() {
				return "substitutionGroup";
			}
			
			protected MWNamedSchemaComponent getQNamedComponent() {
				return ((MWElementDeclaration) this.component).getSubstitutionGroup();
			}
		};
	}
	
	SchemaComponentDetail buildAbstractDetail() {
		return new SchemaComponentDetail(this.getComponent()) {
			protected String getName() {
				return "abstract";
			}
			
			protected String getValueFromComponent() {
				return String.valueOf(((MWElementDeclaration) this.component).isAbstract());
			}
		};
	}
	
	SchemaComponentDetail buildDefaultDetail() {
		return new SchemaComponentDetail(this.getComponent()) {
			protected String getName() {
				return "default";
			}
			
			protected String getValueFromComponent() {
				String defaultValue = ((MWElementDeclaration) this.component).getDefaultValue();
				return (defaultValue == null) ? "" : defaultValue;
			}
		};
	}
	
	SchemaComponentDetail buildFixedDetail() {
		return new SchemaComponentDetail(this.getComponent()) {
			protected String getName() {
				return "fixed";
			}
			
			protected String getValueFromComponent() {
				String fixedValue = ((MWElementDeclaration) this.component).getFixedValue();
				return (fixedValue == null) ? "" : fixedValue;
			}
		};
	}
	
	SchemaComponentDetail buildNillableDetail() {
		return new SchemaComponentDetail(this.getComponent()) {
			protected String getName() {
				return "nillable";
			}
			
			protected String getValueFromComponent() {
				return String.valueOf(((MWElementDeclaration) this.component).isNillable());
			}
		};
	}
}
