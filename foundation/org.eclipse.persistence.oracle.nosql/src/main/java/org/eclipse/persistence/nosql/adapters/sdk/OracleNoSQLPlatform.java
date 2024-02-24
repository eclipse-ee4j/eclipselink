/*
 * Copyright (c) 2022, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.nosql.adapters.sdk;

import java.io.CharArrayWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import jakarta.resource.cci.InteractionSpec;
import jakarta.resource.cci.MappedRecord;

import oracle.nosql.driver.Consistency;
import oracle.nosql.driver.Durability;
import oracle.nosql.driver.Version;
import oracle.nosql.driver.values.ArrayValue;
import oracle.nosql.driver.values.BinaryValue;
import oracle.nosql.driver.values.BooleanValue;
import oracle.nosql.driver.values.DoubleValue;
import oracle.nosql.driver.values.FieldValue;
import oracle.nosql.driver.values.IntegerValue;
import oracle.nosql.driver.values.LongValue;
import oracle.nosql.driver.values.MapValue;
import oracle.nosql.driver.values.NumberValue;
import oracle.nosql.driver.values.StringValue;
import oracle.nosql.driver.values.TimestampValue;

import org.eclipse.persistence.eis.interactions.QueryStringInteraction;
import org.eclipse.persistence.eis.mappings.EISCompositeCollectionMapping;
import org.eclipse.persistence.eis.mappings.EISDirectMapping;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.descriptors.SelectedFieldsLockingPolicy;
import org.eclipse.persistence.eis.EISAccessor;
import org.eclipse.persistence.eis.EISDOMRecord;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.eis.EISException;
import org.eclipse.persistence.eis.EISMappedRecord;
import org.eclipse.persistence.eis.EISPlatform;
import org.eclipse.persistence.eis.interactions.EISInteraction;
import org.eclipse.persistence.eis.interactions.MappedInteraction;
import org.eclipse.persistence.eis.interactions.XMLInteraction;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.internal.expressions.SQLStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLInteractionSpec;
import org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLOperation;
import org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLRecord;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.UUIDSequence;
import org.eclipse.persistence.sessions.DatabaseRecord;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Platform for Oracle NoSQL database.
 *
 * @author Radek Felcman
 * @since EclipseLink 4.0
 */
public class OracleNoSQLPlatform extends EISPlatform {

    /** OracleNoSQL interaction spec properties. */
    public static String OPERATION = "nosql.operation";
    public static String CONSISTENCY = "nosql.consistency";
    public static String DURABILITY = "nosql.durability";
    public static String TIMEOUT = "nosql.timeout";
    public static String VERSION = "nosql.version";
    public static String QUERY = "nosql_query";
    public static String QUERY_ARGUMENTS = "arguments";
    public static String QUERY_ARGUMENT_TYPE_SUFFIX = "_type";
    public static String QUERY_ARGUMENT_VALUE_SUFFIX = "_value";

    /**
     * Default constructor.
     */
    public OracleNoSQLPlatform() {
        super();
        setShouldConvertDataToStrings(true);
        setIsMappedRecordSupported(true);
        setIsIndexedRecordSupported(false);
        setIsDOMRecordSupported(true);
        setSupportsLocalTransactions(true);
    }

    /**
     * Allow the platform to build the interaction spec based on properties defined in the interaction.
     */
    @Override
    public InteractionSpec buildInteractionSpec(EISInteraction interaction) {
        InteractionSpec spec = interaction.getInteractionSpec();
        if (spec == null) {
            OracleNoSQLInteractionSpec noSqlSpec = new OracleNoSQLInteractionSpec(interaction);
            Object operation = interaction.getProperty(OPERATION);
            if (operation == null) {
                throw EISException.operationPropertyIsNotSet();
            }
            if (operation instanceof String) {
                operation = OracleNoSQLOperation.valueOf((String)operation);
            }
            noSqlSpec.setOperation((OracleNoSQLOperation)operation);

            String tableName = interaction.getQuery().getDescriptor().getTableName();
            if (tableName == null) {
                throw EISException.tableNameIsNotSet();
            }
            noSqlSpec.setTableName(tableName);
            // Allows setting of consistency as a property.
            Object consistency = interaction.getProperty(CONSISTENCY);
            if (consistency == null) {
                // Default to session property.
                consistency = interaction.getQuery().getSession().getProperty(CONSISTENCY);
            }
            if (consistency instanceof Consistency) {
                noSqlSpec.setConsistency((Consistency)consistency);
            } else if (consistency instanceof String) {
                if ("ABSOLUTE".equals(consistency)) {
                    noSqlSpec.setConsistency(Consistency.ABSOLUTE);
                } else if ("EVENTUAL".equals(consistency)) {
                    noSqlSpec.setConsistency(Consistency.EVENTUAL );
                } else {
                    throw EISException.invalidConsistencyPropertyValue((String)consistency);
                }
            }

            // Allows setting of durability as a property.
            Object durability = interaction.getProperty(DURABILITY);
            if (durability == null) {
                // Default to session property.
                durability = interaction.getQuery().getSession().getProperty(DURABILITY);
            }
            if (durability instanceof Durability) {
                noSqlSpec.setDurability((Durability)durability);
            } else if (durability instanceof String) {
                if ("COMMIT_NO_SYNC".equals(durability)) {
                    noSqlSpec.setDurability(Durability.COMMIT_NO_SYNC);
                } else if ("COMMIT_SYNC".equals(durability)) {
                    noSqlSpec.setDurability(Durability.COMMIT_SYNC );
                }  else if ("COMMIT_WRITE_NO_SYNC".equals(durability)) {
                    noSqlSpec.setDurability(Durability.COMMIT_WRITE_NO_SYNC );
                } else {
                    throw EISException.invalidDurabilityPropertyValue((String)durability);
                }
            }

            // Allows setting of timeout as a property.
            Object timeout = interaction.getProperty(TIMEOUT);
            if (timeout == null) {
                // Default to session property.
                timeout = interaction.getQuery().getSession().getProperty(TIMEOUT);
            }
            if (timeout instanceof Number) {
                noSqlSpec.setTimeout(((Number)timeout).intValue());
            } else if (timeout instanceof String) {
                noSqlSpec.setTimeout(Integer.parseInt(((String)timeout)));
            } else if (interaction.getQuery().getQueryTimeout() > 0) {
                noSqlSpec.setTimeout(interaction.getQuery().getQueryTimeout());
            }

            // Allows setting of version as a property.
            Object version = interaction.getProperty(VERSION);
            if (version == null) {
                // Default to session property.
                version = interaction.getQuery().getSession().getProperty(VERSION);
            }
            if (version == null) {
                if (interaction.getQuery().getDescriptor() != null) {
                    ClassDescriptor descriptor = interaction.getQuery().getDescriptor();
                    if (descriptor.usesOptimisticLocking() && descriptor.getOptimisticLockingPolicy() instanceof SelectedFieldsLockingPolicy) {
                        DatabaseField field = ((SelectedFieldsLockingPolicy)descriptor.getOptimisticLockingPolicy()).getLockFields().get(0);
                        if (interaction.getInputRow() != null) {
                            version = interaction.getInputRow().get(field);
                        }
                    }
                }
            }
            if (version instanceof Version) {
                noSqlSpec.setVersion((Version)version);
            } else if (version instanceof byte[]) {
                noSqlSpec.setVersion(Version.createVersion((byte[])version));
            }

            if (interaction.getQuery().getDescriptor() != null) {
                noSqlSpec.setDescriptor(interaction.getQuery().getDescriptor());
            }

            spec = noSqlSpec;
        }
        return spec;
    }

    /**
     * INTERNAL:
     * Allow the platform to handle record to row conversion.
     */
    @Override
    public AbstractRecord buildRow(jakarta.resource.cci.Record record, EISInteraction interaction, EISAccessor accessor) {
        if (record == null) {
            return null;
        }
        OracleNoSQLRecord output = (OracleNoSQLRecord)record;
        jakarta.resource.cci.Record result = output;
        if (getRecordConverter() != null) {
            result = getRecordConverter().converterFromAdapterRecord(output);
        }
        return interaction.buildRow(result, accessor);
    }

    /**
     * Allow the platform to handle record to row conversion.
     */
    @Override
    public Vector<AbstractRecord> buildRows(jakarta.resource.cci.Record record, EISInteraction interaction, EISAccessor accessor) {
        if (record == null) {
            return new Vector<>(0);
        }
        OracleNoSQLRecord output = (OracleNoSQLRecord)record;
        if (interaction.getQuery().getDescriptor() != null) {
            // Check for a map of values.
            Vector<AbstractRecord> rows = new Vector<>();
            for (Object value : output.values()) {
                rows.add(buildRow((jakarta.resource.cci.Record)value, interaction, accessor));
            }
            return rows;
        }
        return interaction.buildRows(record, accessor);
    }

    /**
     * Allow the platform to create the appropriate type of record for the interaction.
     * Convert the nested local mapped record to a flat global keyed record.
     */
    @Override
    public jakarta.resource.cci.Record createInputRecord(EISInteraction interaction, EISAccessor accessor) {
        if (interaction instanceof XMLInteraction) {
            return super.createInputRecord(interaction, accessor);
        } if (interaction instanceof QueryStringInteraction) {
            MappedRecord input = (MappedRecord)interaction.createInputRecord(accessor);

            AbstractRecord translationRow = interaction.getQuery().getTranslationRow();
            OracleNoSQLRecord arguments = new OracleNoSQLRecord();
            for (Map.Entry<DatabaseField, Object> entry : (Set<Map.Entry<DatabaseField, Object>>) translationRow.entrySet()) {
                arguments.put(entry.getKey(), entry.getValue());
            }
            if (!arguments.isEmpty()) {
                input.put(QUERY_ARGUMENTS, arguments);
            }
            return input;
        } if (interaction instanceof MappedInteraction) {
            MappedRecord input = (MappedRecord)interaction.createInputRecord(accessor);
            // Create the key from the objects id.
            ClassDescriptor descriptor = interaction.getQuery().getDescriptor();
            if (descriptor == null) {
                if (getRecordConverter() != null) {
                    return getRecordConverter().converterToAdapterRecord(input);
                }
                return input;
            }
            Object key = createMajorKey(descriptor, new EISMappedRecord(input, accessor), interaction, accessor);
            OracleNoSQLRecord record = new OracleNoSQLRecord();
            record.put(key, input);
            if (getRecordConverter() != null) {
                return getRecordConverter().converterToAdapterRecord(record);
            }
            return record;
        } else {
            return super.createInputRecord(interaction, accessor);
        }
    }

    /**
     * Stores the XML DOM value into the record.
     * XML is stored in Oracle NoSQL but storing the XML text, keyed on the object's {@literal "&lt;dataTypeName&gt;/&lt;id&gt;"}.
     */
    @Override
    public void setDOMInRecord(Element dom, jakarta.resource.cci.Record record, EISInteraction interaction, EISAccessor accessor) {
        org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLRecord noSqlRecord = (org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLRecord)record;
        org.eclipse.persistence.oxm.record.DOMRecord domRecord = new org.eclipse.persistence.oxm.record.DOMRecord(dom);
        domRecord.setSession(interaction.getQuery().getSession());
        // Create the key from the objects id.
        ClassDescriptor descriptor = interaction.getQuery().getDescriptor();
        if (descriptor == null) {
            throw EISException.xmlInteractionIsValidOnly(interaction.toString());
        }
        Object key = createMajorKey(descriptor, domRecord, interaction, accessor);
        noSqlRecord.put(key, domRecord.transformToXML().getBytes());
    }

    /**
     * NoSQL stores the data in a single big map.
     * The keys are called major keys (minor keys are used to store the attributes).
     * Major keys can have multiple values (and normally do).
     * The first value is normally the type, then the id.
     */
    protected Object createMajorKey(ClassDescriptor descriptor, AbstractRecord record, EISInteraction interaction, EISAccessor accessor) {
        Object id = descriptor.getObjectBuilder().extractPrimaryKeyFromRow(record, interaction.getQuery().getSession());
        List<String> key = new ArrayList<>(descriptor.getPrimaryKeyFields().size() + 1);
        if (!((EISDescriptor) descriptor).getDataTypeName().isEmpty()) {
            key.add(((EISDescriptor)descriptor).getDataTypeName());
        }
        if (id != null) {
            if (id instanceof CacheId) {
                Object[] idValues = ((CacheId)id).getPrimaryKey();
                for (Object idValue : idValues) {
                    String idString = accessor.getDatasourcePlatform().getConversionManager().convertObject(idValue, String.class);
                    key.add(idString);
                }
            } else {
                String idString = accessor.getDatasourcePlatform().getConversionManager().convertObject(id, String.class);
                key.add(idString);
            }
        }
        return key;
    }

    /**
     * Allow the platform to handle the creation of the Record for the DOM record.
     * Creates a DOM record from the XML data stored as a value in the OracleNoSQLRecord.
     */
    @Override
    public AbstractRecord createDatabaseRowFromDOMRecord(jakarta.resource.cci.Record record, EISInteraction call, EISAccessor accessor) {
        if (record == null) {
            return null;
        }
        EISDOMRecord domRecord = null;
        OracleNoSQLRecord noSqlRecord = (OracleNoSQLRecord)record;
        if (noSqlRecord.isEmpty()) {
            return null;
        } else {
            Map<String, DatabaseMapping> mappings = new HashMap<>();
            ClassDescriptor descriptor = call.getQuery().getDescriptor();
            String sessionName = descriptor.getSessionName();
            Session session = null;
            if (sessionName != null && !sessionName.isEmpty()) {
                session = SessionManager.getManager().getSession(sessionName);
            } else {
                session = SessionManager.getManager().getDefaultSession();
            }
            for (DatabaseMapping mapping: descriptor.getMappings()) {
                for (DatabaseField field : mapping.getFields()) {
                    mappings.put(field.getName().replaceAll("/text\\(\\)", "").toLowerCase(), mapping);
                }
            }
            domRecord = new EISDOMRecord();
            domRecord.setDOM(domRecord.createNewDocument(((XMLInteraction)call).getOutputRootElementName()));
            domRecord.setSession((AbstractSession) session);
            for (Map.Entry<String, Object> entry : (Set<Map.Entry<String, Object>>)noSqlRecord.entrySet()) {
                DatabaseMapping mapping = mappings.get(entry.getKey().toLowerCase());
                if (mapping != null) {
                    //Nested record type
                    if (entry.getValue() instanceof Map) {
                        Map<String, String> subRecord = (Map) entry.getValue();
                        domRecord.put(mapping.getField(), "");
                        EISDOMRecord domSubRecord = (EISDOMRecord) domRecord.buildNestedRow((Element) domRecord.getDOM().getLastChild());
                        domSubRecord.setSession((AbstractSession) session);
                        Map<String, DatabaseMapping> subMappings = new HashMap<>();
                        for (DatabaseMapping subMapping : mapping.getReferenceDescriptor().getMappings()) {
                            subMappings.put(subMapping.getField().getName().replaceAll("\\@", "").replaceAll("/text\\(\\)", "").toLowerCase(), subMapping);
                        }
                        for (Map.Entry<String, String> subEntry : subRecord.entrySet()) {
                            domSubRecord.put(subMappings.get(subEntry.getKey().toLowerCase()).getField(), subEntry.getValue());
                        }
                    //Lob, Blob
                    } else if (mapping instanceof EISDirectMapping && isLob(((EISDirectMapping) mapping).getFieldClassificationClassName())) {
                        domRecord.put(mapping.getField(), entry.getValue());
                    //Nested array of records
                    } else if (entry.getValue() != null && (entry.getValue().getClass().isArray() || (entry.getValue() instanceof List))) {
                        Map<String, DatabaseMapping> subMappings = new HashMap<>();
                        if (mapping.getReferenceDescriptor() == null) {
                            subMappings.put(mapping.getField().getName().replaceAll("\\@", "").replaceAll("/text\\(\\)", "").toLowerCase(), mapping);
                        } else {
                            for (DatabaseMapping subMapping : mapping.getReferenceDescriptor().getMappings()) {
                                subMappings.put(subMapping.getField().getName().replaceAll("\\@", "").replaceAll("/text\\(\\)", "").toLowerCase(), subMapping);
                            }
                        }
                        if (mapping instanceof EISCompositeCollectionMapping) {
                            domRecord.put(mapping.getField(), "");
                        }
                        Object[] entryValue;
                        if (entry.getValue() instanceof List) {
                            entryValue = ((List)entry.getValue()).toArray();
                        } else {
                            entryValue = (Object[])entry.getValue();
                        }
                        for (int i = 0; i < entryValue.length; i++) {
                            Object arrayItem = entryValue[i];
                            if (i > 0) {
                                Element lastChild = (Element)domRecord.getDOM().getLastChild();
                                Element newLastChild = (Element)lastChild.cloneNode(false);
                                domRecord.getDOM().appendChild(newLastChild);
                            }
                            if (arrayItem instanceof Map) {
                                EISDOMRecord domSubRecord = (EISDOMRecord) domRecord.buildNestedRow((Element) domRecord.getDOM().getLastChild());
                                domSubRecord.setSession((AbstractSession) session);
                                for (Map.Entry<String, Object> subEntry : (Set<Map.Entry<String, Object>>) ((Map) arrayItem).entrySet()) {
                                    if (subEntry.getValue() != null) {
                                        domSubRecord.put(subMappings.get(subEntry.getKey().toLowerCase()).getField(), subEntry.getValue());
                                    }
                                }
                            } else {
                                domRecord.add(subMappings.get(entry.getKey()).getField(), arrayItem);
                            }
                        }
                    } else if (entry.getValue() != null) {
                        domRecord.put(mapping.getField(), entry.getValue());
                    }
                }
            }
        }
        return domRecord;
    }

    /**
     * INTERNAL:
     * Allow the platform to initialize the CRUD queries to defaults.
     * Configure the CRUD operations using GET/PUT and DELETE.
     */
    @Override
    public void initializeDefaultQueries(DescriptorQueryManager queryManager, AbstractSession session) {
        boolean isXML = ((EISDescriptor)queryManager.getDescriptor()).isXMLFormat();
        // Insert
        if (!queryManager.hasInsertQuery()) {
            EISInteraction call = isXML ? new XMLInteraction() : new MappedInteraction();
            call.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.PUT_IF_ABSENT);
            queryManager.setInsertCall(call);
        }

        // Update
        if (!queryManager.hasUpdateQuery()) {
            EISInteraction call = isXML ? new XMLInteraction() : new MappedInteraction();
            call.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.PUT_IF_PRESENT);
            queryManager.setUpdateCall(call);
        }

        // Read
        if (!queryManager.hasReadObjectQuery()) {
            MappedInteraction call = isXML ? new XMLInteraction() : new MappedInteraction();
            call.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.GET);
            for (DatabaseField field : queryManager.getDescriptor().getPrimaryKeyFields()) {
                call.addArgument(field.getName());
            }
            queryManager.setReadObjectCall(call);
        }

        // Read-all
        if (!queryManager.hasReadAllQuery()) {
            MappedInteraction call = isXML ? new XMLInteraction() : new MappedInteraction();
            call.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.ITERATOR);
            queryManager.setReadAllCall(call);
        }

        // Delete
        if (!queryManager.hasDeleteQuery()) {
            MappedInteraction call = isXML ? new XMLInteraction() : new MappedInteraction();
            call.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.DELETE);
            for (DatabaseField field : queryManager.getDescriptor().getPrimaryKeyFields()) {
                call.addArgument(field.getName());
            }
            queryManager.setDeleteCall(call);
        }
    }

    @Override
    public DatasourceCall buildCallFromStatement(SQLStatement statement, DatabaseQuery query, AbstractSession session) {
        boolean isXML = ((EISDescriptor)query.getDescriptor()).isXMLFormat();
        if (query.isObjectLevelReadQuery()) {
            MappedInteraction call = isXML ? new XMLInteraction() : new MappedInteraction();
            call.setProperty(OPERATION, OracleNoSQLOperation.ITERATOR_QUERY);
            DatabaseRecord row = new DatabaseRecord();

            //Prepare SQL query for NoSQL
            DatabaseCall sqlCall = buildCall((SQLSelectStatement)statement, query);
            String sqlString = sqlCall.getSQLString();
            StringBuilder sqlVariables = null;
            StringBuilder parameterNames = null;
            sqlString = sqlString.replaceAll("/text\\(\\)", "");
            List<Object> parameters = sqlCall.getParameters();
            for (Object p: parameters) {
                ParameterExpression parameter = (ParameterExpression) p;
                if (sqlVariables == null && parameterNames == null) {
                    sqlVariables = new StringBuilder("DECLARE");
                    parameterNames = new StringBuilder();
                }
                String parameterName = parameter.getField().getName();
                Object parameterValue = parameter.getValue(statement.getTranslationRow(), session);
                String parameterTypeName = ((Class)parameter.getType()).getName();
                FieldValue fieldValue = getFieldValue(parameterTypeName, parameterValue.toString(), false);
                parameterNames.append(parameterName + ";");
                sqlVariables.append(" $" + parameterName + " " + fieldValue.getType().name() + ";");
                sqlString = sqlString.replaceFirst("\\?", "\\$" + parameterName);
                row.put(parameterName + QUERY_ARGUMENT_VALUE_SUFFIX, parameterValue);
                row.put(parameterName + QUERY_ARGUMENT_TYPE_SUFFIX, parameterTypeName);
            }
            if (!parameters.isEmpty()) {
                sqlString = sqlVariables + " " + sqlString;
                row.put(QUERY_ARGUMENTS, parameterNames);
            }
            row.put(QUERY, sqlString);
            call.setInputRow(row);
            return call;
        }
        throw EISException.queryIsTooComplexForOracleNoSQLDB(query.toString());
    }

    /**
     * Do not prepare to avoid errors being triggered for id and all queries.
     */
    @Override
    public boolean shouldPrepare(DatabaseQuery query) {
        return (query.getDatasourceCall() instanceof EISInteraction);
    }

    /**
     * INTERNAL:
     * NoSQL does not support id generation, so use UUID as the default.
     */
    @Override
    protected Sequence createPlatformDefaultSequence() {
        return new UUIDSequence();
    }

    public static FieldValue getFieldValue(String typeName, Object value, boolean isLob) {
        FieldValue fieldValue = null;
        if ("boolean".equals(typeName) || "java.lang.Boolean".equals(typeName)) {
            fieldValue = (Boolean.valueOf(value.toString())) ? BooleanValue.trueInstance() : BooleanValue.falseInstance();
        } else if ("double".equals(typeName) || "java.lang.Double".equals(typeName)) {
            fieldValue = new DoubleValue(value.toString());
        } else if ("int".equals(typeName) || "java.lang.Integer".equals(typeName)) {
            fieldValue = new IntegerValue(value.toString());
        } else if ("long".equals(typeName) || "java.lang.Long".equals(typeName)) {
            fieldValue = new LongValue(value.toString());
        } else if ("java.lang.String".equals(typeName)) {
            fieldValue = new StringValue(value.toString());
        } else if ("java.sql.Timestamp".equals(typeName)) {
            fieldValue = new TimestampValue(value.toString());
        } else if ("java.math.BigDecimal".equals(typeName)) {
            fieldValue = new NumberValue(value.toString());
        } else if (typeName.contains("byte[]") && isLob) {
            fieldValue = new BinaryValue((byte[]) value);
        } else if (typeName.contains("[]") && !isLob) {
            fieldValue = new ArrayValue();
            for (Object item: (Object[])value) {
                ((ArrayValue)fieldValue).add(getFieldValue(typeName.replace("[]", ""), item, false));
            }
        } else if ("java.util.Map".equals(typeName)) {
            fieldValue = new MapValue();
            for (Map.Entry<Object, Object> entry : (Set<Map.Entry<Object, Object>>)((Map)value).entrySet()) {
                String valueTypeName = entry.getValue().getClass().getName();
                ((MapValue)fieldValue).put(entry.getKey().toString(), getFieldValue(valueTypeName, entry.getValue(), false));
            }
        } else {
            fieldValue = new StringValue(value.toString());
        }
        return fieldValue;
    }

    public static boolean isLob(String typeName) {
        return ClassConstants.BLOB.getName().equals(typeName) || ClassConstants.CLOB.getName().equals(typeName);
    }

    /**
     * INTERNAL:
     * Allow for conversion from the Oracle type to the Java type.
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T convertObject(Object sourceObject, Class<T> javaClass) throws ConversionException, DatabaseException {
        if (sourceObject instanceof DOMRecord) {
            sourceObject = ((Node)((DOMRecord)sourceObject).getValues().get(0)).getNodeValue();
        }
        return super.convertObject(sourceObject, javaClass);
    }

    /**
     * INTERNAL:
     * Return the correct call type for the native query string.
     * This allows EIS platforms to use different types of native calls.
     */
    @Override
    public DatasourceCall buildNativeCall(String queryString) {
        QueryStringInteraction call = new QueryStringInteraction(queryString);
        call.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.NATIVE_QUERY);
        DatabaseRecord row = new DatabaseRecord();
        row.put(QUERY, queryString);
        call.setInputRow(row);
        return call;
    }

    private DatabaseCall buildCall(SQLSelectStatement statement, DatabaseQuery query) {
        SQLCall call = new SQLCall();
        call.setQuery(query);
        call.returnManyRows();
        Writer writer = new CharArrayWriter(200);
        //Create temporary Database session to print query
        //OracleNoSQLPlatform extends EISPlatform which is not compatible with DatabasePlatform needed by ExpressionSQLPrinter
        AbstractSession session = new DatabaseSessionImpl(new org.eclipse.persistence.sessions.DatabaseLogin());
        ExpressionSQLPrinter printer = new ExpressionSQLPrinter(session, statement.getTranslationRow(), call, statement.requiresAliases(), statement.getBuilder());
        printer.setWriter(writer);
        session.getPlatform().printSQLSelectStatement(call, printer, statement);
        call.setSQLString(writer.toString());
        return call;
    }
}
