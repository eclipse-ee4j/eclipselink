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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaTypeDefinition;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeListIterator;


final class ComplexTypeDefinitionNodeStructure
	extends NamedSchemaComponentNodeStructure 
{
	// **************** Constructors ******************************************
	
	ComplexTypeDefinitionNodeStructure(MWComplexTypeDefinition complexTypeDefinition) {
		super(complexTypeDefinition);
	}
	
	// **************** SchemaComponentNodeStructure contract *****************
	
	protected ListIterator componentDetails() {
		return new CompositeListIterator(this.nameDetails(), this.typeDetails());
	}
	
	
	// **************** NamedSchemaComponentNodeStructure contract ************
	
	Integer topLevelOrderIndex() {
		return new Integer(3);
	}
	
	
	// **************** Internal **********************************************
	
	private ListIterator typeDetails() {
		SchemaComponentDetail[] details = new SchemaComponentDetail[3];
		
		details[0] = this.buildBaseTypeDetail();
		details[1] = this.buildDerivationMethodDetail();
		details[2] = this.buildAbstractDetail();
		
		return CollectionTools.listIterator(details);
	}
	
	SchemaComponentDetail buildBaseTypeDetail() {
		return new SchemaComponentQNamedDetail(this.getComponent()) {
			protected String getName() {
				return "base";
			}
			
			protected MWNamedSchemaComponent getQNamedComponent() {
				MWSchemaTypeDefinition baseType = ((MWComplexTypeDefinition) this.component).getBaseType();
				return (baseType == null) ? null : baseType;
			}
		};
	}
	
	SchemaComponentDetail buildDerivationMethodDetail() {
		return new SchemaComponentDetail(this.getComponent()) {
			protected String getName() {
				return "derivation method";
			}
			
			protected String getValueFromComponent() {
				return ((MWComplexTypeDefinition) this.component).getDerivationMethod();
			}
		};
	}
	
	SchemaComponentDetail buildAbstractDetail() {
		return new SchemaComponentDetail(this.getComponent()) {
			protected String getName() {
				return "abstract";
			}
			
			protected String getValueFromComponent() {
				return String.valueOf(((MWComplexTypeDefinition) this.component).isAbstract());
			}
		};
	}
}
