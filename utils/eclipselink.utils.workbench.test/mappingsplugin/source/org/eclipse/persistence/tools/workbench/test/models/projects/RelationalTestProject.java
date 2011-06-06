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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWSequencingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWTableGenerationPolicy;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;


public abstract class RelationalTestProject
	extends TestProject
{

	// ********** constructors **********

	protected RelationalTestProject() {
		super();
	}


	// ********** initialization **********

	@Override
	protected void initializeProject() {
		super.initializeProject();
		this.initializeDatabase();
		this.initializeSequencingPolicy();
		this.initializeTableGenerationPolicy();
		this.initializeDescriptors();
	}

	protected void initializeDatabase() {
		TestDatabases.configureMySQLDatabase(this.database());
	}

	/**
	 * if the project defined a SEQUENCE table, set up sequencing to use it
	 */
	protected void initializeSequencingPolicy() {
		MWTable seqTable = this.tableNamed("SEQUENCE");
		if (seqTable == null) {
			return;
		}
		MWSequencingPolicy policy = this.getProject().getSequencingPolicy();
		policy.setSequencingType(MWSequencingPolicy.SEQUENCE_TABLE);
		policy.setTable(seqTable);
		policy.setNameColumn(seqTable.columnNamed("SEQ_NAME"));
		policy.setCounterColumn(seqTable.columnNamed("SEQ_COUNT"));
		policy.setPreallocationSize(50);
	}

	protected void initializeTableGenerationPolicy() {
		MWTableGenerationPolicy tgPolicy = this.getProject().getTableGenerationPolicy();
		tgPolicy.setDefaultPrimaryKeyName("id");
		tgPolicy.setPrimaryKeySearchPattern("*id");
	}

	/**
	 * add a "standard" SEQUENCE table
	 */
	protected void initializeSequenceTable() {
		MWTable table = this.database().addTable("SEQUENCE");
		this.addPrimaryKeyField(table, "SEQ_NAME", "varchar", 20);
		this.addField(table, "SEQ_COUNT", "integer");
	}

	protected void initializeDescriptors() {
		// do nothing
	}


	// ********** convenience methods **********

	public MWRelationalProject getProject() {
		return (MWRelationalProject) this.getProjectInternal();
	}

	protected static DatabasePlatform oraclePlatform() {
		return DatabasePlatformRepository.getDefault().platformNamed("Oracle");
	}

    protected static DatabasePlatform db2Platform() {
        return DatabasePlatformRepository.getDefault().platformNamed("IBM DB2");
    }

    protected static DatabasePlatform mySqlPlatform() {
    	return DatabasePlatformRepository.getDefault().platformNamed("MySQL");
    }

	protected MWAggregateDescriptor aggregateDescriptorWithShortName(String name) {
		return (MWAggregateDescriptor) this.descriptorWithShortName(name);
	}

	protected MWTableDescriptor tableDescriptorWithShortName(String name) {
		return (MWTableDescriptor) this.descriptorWithShortName(name);
	}

	public MWDatabase database() {
		return this.getProject().getDatabase();
	}

	public MWTable tableNamed(String name) {
		return this.database().tableNamed(name);
	}


	// ***** direct mappings *****

	protected MWDirectToFieldMapping addDirectMapping(MWMappingDescriptor descriptor, String attributeName) {
		MWClass descriptorType = descriptor.getMWClass();
		return (MWDirectToFieldMapping) descriptor.addDirectMapping(descriptorType.attributeNamedFromAll(attributeName));
	}

	protected MWDirectToFieldMapping addDirectMapping(MWMappingDescriptor descriptor, String attributeName, MWTable table, String fieldName) {
		MWDirectToFieldMapping mapping = this.addDirectMapping(descriptor, attributeName);
		MWColumn field = table.columnNamed(fieldName);
		if (field == null) {
			throw new NullPointerException();
		}
		mapping.setColumn(field);
		return mapping;
	}

	protected MWDirectToFieldMapping addDirectMapping(MWTableDescriptor descriptor, String attributeName, MWMethod getMethod, MWMethod setMethod) {
		MWDirectToFieldMapping mapping = this.addDirectMapping(descriptor, attributeName);
		mapping.setUsesMethodAccessing(true);
		mapping.setGetMethod(getMethod);
		mapping.setSetMethod(setMethod);
		return mapping;
	}


	// ***** fields *****

	protected MWColumn addField(MWTable table, String fieldName, String typeName) {
		MWColumn field = table.addColumn(fieldName);
		field.setDatabaseType(database().getDatabasePlatform().databaseTypeNamed(typeName));
		return field;
	}

	protected MWColumn addField(MWTable table, String fieldName, String typeName, int size) {
		MWColumn field = this.addField(table, fieldName, typeName);
		field.setSize(size);
		return field;
	}

	protected MWColumn addPrimaryKeyField(MWTable table, String fieldName, String typeName) {
		MWColumn field = this.addField(table, fieldName, typeName);
		field.setPrimaryKey(true);
		return field;
	}

	protected MWColumn addPrimaryKeyField(MWTable table, String fieldName, String typeName, int size) {
		MWColumn field = this.addField(table, fieldName, typeName, size);
		field.setPrimaryKey(true);
		return field;
	}


	// ***** references *****

	protected MWReference addReference(String referenceName, MWTable sourceTable, MWTable targetTable, String sourceFieldName, String targetFieldName) {
		MWReference reference = sourceTable.addReference(referenceName, targetTable);
		reference.addColumnPair(sourceTable.columnNamed(sourceFieldName), targetTable.columnNamed(targetFieldName));
		return reference;
	}

	protected MWReference addReferenceOnDB(String referenceName, MWTable sourceTable, MWTable targetTable, String sourceFieldName, String targetFieldName) {
		MWReference reference = this.addReference(referenceName, sourceTable, targetTable, sourceFieldName, targetFieldName);
		reference.setOnDatabase(true);
		return reference;
	}

	protected MWReference addReferenceOnDB(String referenceName, MWTable sourceTable, MWTable targetTable,
				String sourceFieldName1, String targetFieldName1,
				String sourceFieldName2, String targetFieldName2) {
		MWReference reference = this.addReferenceOnDB(referenceName, sourceTable, targetTable, sourceFieldName1, targetFieldName1);
		reference.addColumnPair(sourceTable.columnNamed(sourceFieldName2), targetTable.columnNamed(targetFieldName2));
		return reference;
	}

}
