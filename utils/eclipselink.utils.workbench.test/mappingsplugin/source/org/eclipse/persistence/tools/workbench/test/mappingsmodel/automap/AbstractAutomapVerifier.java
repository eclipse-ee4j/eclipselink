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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.automap;

// JDK
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWAbstractReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

/**
 * @version 10.1.3
 * @author Pascal Filion
 */
abstract class AbstractAutomapVerifier extends TestCase
													implements AutomapVerifier
{

	protected class AggregateDescriptorInfo extends MappingDescriptionInfo{
		public AggregateDescriptorInfo(Map mappingInfoTable) {
			super(mappingInfoTable);
		}
	}

	protected class AggregateMappingInfo implements MappingInfo {
		public void verifyMapping(MWMapping mapping) {
			// Make sure the Mapping is a AggregateMapping
			assertTrue(mapping.getParentDescriptor().getName() + "." + mapping.getName() + "is not a aggregate mapping",
						  mapping instanceof MWAggregateMapping);
		}
	}

	protected interface DescriptorInfo {
		void verifyDescriptor(MWDescriptor descriptor);
	}

	protected class DirectCollectionMappingInfo implements MappingInfo {
		public final String referenceName;
		public final String directValueColumnName;

		public DirectCollectionMappingInfo(String referenceName, String directValueColumnName) {
			super();
			this.referenceName = referenceName;
			this.directValueColumnName = directValueColumnName;
		}

		public void verifyMapping(MWMapping mapping) {
			this.verifyDirectCollectionMapping(mapping);
		}

		private void verifyDirectCollectionMapping(MWMapping mapping) {
			// Make sure the Mapping is a DirectCollectionMapping
			assertTrue(mapping.getParentDescriptor().getName() + "." + mapping.getName() + " is not a direct collection mapping",
						  mapping instanceof MWDirectCollectionMapping);

			MWRelationalDirectCollectionMapping directCollectionMapping = (MWRelationalDirectCollectionMapping) mapping;
			this.verifyReference(directCollectionMapping);
			this.verifyDirectValueColumn(directCollectionMapping);
		}

		private void verifyDirectValueColumn(MWRelationalDirectCollectionMapping mapping) {
			// No direct field could be set during the automap, can safely skip this test
			if (this.directValueColumnName == null) {
				return;
			}
			assertNotNull(mapping.getDirectValueColumn());
			assertEquals(this.directValueColumnName, mapping.getDirectValueColumn().getName());
		}

		private void verifyReference(MWRelationalDirectCollectionMapping mapping) {
			if (this.referenceName == null) {
				return;
			}
			assertNotNull("The reference was not set on " + mapping.getParentDescriptor().getName() + "." + mapping.getName() + ". Should be " + this.referenceName,
							  mapping.getReference());
			assertEquals(this.referenceName, mapping.getReference().getName());
		}

	}

	protected class DirectMappingInfo implements MappingInfo {
		public final String databaseFieldName;
		public final String tableName;

		public DirectMappingInfo(String databaseFieldName) {
			this(null, databaseFieldName);
		}

		public DirectMappingInfo(String tableName, String databaseFieldName) {
			super();
			this.tableName = tableName;
			this.databaseFieldName = databaseFieldName;
		}

		private void verifyDirectMapping(MWMapping mapping) {
			// Make sure the MappingDescription is a DirectMappingDescription
			assertTrue(mapping.getParentDescriptor().getName() + "." + mapping.getName() + "is not direct mapping",
						  mapping instanceof MWDirectMapping);

			// No database field could be set during the automap, can safely skip
			// this test
			if (this.databaseFieldName == null) {
				return;
			}

			MWRelationalDirectMapping directMapping = (MWRelationalDirectMapping) mapping;

			// Make sure the database field is not null
			assertNotNull(directMapping.getColumn());

			// Make sure the database field is the good one
			assertEquals(this.databaseFieldName, directMapping.getColumn().getName());
			if (this.tableName != null) {
				assertEquals(this.tableName, directMapping.getColumn().getTable().getShortName());
			}
		}

		public void verifyMapping(MWMapping mapping) {
			verifyDirectMapping(mapping);
		}
	}

//	protected class EJBDescriptorInfo extends TableDescriptorInfo
//	{
//		public EJBDescriptorInfo(Map mappingInfoTable,
//										 String primaryTableName)
//		{
//			super(mappingInfoTable, primaryTableName);
//		}
//
//		public void testDescriptor(MWDescriptor descriptor)
//		{
//			updateContextMappingInfo(descriptor);
//			super.testDescriptor(descriptor);
//		}
//
//		private void updateContextMappingInfo(MWDescriptor descriptor)
//		{
//			assertTrue(descriptor.getT instanceof descrip);
//			EJBDescriptorState state = (EJBDescriptorState) descriptor;
//
//			for (Iterator iter = state.mappings(); iter.hasNext(); )
//			{
//				MWMapping mapping = (MWMapping) iter.next();
//				boolean ejbContext = mapping
//											.getMapping()
//											.getInstanceVariable()
//											.getTypeDeclaration()
//											.getType()
//											.getName()
//											.equals(EntityContext.class.getName());
//
//				if (ejbContext)
//					mappingInfoTable.put(mapping.getMapping().getName(), new UnmappedMappingInfo());
//			}
//		}
//	}

	protected class ManyToManyMappingInfo extends TableReferenceMappingInfo {
		public final String relationTableName;
		public final String sourceReferenceName;
		public final String targetReferenceName;

		public ManyToManyMappingInfo(String referenceDescriptorName,
											  String relationTableName,
											  String sourceReferenceName,
											  String targetReferenceName)
		{
			super(referenceDescriptorName, null);
			this.relationTableName = relationTableName;
			this.sourceReferenceName = sourceReferenceName;
			this.targetReferenceName = targetReferenceName;
		}

		private void verifyManyToManyMapping(MWMapping mapping) {
			// Make sure the Mapping is a ReferenceMapping
			assertTrue(mapping.getParentDescriptor().getName() + "." + mapping.getName() + "is not M:M",
						  mapping instanceof MWManyToManyMapping);

			// No reference descriptor could be set during the automap, can safely
			// skip this test
			if (this.referenceDescriptorName == null) {
				return;
			}

			MWManyToManyMapping manyToManyMapping = (MWManyToManyMapping) mapping;

			// Test the relation name
			if (this.relationTableName != null) {	
				// Make sure the relation table is not null
				assertNotNull(manyToManyMapping.getParentDescriptor().getName() + "." + manyToManyMapping.getName() + " should have a relation table: " + this.relationTableName,
								  manyToManyMapping.getRelationTable());

				// Make sure the relation table is the good one
				assertEquals(this.relationTableName, manyToManyMapping.getRelationTable().getName());
			}

			// Source reference
			if (this.sourceReferenceName != null) {	
				// Make sure the source reference is not null
				assertNotNull(manyToManyMapping.getParentDescriptor().getName() + "." + manyToManyMapping.getName() + " should have a source reference: " + this.sourceReferenceName,
								  manyToManyMapping.getSourceReference());

				// Make sure the source reference is the good one
				assertEquals(this.sourceReferenceName, manyToManyMapping.getSourceReference().getName());
			}

			// Target reference
			if (this.targetReferenceName != null) {	
				// Make sure the target reference is not null
				assertNotNull(manyToManyMapping.getParentDescriptor().getName() + "." + manyToManyMapping.getName() + " should have a target reference: " + this.targetReferenceName,
								  manyToManyMapping.getTargetReference());

				// Make sure the target reference is the good one
				assertEquals(this.targetReferenceName, manyToManyMapping.getTargetReference().getName());
			}
		}

		public void verifyMapping(MWMapping mapping) {
			super.verifyMapping(mapping);
			verifyManyToManyMapping(mapping);
		}
	}

	protected abstract class MappingDescriptionInfo implements DescriptorInfo {
		public final Map mappingInfoTable;

		public MappingDescriptionInfo(Map mappingInfoTable) {
			super();
			this.mappingInfoTable = mappingInfoTable;
		}

		public void verifyDescriptor(MWDescriptor descriptor) {
			verifyMappingDescription(descriptor);
		}

		private void verifyMappingDescription(MWDescriptor descriptor) {
			assertTrue(descriptor.getName() + " is not a mapping descriptor",
						  descriptor instanceof MWMappingDescriptor);

			MWMappingDescriptor mappingDescriptor = (MWMappingDescriptor) descriptor;

			for (Iterator stream = mappingDescriptor.mappings(); stream.hasNext(); ) {
				MWMapping mapping = (MWMapping) stream.next();
				String name = mapping.getName();
				MappingInfo mappingInfo = (MappingInfo) this.mappingInfoTable.get(name);

				assertNotNull("No MappingInfo was defined for " + name + " from " + mapping.getParentDescriptor(), mappingInfo);
				mappingInfo.verifyMapping(mapping);
			}
		}
	}

	protected interface MappingInfo {
		void verifyMapping(MWMapping mapping);
	}

	protected final class ProhibitedMappingInfo implements MappingInfo {
		public void verifyMapping(MWMapping mapping) {
			fail("This mapping should not have been mapped: " + mapping.getParentDescriptor().getName() + "." + mapping.getName());
		}
	}

	protected final class NullMappingInfo implements MappingInfo {
		public void verifyMapping(MWMapping mapping) {
			// Let this test pass
		}
	}

	protected class OneToManyMappingInfo extends TableReferenceMappingInfo {
		public OneToManyMappingInfo(String referenceDescriptorName, String referenceName) {
			super(referenceDescriptorName, referenceName);
		}

		public void verifyMapping(MWMapping mapping) {
			super.verifyMapping(mapping);
			verifyOneToManyMapping(mapping);
		}

		private void verifyOneToManyMapping(MWMapping mapping) {
			// Make sure the Mapping is a MWOneToManyMapping
			assertTrue(mapping.getParentDescriptor().getName() + "." + mapping.getName() + "is not 1:M",
						  mapping instanceof MWOneToManyMapping);
		}
	}

//	protected class ObjectTypeMappingInfo extends DirectMappingInfo
//	{
//		private final String databaseTypeName;
//
//		public ObjectTypeMappingInfo(String databaseFieldName,
//											  String databaseTypeName)
//		{
//			super(databaseFieldName);
//			this.databaseTypeName = databaseTypeName;
//		}
//
//		public void testMapping(MWMapping mapping)
//		{
//			super.testMapping(mapping);
//			testDatabaseFieldType(mapping);
//		}
//
//		private void testDatabaseFieldType(MWMapping mapping)
//		{
//			// Make sure the Mapping is a VariableOneoOOneMappingDescription
//			assertTrue(mapping.getC instanceof MWTypeConversionMapping.MAPPING_ID);
//
//			// No database field name could be set during the automap, can safely
//			// skip this test
//			if (databaseTypeName == null)
//				return;
//
//			MWObjectTypeConverter converter = (MWObjectTypeConverter) mapping.getMapping();
//
//			// Make sure the database type is not null
//			assertNotNull(mapping.getDatabaseType());
//
//			// Make sure the database type is the good one
//			assertEquals(databaseTypeName,
//							 mapping.getDatabaseType().getName());
//		}
//	}

	protected class OneToOneMappingInfo extends TableReferenceMappingInfo {
		public OneToOneMappingInfo(String referenceDescriptorName, String referenceName) {
			super(referenceDescriptorName, referenceName);
		}

		public void verifyMapping(MWMapping mapping) {
			super.verifyMapping(mapping);
			verifyOneToOneMapping(mapping);
		}

		private void verifyOneToOneMapping(MWMapping mapping) {
			// Make sure the Mapping is a MWOneToOneMapping
			assertTrue(mapping.getParentDescriptor().getName() + "." + mapping.getName() + "is not 1:1",
						  mapping instanceof MWOneToOneMapping);
		}
	}

	protected abstract class ReferenceMappingInfo implements MappingInfo {
		public final String referenceDescriptorName;

		public ReferenceMappingInfo(String referenceDescriptorName) {
			super();
			this.referenceDescriptorName = referenceDescriptorName;
		}

		public void verifyMapping(MWMapping mapping) {
			verifyReferenceMapping(mapping);
		}

		private void verifyReferenceMapping(MWMapping mapping) {
			// Make sure the Mapping is a ReferenceMapping
			assertTrue(mapping.getParentDescriptor().getName() + "." + mapping.getName() + " is not a reference mapping: " + mapping,
						  mapping.isReferenceMapping());

			// No reference descriptor could be set during the automap, can safely
			// skip this test
			if (this.referenceDescriptorName == null) {
				return;
			}

			MWAbstractReferenceMapping referenceMapping = (MWAbstractReferenceMapping) mapping;

			// Make sure the reference descriptor is not null
			assertNotNull(mapping.getName() + " has not reference descriptor set, should be " + this.referenceDescriptorName,
							  referenceMapping.getReferenceDescriptor());

			// Make sure the reference descriptor is the good one
			assertEquals(this.referenceDescriptorName, referenceMapping.getReferenceDescriptor().getName());
		}
	}

	protected class TableDescriptorInfo extends MappingDescriptionInfo {
		public final String[] primaryTableNames;

		public TableDescriptorInfo(Map mappingInfoTable, String primaryTableName) {
			this(mappingInfoTable, new String[] { primaryTableName });
		}

		public TableDescriptorInfo(Map mappingInfoTable, String[] primaryTableNames) {
			super(mappingInfoTable);
			this.primaryTableNames = primaryTableNames;
		}

		public void verifyDescriptor(MWDescriptor descriptor) {
			super.verifyDescriptor(descriptor);
			verifyTableDescriptor(descriptor);
		}

		private void verifyPrimaryAndAssociatedTables(MWTableDescriptor descriptor) {
			// No primary table could be set during the automap,
			// can safely skip this test
			if (this.primaryTableNames.length == 0) {
				return;
			}

			// Make sure the primary table is not null
			assertNotNull(descriptor.getPrimaryTable());

			// Make sure the tables are the good one
			assertEquals(this.primaryTableNames.length, descriptor.associatedTablesSize());

			for (Iterator stream = descriptor.associatedTables(); stream.hasNext(); ) {
				MWTable table = (MWTable) stream.next();
				assertTrue(descriptor.getName() + ", " + table.getName() + " should not be an associated table", CollectionTools.contains(this.primaryTableNames, table.getName()));
			}

			for (int index = this.primaryTableNames.length; --index >= 0; ) {
				String tableName = this.primaryTableNames[index];
				MWTable table = descriptor.getDatabase().tableNamed(tableName);

				// Make sure the table exists on the database and is an associated table
				assertNotNull(table);
				CollectionTools.contains(descriptor.associatedTables(), table);
			}
		}

		private void verifyTableDescriptor(MWDescriptor descriptor) {
			assertTrue(descriptor.getName() + " is not a table descriptor",
						  descriptor instanceof MWTableDescriptor);

			MWTableDescriptor tableDescriptor = (MWTableDescriptor) descriptor;
			verifyPrimaryAndAssociatedTables(tableDescriptor);
		}
	}

	protected abstract class TableReferenceMappingInfo extends ReferenceMappingInfo {
		public final String referenceName;

		public TableReferenceMappingInfo(String referenceDescriptorName, String referenceName) {
			super(referenceDescriptorName);
			this.referenceName = referenceName;
		}

		public void verifyMapping(MWMapping mapping) {
			super.verifyMapping(mapping);
			verifyTableReferenceMapping(mapping);
		}

		private void verifyTableReferenceMapping(MWMapping mapping) {
			// Make sure the MappingDescription is a TableReferenceMappingDescription
			assertTrue(mapping.getParentDescriptor().getName() + "." + mapping.getName() + "is not a table reference mapping",
						  mapping.isTableReferenceMapping());

			// No table reference could be set during the automap, can safely skip
			// this test
			if (this.referenceName == null) {
				return;
			}

			MWAbstractTableReferenceMapping tableReferenceMapping = (MWAbstractTableReferenceMapping) mapping;

			// Make sure the reference is not null
			assertNotNull("The reference was not set on " + tableReferenceMapping.getParentDescriptor().getName() + "." + tableReferenceMapping.getName() + ". Should be " + this.referenceName,
							  tableReferenceMapping.getReference());

			// Make sure the reference is the good one
			assertEquals(this.referenceName, tableReferenceMapping.getReference().getName());
		}
	}

//	protected class TypeConversionMappingInfo extends DirectMappingInfo
//	{
//		private final String databaseTypeName;
//
//		public TypeConversionMappingInfo(String databaseFieldName,
//													String databaseTypeName)
//		{
//			super(databaseFieldName);
//			this.databaseTypeName = databaseTypeName;
//		}
//
//		public void testMapping(MWMapping mapping)
//		{
//			super.testMapping(mapping);
//			testDatabaseFieldType(mapping);
//		}
//
//		private void testDatabaseFieldType(MWMapping mapping)
//		{
//			// Make sure the Mapping is a VariableOneoOOneMappingDescription
//			assertTrue(mapping.getMapping().getMappingType() == MWTypeConversionMapping.MAPPING_ID);
//
//			// No database field name could be set during the automap, can safely
//			// skip this test
//			if (databaseTypeName == null)
//				return;
//
//			MWTypeConversionMapping mapping = (MWTypeConversionMapping) mapping.getMapping();
//
//			// Make sure the database type is not null
//			assertNotNull(mapping.getDatabaseType());
//
//			// Make sure the database type is the good one
//			assertEquals(databaseTypeName, mapping.getDatabaseType().getName());
//		}
//	}

	protected class VariableOneToOneMappingInfo extends ReferenceMappingInfo {
		public final String classIndicatorFieldName;

		public VariableOneToOneMappingInfo(String referenceDescriptorName, String classIndicatorFieldName) {
			super(referenceDescriptorName);
			this.classIndicatorFieldName = classIndicatorFieldName;
		}

		public void verifyMapping(MWMapping mapping) {
			super.verifyMapping(mapping);
			verifyVariableOneToOneMapping(mapping);
		}

		private void verifyVariableOneToOneMapping(MWMapping mapping) {
			// Make sure the Mapping is a VariableOneoOneMappingDescription
			assertTrue(mapping.getParentDescriptor().getName() + "." + mapping.getName() + "is not a variable 1:1 mapping",
						  mapping instanceof MWVariableOneToOneMapping);

			// No class indicator field could be set during the automap, can safely
			// skip this test
			if (this.classIndicatorFieldName == null) {
				return;
			}

			MWVariableOneToOneMapping varOneToOneMapping = (MWVariableOneToOneMapping) mapping;

			// Make sure the class indicator field is not null
			assertNotNull(varOneToOneMapping.getClassIndicatorPolicy().getField());

			// Make sure the class indicator field is the good one
			assertEquals(this.classIndicatorFieldName, varOneToOneMapping.getClassIndicatorPolicy().getColumn().getName());
		}
	}
	protected final void testDescriptors(Map descriptorTable)
	{
		for (Iterator iter = descriptorTable.keySet().iterator(); iter.hasNext();)
		{
			MWDescriptor descriptor = (MWDescriptor) iter.next();
			DescriptorInfo descriptorInfo = (DescriptorInfo) descriptorTable.get(descriptor);

			assertNotNull("No DescriptorInfo was set for: " + descriptor, descriptorInfo);
			descriptorInfo.verifyDescriptor(descriptor);
		}
	}
}
