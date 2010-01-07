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
package org.eclipse.persistence.tools.workbench.mappingsmodel.generation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTypeNames;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NameTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Generation of descriptors from tables.
 */
public class MWDescriptorGenerator
implements MWTypeNames
{
   
    // instance variables to be set by the user of this class
	private volatile MWRelationalProject 	project;
	private Collection		tables;
	private volatile String 	packageName;
	private volatile boolean	generateBidirectionalRelationships;
	private volatile boolean    generateEjbs;
	private volatile boolean 	generateLocalInterfaces;	// should only be true if generateEjbs
	private volatile boolean 	generateRemoteInterfaces;	// should only be true if generateEjbs
	private volatile boolean 	generateMethodAccessors;	// should always be true if generateEjbs
	private Collection		relationshipsToCreate;			// must be MWRelationshipHolders between tables contained in "tables"

	// used internally
	private Map		tableToDescriptorDictionary;
	private Map		relationshipToMappingDictionary;
	
	
	public MWDescriptorGenerator() 
	{
		super();
		this.relationshipsToCreate = new Vector();
		this.tableToDescriptorDictionary = new HashMap();
		this.relationshipToMappingDictionary = new HashMap();
	}


	// Generate an attribute name.  Don't bother making it unique yet.
	private String defaultInstanceVariableName(MWRelationshipHolder relationshipHolder, MWClass sourceClass, String targetClassShortName) 
	{
		// try to get the name from the reference, if it has just one field
		if (! relationshipHolder.isForeignKeyInTargetTable() && relationshipHolder.getReference().columnPairsSize() == 1) 
		{
		    String columnName = ((MWColumnPair) relationshipHolder.getReference().columnPairs().next()).getSourceColumn().getName();
			columnName = columnName.replace(' ', '_');
			if (columnName.toUpperCase().endsWith("_ID")) {
				columnName = columnName.substring(0, columnName.length() - 3);
			}
			return StringTools.convertAllCapsToCamelBack(columnName, false);
		}
		return StringTools.uncapitalize(targetClassShortName);
	}
	
	// make sure that back pointers are created for 1-M's
	private void ensureBackPointersIncluded()
	{
		Vector backPointersToCreate = new Vector();
		for (Iterator relIt = this.relationshipsToCreate.iterator(); relIt.hasNext(); )
		{
			MWRelationshipHolder relHolder = (MWRelationshipHolder) relIt.next();
			if (relHolder.isOneToMany())
			{
				MWReference ref = relHolder.getReference();
				boolean foreignKeyInTargetTable = false;
				
				if (getRelationshipToCreate(ref, foreignKeyInTargetTable) == null)
				{
					MWRelationshipHolder backPointer = new MWRelationshipHolder(ref, foreignKeyInTargetTable);
					backPointer.setOneToOne();
					backPointersToCreate.add(backPointer);
				}
			}			
		}
		
		this.relationshipsToCreate.addAll(backPointersToCreate);
	}
	
	private void ensureSpecCompliance()
	{
		for (Iterator descriptors = this.tableToDescriptorDictionary.values().iterator(); descriptors.hasNext(); )
		{
			MWDescriptor desc = (MWDescriptor) descriptors.next();
		}
	}
	
	private void generateBidirectionalRelationships()
	{
		for (Iterator relationships = this.relationshipsToCreate.iterator(); relationships.hasNext(); )
		{
			MWRelationshipHolder relationship = (MWRelationshipHolder) relationships.next();
			MWRelationshipHolder relationshipPartner = 
				getRelationshipToCreate(relationship.getReference(), ! relationship.isForeignKeyInTargetTable());
			
			MWAbstractTableReferenceMapping tableRefMapping = (MWAbstractTableReferenceMapping) this.relationshipToMappingDictionary.get(relationship);
			MWAbstractTableReferenceMapping tableRefMappingPartner = (MWAbstractTableReferenceMapping) this.relationshipToMappingDictionary.get(relationshipPartner);
			
			if (tableRefMappingPartner != null)
			{
				tableRefMapping.setMaintainsBidirectionalRelationship(true);
				tableRefMapping.setRelationshipPartnerMapping(tableRefMappingPartner);
				tableRefMappingPartner.setMaintainsBidirectionalRelationship(true);
				tableRefMappingPartner.setRelationshipPartnerMapping(tableRefMapping);
			}
		}
	}
	
	public void generateClassesAndDescriptors() {
		ensureBackPointersIncluded(); // makes sure that 1-M relationships have backpointer relationships
		
		for (Iterator stream = this.tables.iterator(); stream.hasNext(); ) {
			MWTable table = (MWTable) stream.next();
			String typeName = table.getShortName();
			typeName = typeName.replace(' ', '_');
			typeName = StringTools.convertAllCapsToCamelBack(typeName);
			String ejbName = ClassTools.shortNameForClassNamed(typeName);
		
			if (this.packageName != null && this.packageName.length() != 0) {
				typeName = this.packageName + '.' + typeName;
			}
			
			if (this.getProject().getRepository().typeNamedIgnoreCase(typeName) != null) {
				typeName = generateUniqueCaseInsensitiveName(typeName);
			}
			
			if (this.generateEjbs) {
				typeName = typeName + "EJB";
			}
			MWClass type = this.getProject().typeNamed(typeName);
			type.clear();

			MWTableDescriptor descriptor;
			MWDescriptor existingDescriptor = this.getProject().descriptorForType(type);
			if (existingDescriptor != null) {
				getProject().removeDescriptor(existingDescriptor);
			}
			type.addZeroArgumentConstructor();
			try {
				descriptor = (MWTableDescriptor) this.getProject().addDescriptorForType(type);
			} catch (InterfaceDescriptorCreationException ex) {
				throw new RuntimeException(ex);
			}

			descriptor.setPrimaryTable(table);			
				
			this.tableToDescriptorDictionary.put(table, descriptor);
		}
			
		generateInstanceVariablesAndMappings();
		ensureSpecCompliance();
	}

	private void generateDirectMapping(MWTableDescriptor descriptor, MWColumn column) 
	{
		Collection mappings = descriptor.allWritableMappingsForField(column);
		
		// Don't generate for this field if it is used already (should be in a relationship in this case)
		//   unless it is a primary key (in which case, it will have a relationship and a direct mapping)
		if (! mappings.isEmpty() && ! column.isPrimaryKey()) 
			return;
			
		MWClassAttribute instVar = generateInstanceVariable(descriptor, column);
		
		if (column.isPrimaryKey()) {
			if (!CollectionTools.contains(((MWRelationalTransactionalPolicy) descriptor.getTransactionalPolicy()).getPrimaryKeyPolicy().primaryKeys(), column)) {
				((MWRelationalTransactionalPolicy) descriptor.getTransactionalPolicy()).getPrimaryKeyPolicy().addPrimaryKey(column);
			}
		}
		
		MWDirectToFieldMapping dtfMapping = (MWDirectToFieldMapping) descriptor.addDirectMapping(instVar);
		dtfMapping.setColumn(column);
	}

	private void generateDirectMappings() 
	{
		for (Iterator stream = this.tableToDescriptorDictionary.keySet().iterator(); stream.hasNext(); ) 
		{
			MWTable table = (MWTable) stream.next();
			MWTableDescriptor descriptor = (MWTableDescriptor) this.tableToDescriptorDictionary.get(table);
			
			for (Iterator fields = table.columns(); fields.hasNext(); )
				generateDirectMapping(descriptor, (MWColumn) fields.next()); 
		}
	}
	
	private MWClassAttribute generateInstanceVariable(MWTableDescriptor descriptor, String instanceVariableName, MWClass instanceVariableType)
	{
		return retrieveOrGenerateInstanceVariable(descriptor, instanceVariableName, instanceVariableType, 0);
	}
	
	private MWClassAttribute retrieveOrGenerateInstanceVariable(MWTableDescriptor descriptor, String instanceVariableName, MWClass instanceVariableType, int instanceVariableArrayDepth)
	{
		MWClassAttribute instanceVariable;
		instanceVariable = retrieveAttribute(descriptor, instanceVariableName, instanceVariableType, instanceVariableArrayDepth);
		if (instanceVariable == null) {
			instanceVariable = descriptor.getMWClass().addAttribute(instanceVariableName, instanceVariableType, instanceVariableArrayDepth);
		}
	
		instanceVariable.getModifier().setPrivate(true);
		return instanceVariable;
	}
	
	private MWClassAttribute retrieveEjb20Attribute(MWTableDescriptor descriptor, String instanceVariableName, MWClass instanceVariableType, int instanceVariableArrayDepth) {
		//check for existing instance variable that meet criteria first
		MWClassAttribute instanceVariable = descriptor.getMWClass().attributeNamedFromCombinedAll(instanceVariableName);
		if (instanceVariable != null) {
			if (descriptor.mappingForAttribute(instanceVariable) == null) {
				instanceVariable.setType(instanceVariableType);
				instanceVariable.setDimensionality(instanceVariableArrayDepth);
				instanceVariable.getModifier().setPrivate(true);
				instanceVariable.setEjb20Attribute(true);
			}
		} 
		return instanceVariable;
	}

	private MWClassAttribute retrieveAttribute(MWTableDescriptor descriptor, String instanceVariableName, MWClass instanceVariableType, int instanceVariableArrayDepth) {
		//check for existing instance variable that meet criteria first
		MWClassAttribute instanceVariable = descriptor.getMWClass().attributeNamedFromAll(instanceVariableName);
		if (instanceVariable != null) {
			if (descriptor.mappingForAttribute(instanceVariable) == null) {
				instanceVariable.setType(instanceVariableType);
				instanceVariable.setDimensionality(instanceVariableArrayDepth);
				instanceVariable.getModifier().setPrivate(true);
			}
		} 
		return instanceVariable;
	}

	private MWClassAttribute generateInstanceVariable(MWTableDescriptor descriptor, MWColumn column) 
	{
		String ivName = column.getName().replace(' ', '_');
		ivName = StringTools.convertAllCapsToCamelBack(ivName, false);
		ivName = NameTools.uniqueJavaNameFor(ivName, descriptor.getMWClass().attributeNames());
		MWClass instanceVariableType = this.project.typeNamed(column.javaTypeDeclaration().getJavaClassName());
		int instanceVariableArrayDepth = column.javaTypeDeclaration().getArrayDepth();
		MWClassAttribute instanceVariable = retrieveOrGenerateInstanceVariable(descriptor, ivName, instanceVariableType, instanceVariableArrayDepth);
		
		if (this.generateMethodAccessors)
			instanceVariable.generateAllAccessors();
		
		return instanceVariable;
	}
	
	private void generateInstanceVariablesAndMappings() 
	{
		generateRelationshipMappings();
		generateDirectMappings();
	}
	
	private MWOneToManyMapping generateOneToManyMapping(MWTableDescriptor sourceDescriptor, MWClassAttribute attribute) 
	{
		MWOneToManyMapping oneToManyMapping = sourceDescriptor.addOneToManyMapping(attribute);
		oneToManyMapping.setUseTransparentIndirection();
		return oneToManyMapping;
	}

	private MWOneToOneMapping generateOneToOneMapping(MWTableDescriptor sourceDescriptor, MWClassAttribute attribute) 
	{
		MWOneToOneMapping oneToOneMapping = sourceDescriptor.addOneToOneMapping(attribute);
		oneToOneMapping.setUseValueHolderIndirection();
		return oneToOneMapping;
	}

	private void generateReferenceMapping(MWRelationshipHolder relationshipHolder)
	{
		MWTableDescriptor sourceDescriptor = getDescriptorForTable(relationshipHolder.getRelationshipSourceTable());
		MWTableDescriptor targetDescriptor = getDescriptorForTable(relationshipHolder.getRelationshipTargetTable());
		MWClassAttribute newVariable = generateRelationshipInstanceVariable(relationshipHolder, sourceDescriptor, targetDescriptor);

		MWAbstractTableReferenceMapping referenceMapping;		
		if (relationshipHolder.isOneToOne()) 
			referenceMapping = generateOneToOneMapping(sourceDescriptor, newVariable);
		else 
			referenceMapping = generateOneToManyMapping(sourceDescriptor, newVariable);
		
		this.relationshipToMappingDictionary.put(relationshipHolder, referenceMapping);
		referenceMapping.setReferenceDescriptor(targetDescriptor);
		referenceMapping.setReference(relationshipHolder.getReference());
		
		if (relationshipHolder.isForeignKeyInTargetTable() && referenceMapping.isOneToOneMapping()) {
			for (Iterator i = relationshipHolder.getReference().columnPairs(); i.hasNext(); ) {
				((MWOneToOneMapping)referenceMapping).addTargetForeignKey((MWColumnPair) i.next());
			}
		}
	}

	
	private void generateRelationshipMappings() 
	{
		for (Iterator relationshipsToCreateIterator = this.relationshipsToCreate.iterator(); relationshipsToCreateIterator.hasNext(); ) 
		{
			MWRelationshipHolder relationshipHolder = (MWRelationshipHolder) relationshipsToCreateIterator.next();
			generateReferenceMapping(relationshipHolder);
		}
		
		// must wait until all relationship mappings have been generated
		if (this.generateBidirectionalRelationships)
			generateBidirectionalRelationships();
	}
	
	private String generateUniqueCaseInsensitiveName(String typeName) {
		int index = 0;
		boolean notUnique = true;
		String newTypeName = null;
		while (notUnique) {
			newTypeName = typeName + String.valueOf(++index);
			notUnique = this.getProject().getRepository().typeNamedIgnoreCase(newTypeName) != null;
		}
		return newTypeName;
	}
	
	private MWClassAttribute generateRelationshipInstanceVariable(MWRelationshipHolder relationshipHolder, 
																   MWTableDescriptor sourceDescriptor, 
																   MWTableDescriptor targetDescriptor) 
	{	
		MWClass sourceClass = sourceDescriptor.getMWClass();
		
		String targetClassShortName = targetDescriptor.getMWClass().shortName();
		
		if (targetClassShortName.endsWith("EJB"))
			targetClassShortName = targetClassShortName.substring(0, targetClassShortName.length() - 3);
		
		MWClass targetClass;
		
		targetClass = targetDescriptor.getMWClass();
		
		String attributeName = defaultInstanceVariableName(relationshipHolder, sourceClass, targetClassShortName);
		MWClass attributeType;
		MWClassAttribute attribute;
		
		if (relationshipHolder.isOneToMany())
		{
			// this name will never collide with a reserved word
			attributeName = NameTools.uniqueNameFor(attributeName + "Collection", sourceClass.attributeNames());
			attributeType = getCollectionClass();
			attribute = generateInstanceVariable(sourceDescriptor, attributeName, attributeType);
			attribute.setItemType(targetClass);
		}
		else
		{
			attributeName = NameTools.uniqueJavaNameFor(attributeName, sourceClass.attributeNames());
			attributeType = getValueHolderClass();
			attribute = generateInstanceVariable(sourceDescriptor, attributeName, attributeType);
			attribute.setValueType(targetClass);
		}
		
		if (this.generateMethodAccessors)
			attribute.generateAllAccessors();
		
		return attribute;
	}
	
	private MWClass getCollectionClass() 
	{
		return this.project.typeNamed(COLLECTION_TYPE_NAME);
	}
	
	private MWTableDescriptor getDescriptorForTable(MWTable table)
	{
		return (MWTableDescriptor) this.tableToDescriptorDictionary.get(table);
	}
	
	public String getPackageName()
	{
		return this.packageName;
	}
	
	public MWRelationalProject getProject()
	{
		return this.project;
	}
	
	private MWRelationshipHolder getRelationshipToCreate(MWReference reference, boolean foreignKeyInTargetTable)
	{
		for (Iterator relsToCreate = this.relationshipsToCreate.iterator(); relsToCreate.hasNext(); )
		{
			MWRelationshipHolder holder = (MWRelationshipHolder) relsToCreate.next();
			
			if (holder.getReference() == reference && holder.isForeignKeyInTargetTable() == foreignKeyInTargetTable)
				return holder;			
		}
		
		return null;
	}
	
	private MWClass getValueHolderClass() 
	{
		return this.project.typeNamed(VALUE_HOLDER_INTERFACE_TYPE_NAME);
	}
	
	public void setGenerateBidirectionalRelationships(boolean generateBidirectionalRelationships)
	{
		this.generateBidirectionalRelationships = generateBidirectionalRelationships;
	}
	
	public void setGenerateEjbs(boolean generateEjbs)
	{
		this.generateEjbs = generateEjbs;
	}
	
	public void setGenerateLocalInterfaces(boolean generateLocalInterfaces)
	{
		this.generateLocalInterfaces = generateLocalInterfaces;
	}
	
	public void setGenerateMethodAccessors(boolean generateAccessors)
	{
		this.generateMethodAccessors = generateAccessors;
	}
	
	public void setGenerateRemoteInterfaces(boolean generateRemoteInterfaces)
	{
		this.generateRemoteInterfaces = generateRemoteInterfaces;
	}
	
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	
	public void setProject(MWRelationalProject project)
	{
		this.project = project;
	}
	
	public void setRelationshipsToCreate(Collection relationshipsToCreate)
	{
		this.relationshipsToCreate = relationshipsToCreate;
	}
	
	public void setTables(Collection tables)
	{
		this.tables = tables;
	}
	
	public boolean shouldGenerateLocalInterfaces()
	{
		return this.generateLocalInterfaces;
	}
	
	public boolean shouldGenerateRemoteInterfaces()
	{
		return this.generateRemoteInterfaces;
	}
}
