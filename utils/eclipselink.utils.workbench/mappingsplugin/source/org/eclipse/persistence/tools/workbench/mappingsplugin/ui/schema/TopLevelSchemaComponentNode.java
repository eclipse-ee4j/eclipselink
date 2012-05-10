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
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWModelGroupDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSimpleTypeDefinition;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractTreeNodeValueModel;


final class TopLevelSchemaComponentNode 
	extends SchemaComponentNode 
	implements Comparable
{
	// **************** Constructors ******************************************
	
	TopLevelSchemaComponentNode(AbstractTreeNodeValueModel parent, MWNamedSchemaComponent component) {
		super(parent, component);
	}
	
	
	// **************** Initialization ****************************************
	
	protected SchemaComponentNodeStructure buildStructure(MWSchemaComponent component) {
		SchemaComponentNodeStructure structure;
		
		if (component instanceof MWElementDeclaration) {
			structure =new ElementDeclarationNodeStructure((MWElementDeclaration) component);
		}
		else if (component instanceof MWModelGroupDefinition) {
			structure =new ModelGroupDefinitionNodeStructure((MWModelGroupDefinition) component);
		}
		else if (component instanceof MWAttributeDeclaration) {
			structure =new AttributeDeclarationNodeStructure((MWAttributeDeclaration) component);
		}
		else if (component instanceof MWComplexTypeDefinition) {
			structure =new ComplexTypeDefinitionNodeStructure((MWComplexTypeDefinition) component);
		}
		else if (component instanceof MWSimpleTypeDefinition) {
			structure =new SimpleTypeDefinitionNodeStructure((MWSimpleTypeDefinition) component);
		}
		else {
			throw new IllegalArgumentException("Unsupported top level schema component.");
		}
		
		structure.addPropertyChangeListener(DISPLAY_STRING_PROPERTY, this.buildDisplayStringChangeListener());
		return structure;
	}
	
	
	// **************** Comparable contract ***********************************
	
	public int compareTo(Object o) {
		return TopLevelSchemaComponentNodeComparator.compare(this, o);
	}
	
	
	// **************** Member classes ****************************************
	
	
	private static class TopLevelSchemaComponentNodeComparator
	{
		/** 
		 * Sort nodes first by namespace (root namespace first, then alphabetically by prefix),
		 * then by node type
		 * then by prefixed name.
		 */
		public static int compare(Object o1, Object o2) {
			TopLevelSchemaComponentNode node1 = (TopLevelSchemaComponentNode) o1;
			TopLevelSchemaComponentNode node2 = (TopLevelSchemaComponentNode) o2;
			MWNamedSchemaComponent comp1 = (MWNamedSchemaComponent) node1.component;
			MWNamedSchemaComponent comp2 = (MWNamedSchemaComponent) node2.component;
			
			int comparison = compare(comp1.getTargetNamespace(), comp2.getTargetNamespace());
	
			if (comparison == 0) {
				comparison = compare((NamedSchemaComponentNodeStructure) node1.structure, 
									 (NamedSchemaComponentNodeStructure) node2.structure);
			}
			
			if (comparison == 0) {
				comparison = comp1.qName().compareTo(comp2.qName());
			}
			
			return comparison;
		}
		
		private static int compare(MWNamespace thisNamespace, MWNamespace otherNamespace) {
			if (thisNamespace.isTargetNamespace() && ! otherNamespace.isTargetNamespace()) {
				return -1;
			}
			else if (! thisNamespace.isTargetNamespace() && otherNamespace.isTargetNamespace()) {
				return 1;
			}
			else if(thisNamespace.getNamespacePrefix() != otherNamespace.getNamespacePrefix()) {
				return thisNamespace.getNamespacePrefix().compareToIgnoreCase(otherNamespace.getNamespacePrefix());
			}
			else {
				return 0;
			}
		}
		
		private static int compare(NamedSchemaComponentNodeStructure structure1, NamedSchemaComponentNodeStructure structure2) {
			return structure1.topLevelOrderIndex().compareTo(structure2.topLevelOrderIndex());
		}
	}
}
