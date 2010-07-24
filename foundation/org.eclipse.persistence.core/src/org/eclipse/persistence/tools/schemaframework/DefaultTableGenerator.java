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
package org.eclipse.persistence.tools.schemaframework;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.descriptors.FieldTransformation;
import org.eclipse.persistence.internal.descriptors.MethodBasedFieldTransformation;
import org.eclipse.persistence.internal.descriptors.TransformerBasedFieldTransformation;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.RelationTableMechanism;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;

/**
 * DefaultTableGenerator is a utility class used to generate a default table schema for a EclipseLink project object.
 *
 * The utility can be used in EclipseLink CMP for OC4J to perform the table auto creation process, which can be triggered
 * at deployment time when EclipseLink project descriptor is absent (default mapping) or present.
 *
 * The utility can also be used to any EclipseLink application to perform the table drop/creation at runtime.
 *
 * The utility handles all direct/relational mappings, inheritance, multiple tables, interface with/without tables,
 * optimistic version/timestamp lockings, nested relationships, BLOB/CLOB generation.
 *
 * The utility is platform-agnostic.
 *
 * Usage:
 * - CMP
 *  1. set "autocreate-tables=true|false, autodelete-tables=true|false" in oc4j application deployment
 *     descriptor files (config/system-application.xml, config/application.xml, or orion-application.xml in an .ear)
 *
 *  2. Default Mapping: the same as CMP, plus system properties setting -Declipselink.defaultmapping.autocreate-tables='true|false'
 *     and  -Declipselink.defaultmapping.autodelete-tables='true|false'
 *
 * - Non-CMP:
 *  1.  Configuration: through sessions.xml
 *  2.  Directly runtime call through schema framework:
 *      SchemaManager mgr = new SchemaManager(session);
 *      mgr.replaceDefaultTables(); //drop and create
 *		mgr.createDefaultTables(); //create only
 *
 * The utility currently only supports relational project.
 *
 * @author King Wang
 * @since Oracle TopLink 10.1.3
 */
public class DefaultTableGenerator {
    //the project object used to generate the default data schema.
    Project project = null;

    //the target database platform
    private DatabasePlatform databasePlatform;
    
    //used to track the table definition: keyed by the table name, and valued
    //by the table definition object
    private Map<String, TableDefinition> tableMap = null;

    //used to track the field definition: keyed by the database field object, and 
    //valued by the field definition.
    private Map<DatabaseField, FieldDefinition> fieldMap = null;

    //DatabaseField pool (synchronized with above 'fieldMap')
    private Map<DatabaseField, DatabaseField> databaseFields;

    //When this flag is 'false' EclipseLink will not attempt to create fk constraints
    protected boolean generateFKConstraints = true;

    /**
     * Default construcotr
     */
    public DefaultTableGenerator(Project project) {
        this.project = project;
        if (project.getDatasourceLogin().getDatasourcePlatform() instanceof DatabasePlatform){
        	databasePlatform = (DatabasePlatform)project.getDatasourceLogin().getDatasourcePlatform();
        }
        tableMap = new HashMap();
        fieldMap = new HashMap();
        databaseFields = new HashMap();
    }
    
    /**
     * This constructor will create a DefaultTableGenerator that can be set to create fk
     * constraints
     */
    public DefaultTableGenerator(Project project, boolean generateFKConstraints){
    	this(project);
    	this.generateFKConstraints = generateFKConstraints;
    }

    /**
     * Generate a default TableCreator object from the EclipseLink project object.
     */
    public TableCreator generateDefaultTableCreator() {
        TableCreator tblCreator = new TableCreator();

        //go through each descriptor and build the table/field definitions out of mappings
        Iterator descIter = project.getDescriptors().values().iterator();

        while (descIter.hasNext()) {
            ClassDescriptor desc = (ClassDescriptor)descIter.next();

            if ((desc instanceof XMLDescriptor) || (desc instanceof EISDescriptor) || (desc instanceof ObjectRelationalDataTypeDescriptor)) {
                //default table generator does not support ox, eis and object-relational descriptor
                AbstractSessionLog.getLog().log(SessionLog.WARNING, "relational_descriptor_support_only", (Object[])null, true);

                return tblCreator;
            }

            // Aggregate descriptors do not contain table/field data and are 
            // processed through their owning entities. Aggregate descriptors
            // can not exist on their own.
            if (!desc.isAggregateDescriptor() && !desc.isAggregateCollectionDescriptor()) {
                initTableSchema(desc);
            }
        }

        //Post init the schema for relation table and direct collection/map tables, and several special mapping handlings.
        descIter = project.getOrderedDescriptors().iterator();

        while (descIter.hasNext()) {
            ClassDescriptor desc = (ClassDescriptor) descIter.next();

            // Aggregate descriptors do not contain table/field data and are 
            // processed through their owning entities. Aggregate descriptors
            // can not exist on their own.
            if (!desc.isAggregateDescriptor() && !desc.isAggregateCollectionDescriptor()) {
                postInitTableSchema(desc);
            }
        }

        tblCreator.addTableDefinitions(tableMap.values());

        return tblCreator;
    }

    /**
     * Generate a default TableCreator object from the EclipseLink project object,
     * and perform the table existence check through jdbc table metadata, and filter out
     * tables which are already in the database.
     */
    public TableCreator generateFilteredDefaultTableCreator(AbstractSession session) throws DatabaseException {
        TableCreator tblCreator = generateDefaultTableCreator();

        try {
            //table exisitence check.
            java.sql.Connection conn = null;
            if (session.isServerSession()) {
                //acquire a connection from the pool
                conn = ((ServerSession)session).getDefaultConnectionPool().acquireConnection().getConnection();
            } else if (session.isDatabaseSession()) {
                conn = ((DatabaseSessionImpl)session).getAccessor().getConnection();
            } 
            if (conn == null) {
                //TODO: this is not pretty, connection is not obtained for some reason. 
                return tblCreator;
            }
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet resultSet = dbMetaData.getTables(null, dbMetaData.getUserName(), null, new String[] { "TABLE" });
            java.util.List tablesInDatabase = new java.util.ArrayList();

            while (resultSet.next()) {
                //save all tables from the database
                tablesInDatabase.add(resultSet.getString("TABLE_NAME"));
            }

            resultSet.close();

            java.util.List existedTables = new java.util.ArrayList();
            java.util.List existedTableNames = new java.util.ArrayList();
            Iterator tblDefIter = tblCreator.getTableDefinitions().iterator();

            while (tblDefIter.hasNext()) {
                TableDefinition tblDef = (TableDefinition) tblDefIter.next();

                //check if the to-be-created table is already in the database
                if (tablesInDatabase.contains(tblDef.getFullName())) {
                    existedTables.add(tblDef);
                    existedTableNames.add(tblDef.getFullName());
                }
            }

            if (!existedTableNames.isEmpty()) {
                session.getSessionLog().log(SessionLog.FINEST, "skip_create_existing_tables", existedTableNames);

                //remove the existed tables, won't create them.
                tblCreator.getTableDefinitions().removeAll(existedTables);
            }
        } catch (SQLException sqlEx) {
            throw DatabaseException.errorRetrieveDbMetadataThroughJDBCConnection();
        }

        return tblCreator;
    } 

    /**
     * Build tables/fields information into the table creator object from a EclipseLink descriptor.
     * This should handle most of the direct/relational mappings except many-to-many and direct
     * collection/map mappings, witch must be down in postInit method.
     */
    protected void initTableSchema(ClassDescriptor descriptor) {
        TableDefinition tableDefintion = null;
        if (descriptor.hasTablePerClassPolicy() && descriptor.isAbstract()) {
            return;
        }

        //create a table definition for each mapped database table
        for (DatabaseTable table : descriptor.getTables()) {
            tableDefintion = getTableDefFromDBTable(table);
        }

        //build each field definition and figure out which table it goes
        for (DatabaseField dbField : descriptor.getFields()) {
            boolean isPKField = false;

            //first check if the filed is a pk field in the default table.
            isPKField = descriptor.getPrimaryKeyFields().contains(dbField);

            //then check if the field is a pk field in the secondary table(s), this is only applied to the multiple tables case.
            Map secondaryKeyMap = descriptor.getAdditionalTablePrimaryKeyFields().get(dbField.getTable());

            if (secondaryKeyMap != null) {
                isPKField = isPKField || secondaryKeyMap.containsValue(dbField);
            }

            //build or retrieve the field definition.
            FieldDefinition fieldDef = getFieldDefFromDBField(dbField, isPKField);
            if (isPKField) {
                // Check if the generation strategy is IDENTITY
                String sequenceName = descriptor.getSequenceNumberName();
                DatabaseLogin login = this.project.getLogin();
                Sequence seq = login.getSequence(sequenceName);
                if(seq instanceof DefaultSequence) {
                    seq = login.getDefaultSequence();
                }
                //The native sequence whose value should be acquired after insert is identity sequence
                boolean isIdentity = seq instanceof NativeSequence && seq.shouldAcquireValueAfterInsert();
                fieldDef.setIsIdentity(isIdentity);
            }

            //find the table the field belongs to, and add it to the table, only if not already added.
            tableDefintion = this.tableMap.get(dbField.getTableName());

            if (!tableDefintion.getFields().contains(fieldDef)) {
                tableDefintion.addField(fieldDef);
            }
        }
    }
    
    /**
     * Build additional table/field definitions for the descriptor, like relation table
     * and direct-collection, direct-map table, as well as reset LOB type for serialized
     * object mapping and type conversion mapping for LOB usage
     */
    private void postInitTableSchema(ClassDescriptor desc) {
        for (DatabaseMapping mapping : desc.getMappings()) {
            if (desc.isChildDescriptor() && desc.getInheritancePolicy().getParentDescriptor().getMappingForAttributeName(mapping.getAttributeName()) != null) {
                // If we are an inheritance subclass, do nothing. That is, don't 
                // generate mappings that will be generated by our parent,
                // otherwise the fields for that mapping will be generated n 
                // times for the same table.
                continue;
            } else if (mapping.isManyToManyMapping()) {
                buildRelationTableDefinition((ManyToManyMapping)mapping, ((ManyToManyMapping)mapping).getRelationTableMechanism(), ((ManyToManyMapping)mapping).getListOrderField(), mapping.getContainerPolicy());
            } else if (mapping.isDirectCollectionMapping()) {
                buildDirectCollectionTableDefinition((DirectCollectionMapping) mapping, desc);
            } else if (mapping.isDirectToFieldMapping()) {
                Converter converter = ((DirectToFieldMapping)mapping).getConverter();
                if (converter != null) {
                    if (converter instanceof TypeConversionConverter) {
                        resetFieldTypeForLOB((DirectToFieldMapping)mapping);
                    }
                    
                    if (converter instanceof SerializedObjectConverter) {
                        //serialized object mapping field should be BLOB/IMAGE
                        getFieldDefFromDBField(mapping.getField(), false).setType(Byte[].class);
                    }
                }
            } else if (mapping.isAggregateCollectionMapping()) {
                //need to figure out the target foreign key field and add it into the aggregate target table
                createAggregateTargetTable((AggregateCollectionMapping) mapping);
            } else if (mapping.isForeignReferenceMapping()) {
                if (mapping.isOneToOneMapping()) {
                    RelationTableMechanism relationTableMechanism = ((OneToOneMapping)mapping).getRelationTableMechanism(); 
                    if(relationTableMechanism == null) {
                        addForeignKeyFieldToSourceTargetTable((OneToOneMapping) mapping);
                    } else {
                        buildRelationTableDefinition((OneToOneMapping)mapping, relationTableMechanism, null, null);
                    }
                } else if (mapping.isOneToManyMapping()) {
                    addForeignKeyFieldToSourceTargetTable((OneToManyMapping) mapping);
                    TableDefinition targTblDef = getTableDefFromDBTable(((OneToManyMapping)mapping).getReferenceDescriptor().getDefaultTable());
                    addFieldsForMappedKeyMapContainerPolicy(mapping.getContainerPolicy(), targTblDef);
                }
            } else if (mapping.isTransformationMapping()) {
                resetTransformedFieldType((TransformationMapping) mapping);
            } else if (mapping.isAggregateObjectMapping()){
                postInitTableSchema(((AggregateObjectMapping)mapping).getReferenceDescriptor());
            }
        }
        
        processAdditionalTablePkFields(desc);
    }

    
    /**
     * The ContainerPolicy may contain some additional fields that should be added to the table
     * 
     * @see MappedKeyMapContainerPolicy
     * @param cp
     * @param tblDef
     */
    private void addFieldsForMappedKeyMapContainerPolicy(ContainerPolicy cp, TableDefinition tblDef){
        if (cp.isMappedKeyMapPolicy()){
            List<DatabaseField> keyFields = cp.getIdentityFieldsForMapKey();
            Iterator<DatabaseField> i = keyFields.iterator();
            while (i.hasNext()){
                DatabaseField foreignKey = i.next();
                FieldDefinition fieldDef = getFieldDefFromDBField(foreignKey, false);
                if (!tblDef.getFields().contains(fieldDef)) {
                    tblDef.addField(getFieldDefFromDBField(foreignKey, false));
                }
            }
            Map<DatabaseField, DatabaseField> foreignKeys = ((MappedKeyMapContainerPolicy)cp).getForeignKeyFieldsForMapKey();
            if (foreignKeys != null){
                addForeignMappingFkConstraint(foreignKeys);
            }
        }
    }
    
    /**
     * Build relation table definitions for all many-to-many relationships in a EclipseLink descriptor.
     */
    private void buildRelationTableDefinition(ForeignReferenceMapping mapping, RelationTableMechanism relationTableMechanism, DatabaseField listOrderField, ContainerPolicy cp) {
        //first create relation table
        TableDefinition tblDef = getTableDefFromDBTable(relationTableMechanism.getRelationTable());

        //add source foreign key fields into the relation table
        Vector srcFkFields = relationTableMechanism.getSourceRelationKeyFields();
        Vector srcKeyFields = relationTableMechanism.getSourceKeyFields();

        buildRelationTableFields(mapping, tblDef, srcFkFields, srcKeyFields);

        //add target foreign key fields into the relation table
        Vector targFkFields = relationTableMechanism.getTargetRelationKeyFields();
        Vector targKeyFields = relationTableMechanism.getTargetKeyFields();
        
        buildRelationTableFields(mapping, tblDef, targFkFields, targKeyFields);
        
        if (cp != null){
            addFieldsForMappedKeyMapContainerPolicy(cp, tblDef);
        }
        
        if(listOrderField != null) {
            tblDef.addField(getFieldDefFromDBField(listOrderField, false));
        }
    }

    /**
     * Build field definitions and foreign key constraints for all many-to-many relation table.
     */
    private void buildRelationTableFields(ForeignReferenceMapping mapping, TableDefinition tblDef, Vector fkFields, Vector targetFields) {
        assert fkFields.size() > 0 && fkFields.size() == targetFields.size();
        
        DatabaseField fkField = null;
        DatabaseField targetField = null;
        Vector<String> fkFieldNames = new Vector();
        Vector<String> targetFieldNames = new Vector();

        for (int index = 0; index < fkFields.size(); index++) {
            fkField = (DatabaseField) fkFields.get(index);
            targetField = (DatabaseField) targetFields.get(index);
            fkFieldNames.add(fkField.getNameDelimited(databasePlatform));
            targetFieldNames.add(targetField.getNameDelimited(databasePlatform));
            
            fkField = resolveDatabaseField(fkField, targetField);
            setFieldToRelationTable(fkField, tblDef);
        }
        
        // add a foreign key constraint from fk field to target field
        DatabaseTable targetTable = targetField.getTable();
        TableDefinition targetTblDef = getTableDefFromDBTable(targetTable);

        if (mapping.getDescriptor().hasTablePerClassPolicy()
                && mapping.getDescriptor().getTablePerClassPolicy().hasChild()) {
            return;
        }
        if (mapping.getReferenceDescriptor().hasTablePerClassPolicy()
                && mapping.getReferenceDescriptor().getTablePerClassPolicy().hasChild()) {
            return;
        }
        addForeignKeyConstraint(tblDef, targetTblDef, fkFieldNames, targetFieldNames);
    }

    /**
     * Build direct collection table definitions in a EclipseLink descriptor
     */
    private void buildDirectCollectionTableDefinition(DirectCollectionMapping mapping, ClassDescriptor desc) {
        //first create direct collection table
        TableDefinition tblDef = getTableDefFromDBTable(mapping.getReferenceTable());

        DatabaseField dbField = null;

        //add the table reference key(s)
        Vector refPkFields = mapping.getReferenceKeyFields();

        for (int index = 0; index < refPkFields.size(); index++) {
            dbField = resolveDatabaseField((DatabaseField) refPkFields.get(index), mapping.getSourceKeyFields().get(index));
            tblDef.addField(getDirectCollectionReferenceKeyFieldDefFromDBField(dbField));
        }

        //add the direct collection field to the table.
        tblDef.addField(getFieldDefFromDBField(mapping.getDirectField(), false));

        //if the mapping is direct-map field, add the direct key field to the table as well.
        // TODO: avoid generating DDL for map key mappings for the time being.
        // Bug: 270814
        if (mapping.isDirectMapMapping() && ! mapping.getContainerPolicy().isMappedKeyMapPolicy() ) {
            dbField = ((DirectMapMapping) mapping).getDirectKeyField();
            tblDef.addField(getFieldDefFromDBField(dbField, false));
        } else {
            addFieldsForMappedKeyMapContainerPolicy(mapping.getContainerPolicy(), tblDef);
            
            if(mapping.getListOrderField() != null) {
                tblDef.addField(getFieldDefFromDBField(mapping.getListOrderField(), false));
            }
        }
    }

    /**
     * Reset field type to use BLOB/CLOB with type conversion mapping fix for 4k oracle thin driver bug.
     */
    private void resetFieldTypeForLOB(DirectToFieldMapping mapping) {
        if (mapping.getFieldClassification().getName().equals("java.sql.Blob")) {
            //allow the platform to figure out what database field type gonna be used. 
            //For example, Oracle9 will generate BLOB type, SQL Server generats IMAGE.
            getFieldDefFromDBField(mapping.getField(), false).setType(Byte[].class);
        } else if (mapping.getFieldClassification().getName().equals("java.sql.Clob")) {
            //allow the platform to figure out what database field type gonna be used. 
            //For example, Oracle9 will generate CLOB type. SQL Server generats TEXT.
            getFieldDefFromDBField(mapping.getField(), false).setType(Character[].class);
        }
    }

    /**
     * Reset the transformation mapping field types
     */
    private void resetTransformedFieldType(TransformationMapping mapping) {
        Iterator transIter = mapping.getFieldTransformations().iterator();
        while (transIter.hasNext()) {
            FieldTransformation transformation = (FieldTransformation) transIter.next();
            
            if (transformation instanceof MethodBasedFieldTransformation) {
                MethodBasedFieldTransformation methodTransformation = (MethodBasedFieldTransformation) transformation; 
                try {
                    Class returnType = Helper.getDeclaredMethod(mapping.getDescriptor().getJavaClass(), methodTransformation.getMethodName(), null).getReturnType();
                    getFieldDefFromDBField(methodTransformation.getField(), false).setType(returnType);
                } catch (NoSuchMethodException ex) {
                    // For some reason, the method type could not be retrieved, 
                    // use the default java.lang.String type
                }
            } else {
                // Must be a TransformerBasedFieldTransformation
                TransformerBasedFieldTransformation classTransformation = (TransformerBasedFieldTransformation) transformation;
                String methodName = "buildFieldValue";
                Class[] params = new Class[] {Object.class, String.class, Session.class};
                
                try {
                    Class returnType = Helper.getDeclaredMethod(classTransformation.getTransformerClass(), methodName, params).getReturnType();
                    
                    if (returnType.equals(Object.class)) {
                        // User needs to be more specific with their class
                        // transformer return type if they are using DDL. Throw
                        // an exception.
                        throw ValidationException.missingFieldTypeForDDLGenerationOfClassTransformation(mapping.getDescriptor(), mapping.getAttributeName(), methodName);
                    }
                    
                    getFieldDefFromDBField(classTransformation.getField(), false).setType(returnType);
                } catch (NoSuchMethodException ex) {
                    // For some reason, the method type could not be retrieved.
                    // Did the interface method change? Throw an exception. 
                    throw ValidationException.missingTransformerMethodForDDLGenerationOfClassTransformation(mapping.getDescriptor(), mapping.getAttributeName(), methodName);
                }
            }
        }
    }

    /**
     * Add the foreign key to the aggregate collection mapping target table.
     * Also add listOrderField if specified.
     */
    private void createAggregateTargetTable(AggregateCollectionMapping mapping) {
        TableDefinition targTblDef = getTableDefFromDBTable(mapping.getReferenceDescriptor().getDefaultTable());
        addFieldsForMappedKeyMapContainerPolicy(mapping.getContainerPolicy(), targTblDef);

        Iterator aggregateFieldIterator = mapping.getReferenceDescriptor().getFields().iterator();
        while (aggregateFieldIterator.hasNext()) {
            DatabaseField dbField = (DatabaseField) aggregateFieldIterator.next();
            //add the target foreign key field definition to the table definition
            targTblDef.addField(getFieldDefFromDBField(dbField, false));
        }
        
        //unlike normal one-to-many mapping, aggregate collection mapping does not have 1:1 back reference
        //mapping, so the target foreign key fields are not stored in the target descriptor.
        Iterator targFKIter = mapping.getTargetForeignKeyFields().iterator();

        while (targFKIter.hasNext()) {
            DatabaseField dbField = (DatabaseField) targFKIter.next();
            //add the target foreign key field definition to the table definition
            targTblDef.addField(getFieldDefFromDBField(dbField, false));
        }

        if(mapping.getListOrderField() != null) {
            getTableDefFromDBTable(mapping.getListOrderField().getTable()).addField(getFieldDefFromDBField(mapping.getListOrderField(), false));
        }
    }

    private void addForeignKeyFieldToSourceTargetTable(OneToOneMapping mapping) {        
        if (!mapping.isForeignKeyRelationship()
                || (mapping.getReferenceDescriptor().hasTablePerClassPolicy()
                        && mapping.getReferenceDescriptor().getTablePerClassPolicy().hasChild())) {
            return;
        }
 
        addForeignMappingFkConstraint(mapping.getSourceToTargetKeyFields());
    }
    
    private void addForeignKeyFieldToSourceTargetTable(OneToManyMapping mapping) {
        if (mapping.getDescriptor().hasTablePerClassPolicy()
                && mapping.getDescriptor().getTablePerClassPolicy().hasChild()) {
            return;
        }
        addForeignMappingFkConstraint(mapping.getTargetForeignKeysToSourceKeys());
        if(mapping.getListOrderField() != null) {
            getTableDefFromDBTable(mapping.getListOrderField().getTable()).addField(getFieldDefFromDBField(mapping.getListOrderField(), false));
        }
    }    

    private void addForeignMappingFkConstraint(final Map<DatabaseField, DatabaseField> srcFields) {
        // srcFields map from the foreign key field to the target key field

        if(srcFields.size() == 0) {
            return;
        }

        List<DatabaseField> fkFields = new Vector<DatabaseField>();
        List<DatabaseField> targetFields = new Vector<DatabaseField>();
        
        for (DatabaseField fkField : srcFields.keySet()) {
            fkFields.add(fkField);
            targetFields.add(srcFields.get(fkField));
        }
        addJoinColumnsFkConstraint(fkFields, targetFields);
    }
    
    /**
     * Build a table definition object from a database table object
     */
    private TableDefinition getTableDefFromDBTable(DatabaseTable dbTbl) {
        TableDefinition tblDef = this.tableMap.get(dbTbl.getName());

        if (tblDef == null) {
            //table not built yet, simply built it
            tblDef = new TableDefinition();
            tblDef.setName(dbTbl.getNameDelimited(databasePlatform));
            tblDef.setQualifier(dbTbl.getTableQualifier());
            addUniqueKeyConstraints(tblDef, dbTbl.getUniqueConstraints());
            tableMap.put(dbTbl.getName(), tblDef);
        }

        return tblDef;
    }

    /**
     * Resolve the foreign key database field metadata in relation table or direct collection/map table.
     * Those metadata includes type, and maybe dbtype/size/subsize if DatabaseField carries those info.
     */
    private DatabaseField resolveDatabaseField(DatabaseField childField, DatabaseField parentField) {
        //set through the type from the source table key field to the relation or direct collection table key field.        
        DatabaseField resolvedDatabaseField = new DatabaseField();
        // find original field in the parent table, which contains actual type definitions
        // if 'resolvedParentField' is null, there is no corresponding field definition (typo?)
        DatabaseField resolvedParentField = databaseFields.get(parentField);
        
        resolvedDatabaseField.setName(childField.getName());
        //Table should be set, otherwise other same name field will be used wrongly because equals() is true.
        //Fix for GF#1392 the same name column for the entity and many-to-many table cause wrong pk constraint.
        resolvedDatabaseField.setTable(childField.getTable());
        
        // type definitions from parent field definition
        if(resolvedParentField != null) {
            resolvedDatabaseField.setType(resolvedParentField.getType());
            resolvedDatabaseField.setScale(resolvedParentField.getScale());
            resolvedDatabaseField.setLength(resolvedParentField.getLength());
            resolvedDatabaseField.setPrecision(resolvedParentField.getPrecision());
        }

        // these are defined in childField definition(see @JoinColumn)
        resolvedDatabaseField.setUnique(childField.isUnique());
        resolvedDatabaseField.setNullable(childField.isNullable());
        resolvedDatabaseField.setUpdatable(childField.isUpdatable());
        resolvedDatabaseField.setInsertable(childField.isInsertable());
        resolvedDatabaseField.setUseDelimiters(childField.shouldUseDelimiters());
        resolvedDatabaseField.useUpperCaseForComparisons(childField.getUseUpperCaseForComparisons());
        resolvedDatabaseField.setNameForComparisons(childField.getNameForComparisons());
        
        String columnDef = childField.getColumnDefinition();
        if(columnDef == null || columnDef.trim().equals("")) {
            // if childField has no column definition, follow the definition of the parent field
            if(resolvedParentField != null) {
                resolvedDatabaseField.setColumnDefinition(resolvedParentField.getColumnDefinition());
            }
        } else {
            resolvedDatabaseField.setColumnDefinition(columnDef);
        }
        
        return resolvedDatabaseField;
    }

    /**
     * Build a field definition object from a database field.
     */
    private FieldDefinition getFieldDefFromDBField(DatabaseField dbField, boolean isPrimaryKey) {
        FieldDefinition fieldDef = this.fieldMap.get(dbField);

        if (fieldDef == null) {
            //not built yet, build one
            fieldDef = new FieldDefinition();
            fieldDef.setName(dbField.getNameDelimited(databasePlatform));

            if (dbField.getColumnDefinition() != null && dbField.getColumnDefinition().length() > 0) {
                // This column definition would include the complete definition of the  
                // column like type, size,  "NULL/NOT NULL" clause, unique key clause 
                fieldDef.setTypeDefinition(dbField.getColumnDefinition());
            } else {
                Class fieldType = dbField.getType();
                FieldTypeDefinition fieldTypeDef = (fieldType == null) ? null : databasePlatform.getFieldTypeDefinition(fieldType);
                
                // Check if the user field is a String and only then allow the length specified
                // in the @Column annotation to be set on the field.
                if (fieldType != null) {
                    // If a length has been specified, set it, otherwise let the
                    // field def from individual platforms handle it.
                    if (dbField.getLength() > 0) {
                        fieldDef.setSize(dbField.getLength());
                    } else if (dbField.getPrecision() > 0) {
                        fieldDef.setSize(dbField.getPrecision()); 
                        fieldDef.setSubSize(dbField.getScale());
                    }
                }

                if ((fieldType == null) || (!fieldType.isPrimitive() && (fieldTypeDef  == null))) {
                    //TODO: log a warning for inaccessible type or not convertable type.
                    AbstractSessionLog.getLog().log(SessionLog.CONFIG, "field_type_set_to_java_lang_string", dbField.getQualifiedName(), fieldType);

                    //set the default type (lang.String) to all un-resolved java type, like null, Number, util.Date, NChar/NType, Calendar
                    //sql.Blob/Clob, Object, or unknown type). Please refer to bug 4352820.
                    fieldDef.setType(ClassConstants.STRING);
                } else {
                    //need to convert the primitive type if applied.
                    fieldDef.setType(ConversionManager.getObjectClass(fieldType));
                }

                fieldDef.setShouldAllowNull(dbField.isNullable());             
                fieldDef.setUnique(dbField.isUnique());     
            }

            fieldDef.setIsPrimaryKey(isPrimaryKey);
            fieldMap.put(dbField, fieldDef);
            databaseFields.put(dbField, dbField);
        }

        return fieldDef;
    }
    
    /**
     * Build a field definition object from a database field.
     */
    private FieldDefinition getDirectCollectionReferenceKeyFieldDefFromDBField(DatabaseField dbField) {
        FieldDefinition fieldDef = (FieldDefinition)getFieldDefFromDBField(dbField, true).clone();
        //direct collection/map table reference kye filed is not unique, need to set it as non-pk.
        fieldDef.setIsPrimaryKey(false);
        return fieldDef;
    }

    /**
     * Build and add a field definition object to relation table
     */
    private void setFieldToRelationTable(DatabaseField dbField, TableDefinition tblDef) {
        FieldDefinition fieldDef = getFieldDefFromDBField(dbField, false);

        if (!tblDef.getFields().contains(fieldDef)) {
            //only add the field once, to avoid add twice if m:m is bi-directional.
            tblDef.addField(getFieldDefFromDBField(dbField, false));
            fieldDef.setIsPrimaryKey(true); // make this a PK as we will be creating constrains later
        }
    }

    private void processAdditionalTablePkFields(ClassDescriptor desc) {
        // only if there are additional tables
        if (!desc.hasMultipleTables()) {
            return;
        }
        
        DatabaseTable dbTbl = null;
        Iterator dbTblIter = desc.getTables().iterator();
        while (dbTblIter.hasNext()) {
            dbTbl = (DatabaseTable) dbTblIter.next();
            Map<DatabaseField, DatabaseField> srcFields = desc.getAdditionalTablePrimaryKeyFields().get(dbTbl);
            if ((null != srcFields) && srcFields.size() > 0) {
                // srcFields is from the secondary field to the primary key field
                // Let's make fk constraint from the secondary field to the primary key field
                List<DatabaseField> fkFields = new Vector<DatabaseField>();
                List<DatabaseField> pkFields = new Vector<DatabaseField>();
        
                for (DatabaseField pkField : srcFields.keySet()) {
                    pkFields.add(pkField);
                    fkFields.add(srcFields.get(pkField));
                }
                addJoinColumnsFkConstraint(fkFields, pkFields);
            }
        }              
    }    
   
    private void addJoinColumnsFkConstraint(List<DatabaseField> fkFields, List<DatabaseField> targetFields) {
        assert fkFields.size() == targetFields.size();
        
        if (fkFields.size() == 0) {
            return;
        }
        
        DatabaseField fkField = null;
        DatabaseField targetField = null;
        Vector<String> fkFieldNames = new Vector();
        Vector<String> targetFieldNames = new Vector();
        
        DatabaseTable sourceTable = fkFields.get(0).getTable();
        TableDefinition sourceTableDef = getTableDefFromDBTable(sourceTable);
        
        for (int i=0; i < fkFields.size(); i++) {            
            fkField = fkFields.get(i);
            targetField = targetFields.get(i);
            fkFieldNames.add(fkField.getName());
            targetFieldNames.add(targetField.getName());

            FieldDefinition fkFieldDef = fieldMap.get(fkField);
            FieldDefinition targetFieldDef = fieldMap.get(targetField);
            
            if (targetFieldDef != null) {
                // UnidirectionalOneToOneMapping case
                if (fkFieldDef == null) {
                    fkFieldDef = getFieldDefFromDBField(fkField, false);
                    if (!sourceTableDef.getFields().contains(fkFieldDef)) {
                        sourceTableDef.addField(fkFieldDef);
                    }
                }
                // Also ensure that the type, size and subsize of the foreign key field is 
                // same as that of the original field.
                fkFieldDef.setType(targetFieldDef.getType());
                fkFieldDef.setTypeDefinition(targetFieldDef.getTypeDefinition());
                fkFieldDef.setSize(targetFieldDef.getSize()); 
                fkFieldDef.setSubSize(targetFieldDef.getSubSize());
                
            }
        }

        // add a foreign key constraint
        DatabaseTable targetTable = targetField.getTable();
        TableDefinition targetTableDef = getTableDefFromDBTable(targetTable);
        
        addForeignKeyConstraint(sourceTableDef, targetTableDef, fkFieldNames, targetFieldNames);
    }

    /**
     * Add a foreign key constraint to the source table.
     */
    private void addForeignKeyConstraint(TableDefinition sourceTableDef, TableDefinition targetTableDef, 
                                         Vector<String> fkFields, Vector<String> targetFields) {

        // Only generate FK constraints if instructed to
        if (! this.generateFKConstraints){
            return;
        }
        assert fkFields.size() > 0 && fkFields.size() == targetFields.size();
        
        // target keys could be primary keys or candidate(unique) keys of the target table

        Vector<String> fkFieldNames = fkFields;
        Vector<String> targetFieldNames = targetFields;
        
        if (fkFields.size() > 1) {
            // if composite key, we should consider the order of keys.
            // Foreign Key constraint should follow the primary/unique key order of the target table.
            // e.g. if the primary key constraint of the target table is (p2, p1),
            // foreign key constraint should be "(f2, f1) REFERENCES TARGET (p2, p1)".

            // we try to reorder keys using primary keys or unique keys order of the target table,
            // but if we might not resolve it due to incorrect field name, then let it as it is.
            // This will trigger underlying database exception so users can recognize errors.
            
            boolean resolved = false;
            boolean error = false;
            
            Map<String, String> targetToFkField = new HashMap<String, String>();
            for (int index = 0; index < fkFields.size(); index++) {
                String targetField = targetFields.get(index);
                if (targetToFkField.containsKey(targetField)) {
                    //target key column appears more than once
                    error = true;
                    break;
                }
                targetToFkField.put(targetField, fkFields.get(index));
            }

            Vector<String> orderedFkFields = new Vector<String>(fkFields.size());
            Vector<String> orderedTargetFields = new Vector<String>(targetFields.size());

            if (!error) {
                // if target fields are primary keys
                resolved = true;
                for (String pkField : targetTableDef.getPrimaryKeyFieldNames()) {
                    String fkField = targetToFkField.get(pkField);
                    if (fkField == null) {
                        //primary key column not found
                        resolved = false;
                        break;
                    }
                    orderedFkFields.add(fkField);
                    orderedTargetFields.add(pkField);
                }
            }
            
            if (!error && !resolved) {
                // if target fields are unique keys
                for (UniqueKeyConstraint uniqueConstraint : targetTableDef.getUniqueKeys()) {
                    orderedFkFields.setSize(0);
                    orderedTargetFields.setSize(0);

                    resolved = true;
                    for (String ukField : uniqueConstraint.getSourceFields()) {
                        String fkField = targetToFkField.get(ukField);
                        if (fkField == null) {
                            //unique key column not found
                            resolved = false;
                            break;
                        }
                        orderedFkFields.add(fkField);
                        orderedTargetFields.add(ukField);
                    }
                    if (resolved) {
                        break;
                    }
                }
            }

            if (resolved) {
                fkFieldNames = orderedFkFields;
                targetFieldNames = orderedTargetFields;
            }
        }

        // For bidirectional relationships both side of mapping will make the same FK constraint twice.
        // TableDefinition.addForeignKeyConstraint() will ignore the same FK constraint.

        ForeignKeyConstraint fkc = sourceTableDef.buildForeignKeyConstraint(fkFieldNames, targetFieldNames,
            targetTableDef, databasePlatform);
        sourceTableDef.addForeignKeyConstraint(fkc);
    }
    
    private void addUniqueKeyConstraints(TableDefinition sourceTableDef, Map<String, Vector<List<String>>> uniqueConstraintsMap) {
        int serialNumber = -1;
        
        for (String name : uniqueConstraintsMap.keySet()) {
            Vector<List<String>> uniqueConstraints = uniqueConstraintsMap.get(name);
           
            for (List<String> uniqueConstraint : uniqueConstraints) {
                if (uniqueConstraint != null) {
                    // To keep the serialNumber consecutive, increment it only
                    // if the name is not specified.
                    if (name == null || name.equals("")) {
                        serialNumber++;
                    }
                    
                    sourceTableDef.addUniqueKeyConstraint(sourceTableDef.buildUniqueKeyConstraint(name, uniqueConstraint, serialNumber, databasePlatform));
                }
            }
        }
    }
}
