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

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSimpleTypeDefinition;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationListIterator;


final class SimpleTypeDefinitionNodeStructure
	extends NamedSchemaComponentNodeStructure 
{
	// **************** Constructors ******************************************
	
	SimpleTypeDefinitionNodeStructure(MWSimpleTypeDefinition simpleTypeDefinition) {
		super(simpleTypeDefinition);
	}
	
	
	// **************** SchemaComponentNodeStructure contract *****************
	
	protected ListIterator componentDetails() {
		return new CompositeListIterator(this.nameDetails(), this.typeDetails());
	}
	
	
	// **************** NamedSchemaComponentNodeStructure contract ************
	
	Integer topLevelOrderIndex() {
		return new Integer(4);
	}
	
	
	// **************** Internal **********************************************
	
	private ListIterator typeDetails() {
		return new CompositeListIterator(
			this.buildVarietyDetail(),
			this.buildVarietySubdetails()
		);
	}
	
	private SchemaComponentDetail buildVarietyDetail() {
		return new SchemaComponentDetail(this.getComponent()) {
			protected String getName() {
				return "variety";
			}
			
			protected String getValueFromComponent() {
				return ((MWSimpleTypeDefinition) this.component).getVariety();
			}
		};
	}
	
	private ListIterator buildVarietySubdetails() {
		MWSimpleTypeDefinition simpleType = (MWSimpleTypeDefinition) this.getComponent();
		
		if (simpleType.getVariety() == MWSimpleTypeDefinition.ATOMIC) {
			return new SingleElementListIterator(this.buildBaseTypeDetail());
		}
		else if (simpleType.getVariety() == MWSimpleTypeDefinition.LIST) {
			return new SingleElementListIterator(this.buildItemTypeDetail());
		}
		else /** UNION */ {
			return this.buildMemberTypeDetails();
		}
	}
	
	private SchemaComponentDetail buildBaseTypeDetail() {
		return new SchemaComponentQNamedDetail(this.getComponent()) {
			protected String getName() {
				return "  base";
			}
			
			protected MWNamedSchemaComponent getQNamedComponent() {
				return ((MWSimpleTypeDefinition) this.component).getBaseType();
			}
		};
	}
	
	private SchemaComponentDetail buildItemTypeDetail() {
		return new SchemaComponentQNamedDetail(this.getComponent()) {
			protected String getName() {
				return "  itemType";
			}
			
			protected String getValueFromComponent() {
				String valueFromComponent = super.getValueFromComponent();
				
				if (((MWSimpleTypeDefinition) this.component).getItemType().getName() == null) {
					valueFromComponent = "base=" + valueFromComponent;
				}
				
				return valueFromComponent;
			}
			
			protected MWNamedSchemaComponent getQNamedComponent() {
				MWSimpleTypeDefinition itemType = ((MWSimpleTypeDefinition) this.component).getItemType();
				return (itemType.getName() == null) ? itemType.getBaseType() : itemType;
			}
		};
	}
	
	private ListIterator buildMemberTypeDetails() {
		return new TransformationListIterator(((MWSimpleTypeDefinition) this.getComponent()).memberTypes()) {
			protected Object transform(Object next) {
				return SimpleTypeDefinitionNodeStructure.this.buildMemberTypeDetail((MWSimpleTypeDefinition) next);
			}
		};
	}
	
	private SchemaComponentDetail buildMemberTypeDetail(final MWSimpleTypeDefinition memberType) {
		return new SchemaComponentQNamedDetail(this.getComponent()) {
			protected String getName() {
				return "  memberType";
			}
			
			protected String getValueFromComponent() {
				String valueFromComponent = super.getValueFromComponent();
				
				if (memberType.getName() == null) {
					valueFromComponent = "base=" + valueFromComponent;
				}
				
				return valueFromComponent;
			}
			
			protected MWNamedSchemaComponent getQNamedComponent() {
				return (memberType.getName() == null) ? memberType.getBaseType() : memberType;
			}
		};
	}
}
