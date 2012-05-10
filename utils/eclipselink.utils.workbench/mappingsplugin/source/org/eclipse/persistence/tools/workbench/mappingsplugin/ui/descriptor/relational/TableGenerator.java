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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.util.*;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWError;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.StatusDialog;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.utility.string.SimpleStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

class TableGenerator
{
	private WorkbenchContext workbenchContext;
	private Collection classDescriptors;
	private HashMap descriptorLookup;
	private HashMap tableLookup;
	private MWRelationalProject project;
	private Vector log;
	private HashMap relationshipTableLookup;
	private int tableCreatedCount;

	protected static HashMap primitiveToJavaTypeMap;
	protected final int ATTR_TYPE = 0;
	protected final int ITEM_TYPE = 1;

	TableGenerator(WorkbenchContext workbenchContext) {
		super();
		initialize(workbenchContext);
	}
	
	protected void initialize(WorkbenchContext workbenchContex) {
		this.workbenchContext = workbenchContex;
		this.descriptorLookup = new HashMap();
		this.tableLookup = new HashMap();
		this.relationshipTableLookup = new HashMap();
		this.log = new Vector();
	}
	
	public StatusDialog.Status generateTablesFromDescriptors(Collection descriptorCollection) {

		MWDescriptor firstDescriptor = (MWDescriptor) descriptorCollection.iterator().next();
		setProject( (MWRelationalProject) firstDescriptor.getProject() );
	
		// Sort descriptors, superclasses first. Class descriptors only
		setClassDescriptors( sortTableDescriptors(descriptorCollection) );
 
		runGenerateTables();
		generationCompleted();

		if (descriptorCollection.size() == 1)
			return StatusDialog.createStatus(firstDescriptor, this.log);

		return StatusDialog.createStatus(this.project, this.log);
	}
	private void generationCompleted() {
		if (this.log.isEmpty()) {
			this.log.add(new MWError("TABLE_GENERATOR_SUCCESSFUL"));
		}
	}
	protected void runGenerateTables() {
		//try {
			// PASS 1: CREATE TABLES FOR DESCRIPTORS
//			int numberOfDescriptors = getClassDescriptors().size();
			int count = 0;
			for (Iterator descriptors = getClassDescriptors().iterator(); descriptors.hasNext(); ) {	
				MWTableDescriptor descriptor = (MWTableDescriptor) descriptors.next();
//				MWClass bldrClass = descriptor.getMWClass();
				//getProgress().setStatus(resourceRepository().getString("creatingTablesFor", new Object[] { bldrClass.shortName() }));
				count++;
				createTableIfAbsent(descriptor);
				descriptor.setActive(true);
//				if((int)(((float)count / (float)numberOfDescriptors) * 100) != 100) {
					//getProgress().setPercentComplete((int)(((float)count / (float)numberOfDescriptors) * 100));
//				}
			}
	
			// PASS 2: NON-COLLECTION VARIABLES
			// 	-- non-collection variables are done before collections so that OneToOne backpointers can
			//		be created before their corresponding One-To-Many mappings.
			count = 0;
			//getProgress().setStatus(resourceRepository().getString("mappingNonCollectionVariables"));
			for (Iterator descriptors = getClassDescriptors().iterator(); descriptors.hasNext(); ) {
				MWTableDescriptor descriptor = (MWTableDescriptor) descriptors.next();
	
				for (Iterator attributes = descriptor.getMWClass().attributes(); attributes.hasNext(); ) {
					MWClassAttribute attribute = (MWClassAttribute)attributes.next();
					MWMapping mapping = descriptor.mappingForAttribute(attribute);
					if (mapping == null) {
						descriptor.addDirectMapping(attribute);
					}
				}
				for (Iterator mappings = descriptor.mappings(); mappings.hasNext(); ) {
					MWMapping mapping = (MWMapping) mappings.next();					
					MWClass sourceClass = mapping.getParentDescriptor().getMWClass();
					/*MWMapping newMapping = */generateForNonCollectionMapping(mapping, sourceClass);
				}
				count++;
//				if((int)(((float)count / (float)numberOfDescriptors) * 100) != 100) {
					//getProgress().setPercentComplete((int)(((float)count / (float)numberOfDescriptors) * 100));
//				}
			}
	
			// PASS 3: COLLECTION VARIABLES
			count = 0;
			//getProgress().setStatus(resourceRepository().getString("mappingCollectionVariables"));
			for (Iterator descriptors = getClassDescriptors().iterator(); descriptors.hasNext(); ) {
				MWTableDescriptor descriptor = (MWTableDescriptor) descriptors.next();
	
				for (Iterator mappings = descriptor.mappings(); mappings.hasNext(); ) {
					MWMapping mapping = (MWMapping) mappings.next();
					MWClass sourceClass = mapping.getParentDescriptor().getMWClass();
					/*MWMapping newMapping = */generateForCollectionMapping( mapping, sourceClass );
				}
				count++;
//				if((int)(((float)count / (float)numberOfDescriptors) * 100) != 100) {
					//getProgress().setPercentComplete((int)(((float)count / (float)numberOfDescriptors) * 100));
//				}
			}
		
		//} catch (Exception e) {
			//ThrowableDialog.show( getMainView(), e);
			//getProgress().setPercentComplete(100);
			
		//}
		
		//getProgress().setPercentComplete(100);
	}
	
	
	protected MWOneToOneMapping backpointerFor(MWMapping mapping, MWClass[] attrAndItemType, MWClass sourceClass) {
		// returnsWith: <Collection element: <BLDRVariableDefinition>>"
	
		MWClass itemType = attrAndItemType[this.ITEM_TYPE];
		MWMappingDescriptor itemDescriptor = locateDescriptorFor(itemType);
	
		// check the attributes of itemType for a backpointer
		for (Iterator stream = itemType.attributes(); stream.hasNext(); ) {
			MWClassAttribute attribute = (MWClassAttribute) stream.next();
			if (attribute.getType() == sourceClass || (attribute.isValueHolder() && attribute.getValueType() == sourceClass)) {
				MWMapping backMapping = itemDescriptor.mappingNamed(attribute.getName());
				if (backMapping == null || attribute.getType().isAssignableToCollection()) {
					continue;
				}
				if (backMapping instanceof MWOneToOneMapping) {
					return (MWOneToOneMapping) backMapping;
				}
				return backMapping.asMWOneToOneMapping();
			}
		}
	
		// if we can't find a backpointer in itemType, then look for a mapping in its descriptor
		for (Iterator stream = itemDescriptor.mappings(); stream.hasNext(); ) {
			MWMapping itemMapping = (MWMapping) stream.next();
			if (itemMapping instanceof MWOneToOneMapping) {
				MWOneToOneMapping oneToOne = (MWOneToOneMapping) itemMapping;
				if (oneToOne.getReferenceDescriptor().getMWClass() == sourceClass) {
					return oneToOne;
				}
			}
		}
			
		return null;
	}
	protected Collection copyInheritedPrimaryKeys( MWTableDescriptor superclassDesc, MWTableDescriptor subclassDesc ) {
		
		// find key columns
		MWTableDescriptor definingDescriptor = superclassDesc;
		boolean definingDescriptorFound = false;
	
		while (! definingDescriptorFound) { 
				// walk up class hierarchy looking for primary key
				MWTableDescriptor superD = locateDescriptorFor( definingDescriptor.getMWClass().getSuperclass() );
				if ( superD == null ) {
					definingDescriptorFound = true;
				} else {
					definingDescriptor = superD;
				}
		}
	
		if (!definingDescriptor.getInheritancePolicy().isActive()) {
	 		definingDescriptor.addInheritancePolicy();
		}
		((MWDescriptorInheritancePolicy) definingDescriptor.getInheritancePolicy()).setIsRoot(true);
	
		if (definingDescriptor.primaryKeysSize() == 0) {
			logError(resourceRepository().getString("unableToFindKeyFieldsForInSuperclasses", new Object[] { subclassDesc.getMWClass().shortName() }));
			ArrayList array = new ArrayList();
			array.add(fabricateKeyFor( subclassDesc.getMWClass(), getTableFor(subclassDesc.getMWClass()) ) );
			return array;
		}
	
		MWTable primaryTable = subclassDesc.getPrimaryTable();
			
		for (Iterator stream = definingDescriptor.primaryKeys(); stream.hasNext(); ) {
			MWColumn primaryKey = (MWColumn) stream.next();
			if (! primaryTable.containsColumnNamed(primaryKey.getName())) {
				((MWRelationalTransactionalPolicy) subclassDesc.getTransactionalPolicy()).getPrimaryKeyPolicy().addPrimaryKey(primaryTable.addColumnLike(primaryKey));
			}
		}
	
		if (!subclassDesc.getInheritancePolicy().isActive()) {
			subclassDesc.addInheritancePolicy();
		}
		MWDescriptorInheritancePolicy subInheritPolicy = (MWDescriptorInheritancePolicy) subclassDesc.getInheritancePolicy();
		MWClassIndicatorPolicy subIndicatorPolicy = subInheritPolicy.getClassIndicatorPolicy();
		subInheritPolicy.setIsRoot(false);
		subInheritPolicy.setParentDescriptor( superclassDesc );
	
		//MWInheritancePolicy defInheritPolicy = definingDescriptor.getInheritancePolicy();
		//MWClassIndicatorPolicy defIndicatorPolicy = defInheritPolicy.getClassIndicatorPolicy();
			
		// add indicator field and mapping to superclass descriptor
		//String indicatorFieldName = defaultAbstractClassIndicatorFieldName();
		//if (primaryTable.getFieldNamed( indicatorFieldName ) == null) {
		//	MWDatabasePlatform platform = getDatabase().getPlatform();
		//	MWDatabaseField indicatorField = createColumnNamedForJavaClassNamed( indicatorFieldName, "java.lang.String");
		//	primaryTable.addField( indicatorField );
		//	defIndicatorPolicy.setField( indicatorField);
		//	defIndicatorPolicy.addIndicator( definingDescriptor.getShortName(), definingDescriptor );
		//}	
	
		//defIndicatorPolicy.addIndicator( subclassDesc.getShortName(), subclassDesc );
	
		return null;
	}
	protected HashMap copyPrimaryKeyColumns( MWTable sourceTable, MWTable targetTable ) {
		// Copy key columns from one table to another.
		// The new columns are created as both primary keys and foreign keys."
		// Returns a HashMap of sourceTable column names to the corresponding column name in the target table.
	
		HashMap columnNameMap = new HashMap();
			
		for (Iterator stream = sourceTable.primaryKeyColumns(); stream.hasNext(); ) {
			MWColumn keyColumn = (MWColumn) stream.next();
			MWColumn keyColumnCopy = targetTable.addColumnLike(keyColumn);
			
			keyColumnCopy.setName(newColumnName( targetTable, sourceTable.getName() + "_" + keyColumn.getName()));


			columnNameMap.put( keyColumn.getName(), keyColumnCopy.getName() );
				
			keyColumnCopy.setPrimaryKey(false);
		}
	
		return columnNameMap;
	}
	protected MWColumn createColumn(String potentialName, MWTable sourceTable, MWColumn foreignColumn) {
		String newColumnName = newColumnName(sourceTable, potentialName);
		MWColumn newColumn = sourceTable.addColumn(newColumnName);
		newColumn.setDatabaseType(foreignColumn.getDatabaseType());
	
		return newColumn;
	}
	protected MWColumn createColumn(String name, String javaClassName, int arrayDepth, MWTable table) {
		// this should only be called for database types.
		// for collection variables, this will create a column for the item class."
		String columnName = newColumnName(table, name);
		DatabaseType dbType = null;
		try {
			dbType = getDatabase().getDatabasePlatform().databaseTypeForJavaTypeDeclaration(javaClassName, arrayDepth);
		}catch (IllegalArgumentException iae){
//			if (iae.getMessage().endsWith("java.lang.Object")) {
				dbType = getDatabase().getDatabasePlatform().defaultDatabaseType();
//			}
//			else throw iae;
		}
		MWColumn column = table.addColumn(columnName);
		column.setDatabaseType(dbType);
	
		return column;
	}
	protected MWColumn createPrimaryKeyColumn(String fieldName, String javaClassName, int arrayDepth, MWTable table) {
		MWColumn column = createColumn(fieldName, javaClassName, arrayDepth, table);
		column.setPrimaryKey(true);
		return column;
	}
	protected void createPrimaryKeyFields(MWClass bldrClass, MWTable table) {
		Collection keyColumns = new ArrayList();
	
//		MWTableDescriptor subclassDescriptor = getDescriptorFor(bldrClass);
		
		// check my superclass for key fields
		MWTableDescriptor superclassDescriptor = locateDescriptorFor(bldrClass.getSuperclass());
	
		if (superclassDescriptor != null) {
			Collection parentKeys = copyInheritedPrimaryKeys(superclassDescriptor, getDescriptorFor(bldrClass));
			if (parentKeys == null) {
				return;
			}
		}
				
		// try to guess the primary key
		if (keyColumns.isEmpty()) {
			keyColumns.addAll(defaultKeysFor(bldrClass, table));
		}
			
		// if still empty, then we need to create a primary key
		if (keyColumns.isEmpty()) {
			// default name not found
			keyColumns.add(fabricateKeyFor(bldrClass, table));
		}
	
		for (Iterator stream = keyColumns.iterator(); stream.hasNext(); ) {
			Object[] varAndColumn = (Object[]) stream.next();
			MWClassAttribute attribute = (MWClassAttribute) varAndColumn[0];
			MWColumn column = (MWColumn) varAndColumn[1];
			MWTableDescriptor descriptor = getDescriptorFor(bldrClass);
			MWMapping existingMapping = descriptor.mappingForAttribute(attribute);
			if (existingMapping != null) {
				descriptor.removeMapping(existingMapping);
			}
			MWDirectToFieldMapping mapping = (MWDirectToFieldMapping) descriptor.addDirectMapping(attribute);
			mapping.setColumn(column);
			mapping.setUsesMethodAccessing(getProject().getDefaultsPolicy().isMethodAccessing());

			((MWRelationalTransactionalPolicy) descriptor.getTransactionalPolicy()).getPrimaryKeyPolicy().addPrimaryKey(column);
		}
	}
	protected MWReference createReferenceIfAbsent(MWTable sourceTable, MWTable targetTable, HashMap keyColumnTranslations) {
		String nameOfReference = sourceTable.getName() + "_" + targetTable.getName();
		MWReference reference = sourceTable.referenceNamed(nameOfReference);
		if (reference != null) {
			return reference;
		}
	
		reference = sourceTable.addReference(nameOfReference, targetTable);

	
		// populate the field associations in the reference
		for (Iterator stream = targetTable.primaryKeyColumns(); stream.hasNext(); ) {
			MWColumn targetColumn = (MWColumn) stream.next();
			
			String sourceColumnName = targetColumn.getName();
			if (keyColumnTranslations != null && keyColumnTranslations.get(targetColumn.getName()) != null) {
				sourceColumnName = (String) keyColumnTranslations.get(targetColumn.getName());
			}
			
			MWColumn joinColumn = sourceTable.columnNamed(sourceColumnName);
			reference.addColumnPair(joinColumn, targetColumn);
		}
	
		return reference;
	}
	protected MWTable createTableIfAbsent(MWTableDescriptor descriptor) {
		// returnsWith: <BLDRDatabaseTable> - find the existing table or create a new one"
	
		MWClass bldrClass = descriptor.getMWClass();
	
		if (bldrClass.attributesSize() == 0 && descriptor.mappingsSize() == 0) {
			if(bldrClass.getSuperclass() != null) {
				MWTableDescriptor parentDescriptor = getDescriptorFor(bldrClass.getSuperclass());
				if (parentDescriptor != null)
					descriptor.setPrimaryTable(parentDescriptor.getPrimaryTable());
			}
			return null;
		}
		
		MWTable table = descriptor.getPrimaryTable();
		if (table == null) {
			String proposedName;
			proposedName = bldrClass.defaultTableNameWithLength(getTableNameLength());
			table = getDatabase().addTable(newTableName(proposedName));
			this.tableCreatedCount++;
			descriptor.setPrimaryTable(table);
		}
		
		this.tableLookup.put(bldrClass, table);
	
		// generate primary keys, if absent
		if (!table.primaryKeyColumns().hasNext()) {
			createPrimaryKeyFields(bldrClass, table);
		}
		
		return table;
	}
	public static String defaultAbstractClassIndicatorFieldName() {
		return "SUBCLASS";
	}
	protected Collection defaultKeysFor(MWClass bldrClass, MWTable table) {
	
		String pkSearchPattern = getProject().getTableGenerationPolicy().getPrimaryKeySearchPattern();
		
		Collection result = new ArrayList();
		for (Iterator stream = bldrClass.attributes(); stream.hasNext(); ) {
			MWClassAttribute attribute = (MWClassAttribute) stream.next();
			if (new SimpleStringMatcher(pkSearchPattern, true).matches(attribute.getName())) {
				MWColumn column = createPrimaryKeyColumn(attribute.getName(), attribute.typeName(), attribute.getDimensionality(), table); 
				if (column != null) {
					result.add( new Object[] { attribute, column } );
				} 
			}
		}
		//try superclass attributes, do this after going through the non-inherited attributes so that they will
		//be resolved first.  fix for bug #4008198
		if (result.isEmpty()) {
			for (Iterator stream = bldrClass.allAttributes(); stream.hasNext(); ) {
				MWClassAttribute attribute = (MWClassAttribute) stream.next();
				if (new SimpleStringMatcher(pkSearchPattern, true).matches(attribute.getName())) {
					getDescriptorFor(bldrClass).addInheritedAttribute(attribute);
					MWColumn column = createPrimaryKeyColumn(attribute.getName(), attribute.typeName(), attribute.getDimensionality(), table); 
					if (column != null) {
						result.add( new Object[] { attribute, column } );
					} 
				}
			}
		}
		
		return result; 
	}
	
	
	/** Determine the type of the attribute and it's itemType, if it is a collection.
		Try to get as much information from the attribute, but if that is incomplete,
		then get the rest from the mapping.  ValueHolders are a special case of this 
		because the attribute can't know the itemType for an indirect collection that
		is using a ValueHolder. (This is because the attrType is ValueHolder and
		itemType is just a collection, so the attribute doesn't know what's in the 
		collection.) 
		
		Anyway, we hope that the user has already chosen an appropriate
		mapping and we can capitalize on this.
	**/
	protected MWClass[] determineAttrAndItemType(MWMapping mapping, boolean firstPass) {
	
		MWClassAttribute attribute = mapping.getInstanceVariable();
		if (attribute == null) {
			return new MWClass[] { null, null };
		}
	
		MWClass attrType = null;
		MWClass itemType = null;
	
		// if the attribute uses a ValueHolder, then attribute.getType()
		// will return a VH.  That's not useful to us.
		if (attribute.isValueHolder()) {
			//value type has been set, return that because its useful
			if (!attribute.getValueType().isObject()) {
				attrType = attribute.getValueType();
				if (attrType.isAssignableToCollection()) {
					itemType = attribute.getItemType();
				}
			//it has not been set log message
			} else {
				if (firstPass) {
					logUrgent(resourceRepository().getString("VALUE_HOLDER_TYPE_SELECT", new Object[] {attribute.getName(), attribute.getDeclaringType().getName() }));
				} 
				attrType = attribute.getType();
			}
		} else {
			attrType = attribute.getType();
			itemType = attribute.getItemType();
		}
	
	
		// if we are missing information, then see if the mapping can help	
		if (attrType == null || itemType == null) {
			
			// MWCollectionMapping
			if (mapping != null) {
				if (mapping.isCollectionMapping()) {
					MWCollectionMapping collectionMapping = (MWCollectionMapping) mapping;
					if (attrType == null) {
						attrType = collectionMapping.getContainerPolicy().getDefaultingContainerClass().getContainerClass();
					}
					if (itemType == null && collectionMapping.getReferenceDescriptor() != null) {
						itemType = collectionMapping.getReferenceDescriptor().getMWClass();
					}
				}
				// MWReferenceMapping
				else if (mapping instanceof MWReferenceMapping) {
					MWReferenceMapping referenceMapping = (MWReferenceMapping) mapping;
					if (attrType == null && referenceMapping.getReferenceDescriptor() != null) {
						attrType = referenceMapping.getReferenceDescriptor().getMWClass();
					}
				}
			}
		}
	
		return new MWClass[] { attrType, itemType };
	}
	protected static boolean directCollectionContentsAreSorted() {
		// There are really three options for direct collections: either
		// 1. their contents are sorted implying that their position in the array can be recalculated, 
		// 2. their contents are unsorted, but position-dependent
		// 3. their contents are unsorted, but position-independent.
		// If the contents are unsorted, it is harmless to retiain the original order (by saving each item's index)."
	
		return true;
	}
	protected static boolean directCollectionContentsAreUnsorted() {
		// There are really three options for direct collections: either
		// 1. their contents are sorted implying that their position in the array can be recalculated, 
		// 2. their contents are unsorted, but position-dependent
		// 3. their contents are unsorted, but position-independent.
		// If the contents are unsorted, it is harmless to retiain the original order (by saving each item's index)."
	
		return ! directCollectionContentsAreSorted();
	}
	protected static boolean directCollectionsHaveUniqueEntries() {
		// Tables defined to store direct collections of TOPLink-ignorant classes will require more primary keys.
		// If the collection has unique entries, then the entry field can be the other primary key.
		// If not, the easiest hack is to create a column for each entry's index."
	
		return true;
	}
	protected Object[] fabricateKeyFor( MWClass bldrClass, MWTable table ) {
		// returnsWith: <Association key: <BLDRVariableDefinition> value: <BLDRDatabaseField>>
		// class - <BLDRClassDefinition> does not have an obvious key variable, so create one
	
		StringBuffer buffer = new StringBuffer();
		buffer.append(resourceRepository().getString("noKeyFieldFound", new Object[] { bldrClass.shortName() }));
		buffer.append(System.getProperty("line.separator"));
		buffer.append(resourceRepository().getString("pleaseEditJavaSourceFileAppropriately"));
		logUrgent(buffer.toString());
	
		String defaultPKName = getProject().getTableGenerationPolicy().getDefaultPrimaryKeyName().toLowerCase();
		MWClass typeInt = getProject().typeFor(int.class);
		MWClassAttribute attribute = bldrClass.addAttribute(defaultPKName, typeInt);
	
		MWColumn primaryKey = createPrimaryKeyColumn( attribute.getName(), attribute.typeName(), attribute.getDimensionality(), table );
		return new Object[] {attribute, primaryKey };
	}
	protected MWMapping generateForMWAggregateMapping( MWMapping mapping, MWClass[] attrAndItemType, MWClass sourceClass ) {
	
		MWAggregateMapping aggregateMapping;
		
		if (mapping instanceof MWAggregateMapping) {
			aggregateMapping = (MWAggregateMapping) mapping;
		} else {
			aggregateMapping = mapping.asMWAggregateMapping();
		}
	
		boolean mapCollectionMappings = false; 
		mapAggregateMapping( aggregateMapping, attrAndItemType, sourceClass, mapCollectionMappings );
	
		return mapping;
	}
	protected MWCollectionMapping generateForMWCollectionMapping( MWMapping mapping, MWClass[] attrAndItemType, MWClass sourceClass ) {
	
		MWClass itemType = attrAndItemType[this.ITEM_TYPE];
		
		if (itemType.isInterface() || itemType.getName().equals(java.lang.Object.class.getName())) {
			return null;
		}
	
		MWCollectionMapping collectionMapping;	
		// if the mapping is already a collection mapping, then respect its decision.
		if (mapping != null && mapping.isCollectionMapping()) {
			collectionMapping = (MWCollectionMapping) mapping;
		} else {
			// check for singular backpointer
			MWOneToOneMapping backpointer = backpointerFor( mapping, attrAndItemType, sourceClass );
	
			if (backpointer == null) { 
				// either there is no back pointer, a collection back pointer or we can't add a back pointer"
				logNotify(resourceRepository().getString("refersToWhichHasNoBackpointer", new Object[] { sourceClass.shortName(), mapping.getName(), itemType.shortName() }));
				collectionMapping = mapping.asMWManyToManyMapping();
			} else {
				logAssumption(resourceRepository().getString("isBackpointerFor", new Object[] { itemType.shortName(), backpointer.getName(), sourceClass.shortName(), mapping.getName() }));
				collectionMapping = mapping.asMWOneToManyMapping();
			}
		}
	
		if (collectionMapping instanceof MWManyToManyMapping) {
			mapManyToManyMapping((MWManyToManyMapping)collectionMapping, attrAndItemType, sourceClass);
		} else {
			mapOneToManyMapping((MWOneToManyMapping)collectionMapping, attrAndItemType, sourceClass);
		}
		
		collectionMapping.getContainerPolicy().getDefaultingContainerClass().setContainerClass(attrAndItemType[this.ATTR_TYPE]);
		collectionMapping.setReferenceDescriptor(locateDescriptorFor(itemType));
	
		return collectionMapping;
	}
	protected MWMapping generateForMWDirectCollectionMapping( MWMapping mapping, MWClass[] attrAndItemType, MWClass sourceClass ) {
	
		MWRelationalDirectCollectionMapping collectionMapping;
		
		if (mapping instanceof MWRelationalDirectCollectionMapping) {
			collectionMapping = (MWRelationalDirectCollectionMapping) mapping;
		} else {
			collectionMapping = (MWRelationalDirectCollectionMapping) mapping.asMWDirectCollectionMapping();
		}
	
		mapDirectCollectionMapping( collectionMapping, attrAndItemType, sourceClass );
	
		return collectionMapping;
	}
	protected MWMapping generateForMWDirectToFieldMapping( MWMapping mapping, MWClass sourceClass ) {
	
		MWDirectToFieldMapping dtfMapping;
		
		if (mapping instanceof MWDirectToFieldMapping) {
			dtfMapping = (MWDirectToFieldMapping) mapping;
		} else {
			dtfMapping = (MWDirectToFieldMapping) mapping.asMWDirectMapping();
		}
	
		mapDirectToFieldMapping(dtfMapping, sourceClass);
	
		return dtfMapping;
	}
	protected MWMapping generateForMWOneToOneMapping( MWMapping mapping, MWClass[] attrAndItemType, MWClass sourceClass ) {
	
		MWOneToOneMapping oneToOneMapping;
		
		if (mapping instanceof MWOneToOneMapping) {
			oneToOneMapping = (MWOneToOneMapping) mapping;
		} else {
			oneToOneMapping = mapping.asMWOneToOneMapping();
		}
	
		mapOneToOneMapping( oneToOneMapping, attrAndItemType, sourceClass );
	
		return oneToOneMapping;
	}
	protected MWMapping generateForCollectionMapping(MWMapping mapping, MWClass sourceClass) {
	
		// already associated with a table
		Collection writtenFields = new ArrayList();
		mapping.addWrittenFieldsTo(writtenFields);
		if (! ((MWRelationalDescriptor) mapping.getParentDescriptor()).isAggregateDescriptor() && writtenFields.size() > 0) {
			return mapping;
		}
	
		MWClass[] attrAndItemType = determineAttrAndItemType( mapping, false );
		MWClass attrType = attrAndItemType[this.ATTR_TYPE];
		MWClass itemType = attrAndItemType[this.ITEM_TYPE];
	
		// if the mapping is a DirectCollection, we can make some guesses about missing information.
		if (mapping instanceof MWDirectCollectionMapping) {
			if (attrType == null) {
				attrType = attrAndItemType[this.ATTR_TYPE] = getProject().typeFor(java.util.Vector.class);
			}
			if (itemType == null) {
				itemType = attrAndItemType[this.ITEM_TYPE] = getProject().typeFor(java.lang.String.class);
			}
		}
	
		// return null if we have incomplete information
		if (attrType == null) {
			return null;
		}
	
		// Aggregate
		if (mapping instanceof MWAggregateMapping) {
			boolean mapCollectionMappings = true;
			mapAggregateMapping( (MWAggregateMapping) mapping, attrAndItemType, sourceClass, mapCollectionMappings );
			return mapping;
		}
		
		// return null, if we have incomplete information
		// not sure what to do with interfaces.
		if (itemType == null || itemType.isInterface()) {
			return null;
		}
		
		// Collection
		if (attrType.isAssignableToCollection()) {
	
			// DirectCollection 
			if (isDatabaseType(itemType)) {
				return generateForMWDirectCollectionMapping( mapping, attrAndItemType, sourceClass );
			}
			// BldrCollection
			else {
				return generateForMWCollectionMapping( mapping, attrAndItemType, sourceClass );
			}
		}
	
		return null;
	}
	protected MWMapping generateForNonCollectionMapping(MWMapping mapping, MWClass sourceClass) {
		// already associated with a table
		Collection writtenFields = new ArrayList();
		mapping.addWrittenFieldsTo(writtenFields);
		if (! ((MWRelationalDescriptor) mapping.getParentDescriptor()).isAggregateDescriptor() && writtenFields.size() > 0) {
			return mapping;
		}
	
		MWClass[] attrAndItemType = determineAttrAndItemType( mapping, true );
		MWClass attrType = attrAndItemType[this.ATTR_TYPE];
	
		// return null, if we have incomplete information
		if (attrType == null) {
			return null;
		}
		
		// Collections are handled in the next pass.
		// not sure what to do with interfaces.
		if (attrType.isAssignableToCollection() || attrType.isInterface()) {
			return null;
		}
		
		// DirectMapping
		if (isDatabaseType(attrType)) {
			return generateForMWDirectToFieldMapping( mapping, sourceClass );
		}
	
		MWMappingDescriptor targetDescriptor = (MWMappingDescriptor) getProject().descriptorForType(attrType);
		
		//incomplete mapping
		if (targetDescriptor== null) {
			return null;
		}
		
		boolean targetDescriptorIsAggregate = ((MWRelationalDescriptor) targetDescriptor).isAggregateDescriptor();
			
		if (mapping instanceof MWAggregateMapping|| targetDescriptorIsAggregate ) {
		// Aggregate
			return generateForMWAggregateMapping( mapping, attrAndItemType, sourceClass );
		}
		// OneToOne
		else {
			return generateForMWOneToOneMapping( mapping, attrAndItemType, sourceClass );
		}
	}
	protected static boolean generateIndexColumnsForDirectCollections() {
		// There are really three options for direct collections: either
		// 1. their contents are sorted implying that their position in the array can be recalculated, 
		// 2. their contents are unsorted, but position-dependent
		// 3. their contents are unsorted, but position-independent.
		// If the contents are unsorted, it is harmless to retiain the original order (by saving each item's index)."
	
		return directCollectionContentsAreUnsorted();
	}
	
	protected Collection getClassDescriptors() {
		return this.classDescriptors;
	}
	protected int getColumnNameLength() {
		//return getProject().getDatabase().getPlatform().getMaxColumnNameLength();
		return 30;
	}
	protected MWDatabase getDatabase() {
		return getProject().getDatabase();
	}
	protected MWTableDescriptor getDescriptorFor(MWClass bldrClass) {
		return (MWTableDescriptor) this.descriptorLookup.get(bldrClass);
	}
	protected static HashMap getPrimitiveToJavaTypeMap() {
		if (primitiveToJavaTypeMap == null) {
			initializePrimitiveToJavaTypeMap();
		}
		return primitiveToJavaTypeMap;
	}
	protected MWRelationalProject getProject() {
		return this.project;
	}
	protected MWTable getRelationshipTableFor(MWClass aClass, MWClass anotherClass) {
		MWTable table = null;
	
		// classCollection is a collection of collections that contain bldrClasses
		for (Iterator stream = this.relationshipTableLookup.keySet().iterator(); stream.hasNext(); ) {
			Collection classes = (Collection) stream.next();
			if (classes.contains(aClass) && classes.contains(anotherClass)) {
				table = (MWTable) this.relationshipTableLookup.get(classes);
				return table;
			}
		}
	
		return null;
	}

	protected MWTable getTableFor(MWClass bldrClass) {
		return (MWTable) this.tableLookup.get(bldrClass);
	}
	protected int getTableNameLength() {
	//	^self project databaseInterface maxTableNameLength
		return 30;
	}
	
	protected static void initializePrimitiveToJavaTypeMap() {
		primitiveToJavaTypeMap = new HashMap();
		primitiveToJavaTypeMap.put("byte", 		"java.lang.Byte");
		primitiveToJavaTypeMap.put("short", 	"java.lang.Short");
		primitiveToJavaTypeMap.put("int",		"java.lang.Integer");
		primitiveToJavaTypeMap.put("char", 		"java.lang.Character");
		primitiveToJavaTypeMap.put("long", 		"java.lang.Long");
		primitiveToJavaTypeMap.put("double", 	"java.lang.Double");
		primitiveToJavaTypeMap.put("float", 	"java.lang.Float");
		primitiveToJavaTypeMap.put("boolean", 	"java.lang.Boolean");
	}
	protected boolean isByteArray(MWClassAttribute attribute) {
		return attribute.getType().isBytePrimitive() && attribute.getDimensionality() == 1;
	}
	protected boolean isDatabaseType(MWClass type) {
		if (type == null) {
			return false;
		}
		return getDatabase().getDatabasePlatform().javaTypeDeclarationCanBeMappedToDatabaseType(type.getName(), 0);
	}
	protected MWTableDescriptor locateDescriptorFor(MWClass bldrClass) {
		// returnsWith: <BLDRDescriptorDefinition>
		// this returns the descriptor required, even if it is not associated with a selected class"
	
		if (bldrClass == null) {
			return null;
		}
		MWTableDescriptor descriptor = getDescriptorFor(bldrClass);
		if (descriptor != null) {
			return descriptor;
		} else {
			return (MWTableDescriptor) getProject().descriptorForType(bldrClass);
		}
	}
	protected void logAssumption(String errorMessage) {
		this.log.add(new MWError("TABLE_GENERATOR_ASSUMPTION", errorMessage));
	}
	protected void logError(String errorMessage) {
		this.log.add(new MWError("TABLE_GENERATOR_ERROR", errorMessage));
	}
	protected void logNotify(String errorMessage) {
		this.log.add(new MWError("TABLE_GENERATOR_WARNING", errorMessage));
	}
	protected void logUrgent(String errorMessage) {
		this.log.add(new MWError("TABLE_GENERATOR_URGENT", errorMessage));
	}
	protected void mapAggregateMapping( MWAggregateMapping aggMapping, MWClass[] attrAndItemType, MWClass sourceClass, boolean mapCollectionMappings ) {
	
		MWMappingDescriptor descriptor = (MWMappingDescriptor) aggMapping.getReferenceDescriptor();
		if (descriptor == null) {
			descriptor = (MWMappingDescriptor) getProject().descriptorForType(attrAndItemType[this.ATTR_TYPE]);
		}
	
		// we generate the collection mappings in Pass 3 and the rest in Pass 2.
		for (Iterator stream = descriptor.mappings(); stream.hasNext(); ) {
			MWMapping mapping = (MWMapping) stream.next();
			if (mapCollectionMappings) {
				generateForCollectionMapping( mapping, sourceClass );
			} else {
				generateForNonCollectionMapping( mapping, sourceClass );
			}
		}		
	}
	protected void mapDirectCollectionMapping(MWRelationalDirectCollectionMapping mapping, MWClass[] attrAndItemType, MWClass sourceClass) {
		MWClassAttribute attribute = mapping.getInstanceVariable();
			 
		MWClass itemClass = attrAndItemType[this.ITEM_TYPE];
		MWClass attrClass = attrAndItemType[this.ATTR_TYPE];
			
		String assumptions = new String();
	
		int length = (getTableNameLength() / 2) - 1;
		String proposedName = sourceClass.defaultTableNameWithLength(length) + "_TO_" + itemClass.defaultTableNameWithLength(length);
		MWTable table = getDatabase().addTable(newTableName(proposedName));
	
		HashMap columnNames = copyPrimaryKeyColumns(getTableFor(sourceClass), table);
	
		MWColumn column = createColumn(attribute.getName(), itemClass.getName(), 0, table);
		if (directCollectionsHaveUniqueEntries()) {
				assumptions = resourceRepository().getString("isUnique", new Object[] { assumptions });
		} else {
			// add index column and make it a key field, or use a sequence"
		}
			
		
	//	if (generateIndexColumnsForDirectCollections()) {
	//			assumptions = assumptions + "@@junsorted ";
	//			table.addField( createColumnNamedForJavaClassNamed( "INDEX", "int") );
	//	} else {
	//			assumptions = assumptions + "@@jsorted ";
	//	}
	
		logAssumption(resourceRepository().getString("directCollectionHasEntries", new Object[]{ sourceClass.shortName(), attribute.getName(), assumptions }));
	
	
		mapping.setDirectValueColumn(column);
		mapping.getContainerPolicy().getDefaultingContainerClass().setContainerClass(attrClass);
		mapping.setUsesMethodAccessing(getProject().getDefaultsPolicy().isMethodAccessing());
	
		MWReference reference = createReferenceIfAbsent(table, getTableFor(sourceClass), columnNames);
		mapping.setReference(reference);
	}
	protected void mapDirectToFieldMapping(MWDirectToFieldMapping mapping, MWClass sourceClass) {
		MWClassAttribute attribute = mapping.getInstanceVariable();
		MWTable table = getTableFor(sourceClass);
		
		MWColumn column = createColumn(attribute.getName(), attribute.typeName(), 0, table);
	
		if (! ((MWRelationalDescriptor) mapping.getParentDescriptor()).isAggregateDescriptor()) {
			mapping.setColumn(column);
		}
	}
	protected void mapManyToManyMapping( MWManyToManyMapping mapping, MWClass[] attrAndItemType, MWClass sourceClass ) {
		// get table for the source object
		MWTable sourceTable = getTableFor(sourceClass);
		if (sourceTable == null) {
			return;
		}
	
		// get descriptor and table for the target items
		MWClass itemClass = attrAndItemType[this.ITEM_TYPE];
		MWTableDescriptor itemDescriptor = locateDescriptorFor(itemClass);
		if (itemDescriptor == null) {
			logError(resourceRepository().getString("couldNotFindMapping", new Object[]{ mapping.getName(), sourceClass.shortName(), itemClass.getName()}));
			return;
		}
			
		MWTable itemTable = itemDescriptor.getPrimaryTable();
		if (itemTable == null) {
			logError(resourceRepository().getString("descriptorDoesNotHaveAPrimaryTable", new Object[]{ itemDescriptor.getMWClass().shortName() }));
			return;
		}
	
		HashMap sourceKeyNames = null;
		HashMap itemKeyNames = null;
			
		// get the join table if it exists
		MWTable joinTable = getRelationshipTableFor( sourceClass, itemClass );
		// if null, create a new joinTable
		if (joinTable == null) {
	
				// create the joinTable
				int length = (getTableNameLength() / 2) - 1;
				String proposedName = sourceClass.defaultTableNameWithLength(length) + "_TO_" + 
											itemClass.defaultTableNameWithLength(length);
				joinTable = getDatabase().addTable(newTableName( proposedName ));
	
				// copy the keys from the source and target tables into the joinTable	
				sourceKeyNames = copyPrimaryKeyColumns( sourceTable, joinTable );
				itemKeyNames = copyPrimaryKeyColumns( itemTable, joinTable );
	
				// add the join table to the database
				registerRelationshipTable( joinTable, sourceClass, itemClass );
				
		}
	
		mapping.setRelationTable(joinTable);
	
		// create a reference from the joinTable to the sourceTable	
		String nameOfReference = joinTable.getName() + "_" + sourceTable.getName();
		MWReference sourceReference = createReferenceIfAbsent( joinTable, sourceTable, sourceKeyNames );
		mapping.setSourceReference(sourceReference);
	
		// create a reference from the joinTable to the targetTable	
		nameOfReference = joinTable.getName() + "_" + itemTable.getName();
		MWReference targetReference = createReferenceIfAbsent( joinTable, itemTable, itemKeyNames );
		mapping.setTargetReference(targetReference);
	
		mapping.setReferenceDescriptor(itemDescriptor);
	}
	protected void mapOneToManyMapping( MWOneToManyMapping mapping, MWClass[] attrAndItemType, MWClass sourceClass ) {
		MWClass itemClass = attrAndItemType[this.ITEM_TYPE];
		if (itemClass.isInterface()) {
			return;
		}
		MWTableDescriptor itemDescriptor = locateDescriptorFor(itemClass);
		if (itemDescriptor.getPrimaryTable() == null) {
			logError(resourceRepository().getString("couldNotFindMapping", new Object[]{ mapping.getName(), sourceClass.shortName(), itemClass.getName() }));
			return;
		}
	
		MWOneToOneMapping backMapping = backpointerFor( mapping, attrAndItemType, sourceClass );
		if (backMapping == null) {
			logUrgent(resourceRepository().getString("thereIsNoOneToOneBackpointer", new Object[] { mapping.getName(), sourceClass.shortName() }));
			return;
		}
			
		// if a reference doesn't exist, then create it
		MWReference reference = backMapping.getReference();
		if (reference == null || reference.columnPairsSize() == 0) {
			logUrgent(resourceRepository().getString("errorMapOneToManyMapping"));
			return;
		}
	
		mapping.setReference(backMapping.getReference());
		mapping.setReferenceDescriptor( itemDescriptor );
	}
	protected void mapOneToOneMapping( MWOneToOneMapping mapping, MWClass[] attrAndItemType, MWClass sourceClass ) {
		MWTableDescriptor sourceDescriptor = getDescriptorFor( sourceClass );
		MWTable sourceTable = getTableFor( sourceClass );
			
		// ask project because we may not be modifying this descriptor"
		MWClass foreignClass = attrAndItemType[this.ATTR_TYPE];
		MWTableDescriptor targetDescriptor = (MWTableDescriptor) getProject().descriptorForType(foreignClass);
		if (targetDescriptor == null) {
			logError(resourceRepository().getString("couldNotFindMapping", new Object[] { mapping.getName(), sourceDescriptor.getName(), foreignClass.getName() }));
			return;
		}
			
		mapping.setUsesMethodAccessing(getProject().getDefaultsPolicy().isMethodAccessing());
	
		MWTable targetTable = targetDescriptor.getPrimaryTable();
			
		// create associations for each primary key"
		if (targetTable != null
				&& targetTable.columnsSize() > 0) {
						
			String referenceName = sourceTable.getName() + "_" + targetTable.getName();
			MWReference reference = sourceTable.referenceNamed(referenceName);
			if (reference == null) {
				reference = sourceTable.addReference(referenceName, targetTable);
			}
				
			for (Iterator stream = targetTable.primaryKeyColumns(); stream.hasNext(); ) {
				MWColumn foreignColumn = (MWColumn) stream.next();
				// create column
				MWColumn column = createColumn(mapping.getName(), sourceTable, foreignColumn);
	
				// add key pair
				reference.addColumnPair(column, foreignColumn);
			}
				
			mapping.setReference(reference);
		}
	
		mapping.setReferenceDescriptor(targetDescriptor);
	}
    
	protected String mungeName(String aString) {
		// munge the string by slapping an integer on the end, do not adjust the length of the string"
		if (aString == null || aString == "") {
			return "1";
		}
	
		// if the last character is not a digit
		if (! Character.isDigit( aString.charAt(aString.length() - 1) )) {
			return aString + "1";
		}
	
		// we know that the string ends with a number
		int startOfNumber = aString.length() - 1;
		for (; startOfNumber >= 0; startOfNumber--) {
			// if it's not a digit
			if (! Character.isDigit(aString.charAt(startOfNumber))) {
				startOfNumber++;
				break;
			}
		}
		String oldNumberString = aString.substring(startOfNumber);
		int number = Integer.parseInt(oldNumberString);
		String newNumberString = String.valueOf(number + 1);
		
		//BUG# 2758136 - changed startOfNumber -1 to startOfNumber
		//With aggregates, field names were being generated like this:
		//HOURS, HOURS1, HOUR2, HOU3 
		return aString.substring(0, startOfNumber) + newNumberString;
	}
	protected String newColumnName(MWTable aTable, String proposedString) {
		// check for column name collisions"
		String newString = proposedString.replace('$', '_');
	
		String columnName = StringTools.convertCamelBackToAllCaps(newString, getColumnNameLength());
	
		while (aTable.columnNamed(columnName) != null) {
			columnName = mungeName(columnName);
		}
	
		return columnName;
	}
	protected String newTableName(String proposedString) {
		// check for table name collisions"
	
		String tableName = proposedString.replace('$', '_');
	
		while (this.getDatabase().containsTableNamed(tableName)) {
			tableName = mungeName( tableName );
		}
	
		return tableName;
	}
	protected void registerRelationshipTable(MWTable aTable, MWClass aClass, MWClass anotherClass) {
		ArrayList classes = new ArrayList();
		classes.add(aClass);
		classes.add(anotherClass);
		this.relationshipTableLookup.put( classes, aTable );
	}
	protected void setClassDescriptors(Collection classDescriptors) {
		this.classDescriptors = classDescriptors;
	}
	
	protected void setProject(MWRelationalProject project) {
		this.project = project;
	}
	
	protected Collection sortTableDescriptors(Collection aCollection) {
		// returnsWith: <OrderedCollection elements: <BLDRClassDefinition>>
		// aCollection - <Collection elements: <BLDRDescriptorDefinition>>"
	
		// The list of descriptors is sorted so that when creating a table, the table for its superclass
		// has already been created."
	
		Collection orderedList = new ArrayList();
	
		// only MWTableDescriptors
		Collection nonClassDescriptors = new ArrayList();
		for (Iterator stream = aCollection.iterator(); stream.hasNext(); ) {
			Object desc = stream.next();
			if (! (desc instanceof MWTableDescriptor))
				nonClassDescriptors.add(desc);
		}
		aCollection.removeAll(nonClassDescriptors);
		
		// this is a map from a bldrClass to an ArrayList that contains the
		// superclass of the bldrClass	
		HashMap dependencyList = new HashMap();
	
		int count = 0;
		// iterate overall the descriptors and determine the superclass
		// of the MWClass associated with each descriptor.
		for (Iterator descriptors = aCollection.iterator(); descriptors.hasNext(); ) {
	
			count++;
			MWTableDescriptor descriptor = (MWTableDescriptor) descriptors.next();
			MWClass bldrClass = descriptor.getMWClass();
			
			this.descriptorLookup.put( bldrClass, descriptor);
	
			MWTableDescriptor foundDependency = null;
			for (Iterator descriptors2 = aCollection.iterator(); descriptors2.hasNext(); ) {
				MWTableDescriptor descriptor2 = (MWTableDescriptor) descriptors2.next();
				if (descriptor2.getMWClass() == bldrClass.getSuperclass()) {
					foundDependency = descriptor2;
					break;
				}
			}
			ArrayList dependsOn = new ArrayList(1);
			if (foundDependency != null) {
				dependsOn.add(bldrClass.getSuperclass());
			}
			dependencyList.put(bldrClass, dependsOn);
		}
	
		// iterate over the dependencyList, removing bldrClasses that whose superclass
		// has already been removed and putting them in the orderedList.
		while (!dependencyList.isEmpty()) {
			MWClass current = null;
			for (Iterator stream = dependencyList.keySet().iterator(); stream.hasNext(); ) {
				MWClass bldrClass = (MWClass) stream.next();
				ArrayList dependsOn = (ArrayList) dependencyList.get(bldrClass);
				if (dependsOn.isEmpty()) {
					current = bldrClass;
				}
			}
			if (current == null) {
				throw new RuntimeException(resourceRepository().getString("errorCyclicDependency"));
			}
	
			MWTableDescriptor descriptor = getDescriptorFor(current);
			orderedList.add(descriptor);
	
			dependencyList.remove(current);
			for (Iterator stream = dependencyList.keySet().iterator(); stream.hasNext(); ) {
				MWClass bldrClass = (MWClass) stream.next();
				ArrayList dependsOn = (ArrayList) dependencyList.get(bldrClass);
				dependsOn.remove(current);
			}
		}
	
		return orderedList;
	}
	
	private ResourceRepository resourceRepository() {
		return this.workbenchContext.getApplicationContext().getResourceRepository();
	}

}
