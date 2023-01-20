/*
 * Copyright (c) 2022, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.nosql.adapters.sdk;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.Interaction;
import jakarta.resource.cci.InteractionSpec;
import jakarta.resource.cci.ResourceWarning;

import oracle.nosql.driver.ops.DeleteRequest;
import oracle.nosql.driver.ops.DeleteResult;
import oracle.nosql.driver.ops.GetRequest;
import oracle.nosql.driver.ops.GetResult;
import oracle.nosql.driver.ops.PrepareRequest;
import oracle.nosql.driver.ops.PrepareResult;
import oracle.nosql.driver.ops.PreparedStatement;
import oracle.nosql.driver.ops.PutRequest;
import oracle.nosql.driver.ops.PutResult;
import oracle.nosql.driver.ops.QueryRequest;
import oracle.nosql.driver.ops.QueryResult;
import oracle.nosql.driver.values.ArrayValue;
import oracle.nosql.driver.values.BinaryValue;
import oracle.nosql.driver.values.BooleanValue;
import oracle.nosql.driver.values.DoubleValue;
import oracle.nosql.driver.values.FieldValue;
import oracle.nosql.driver.values.IntegerValue;
import oracle.nosql.driver.values.LongValue;
import oracle.nosql.driver.values.MapValue;
import oracle.nosql.driver.values.NullValue;
import oracle.nosql.driver.values.NumberValue;
import oracle.nosql.driver.values.StringValue;
import oracle.nosql.driver.values.TimestampValue;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.EISException;
import org.eclipse.persistence.eis.mappings.EISCompositeCollectionMapping;
import org.eclipse.persistence.eis.mappings.EISCompositeDirectCollectionMapping;
import org.eclipse.persistence.eis.mappings.EISCompositeObjectMapping;
import org.eclipse.persistence.eis.mappings.EISDirectMapping;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.nosql.adapters.sdk.OracleNoSQLPlatform;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionManager;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Interaction to Oracle NoSQL JCA adapter.
 *
 * @author Radek Felcman
 * @since EclipseLink 4.0
 */
public class OracleNoSQLInteraction implements Interaction {

    private static final String JSON_DEFAULT_TYPE = "JSON";

    /**
     * Store the connection the interaction was created from.
     */
    protected OracleNoSQLConnection connection;

    /**
     * Default constructor.
     */
    public OracleNoSQLInteraction(OracleNoSQLConnection connection) {
        this.connection = connection;
    }

    @Override
    public void clearWarnings() {
    }

    @Override
    public void close() {
    }

    /**
     * Output records are not supported/required.
     */
    @Override
    public boolean execute(InteractionSpec spec, jakarta.resource.cci.Record input, jakarta.resource.cci.Record output) throws ResourceException {
        throw ValidationException.operationNotSupported("execute(InteractionSpec, jakarta.resource.cci.Record, jakarta.resource.cci.Record)");
    }

    /**
     * Execute the interaction and return output record.
     * The spec is either GET, PUT or DELETE interaction.
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public jakarta.resource.cci.Record execute(InteractionSpec spec, jakarta.resource.cci.Record record) throws ResourceException {
        if (!(spec instanceof OracleNoSQLInteractionSpec)) {
            throw EISException.invalidInteractionSpecType();
        }
        if (!(record instanceof OracleNoSQLRecord)) {
            throw EISException.invalidRecordType();
        }
        OracleNoSQLInteractionSpec noSqlSpec = (OracleNoSQLInteractionSpec) spec;
        OracleNoSQLRecord input = (OracleNoSQLRecord) record;
        try {
            OracleNoSQLOperation operation = noSqlSpec.getOperation();
            Map<String, DatabaseMapping> fieldNameMapping = createFieldNameMappingMap(noSqlSpec.getDescriptor(), true);
            if (operation == OracleNoSQLOperation.GET) {
                OracleNoSQLRecord output = new OracleNoSQLRecord();
                for (Map.Entry<?, ?> entry : (Set<Map.Entry<?, ?>>) input.entrySet()) {
                    MapValue key = null;
                    if (entry.getValue() instanceof byte[]) {
                        key = createMapValue(createDOMRecord((byte[]) entry.getValue()), noSqlSpec.getDescriptor());
                    } else if (entry.getValue() instanceof Map) {
                        key = createMapValue((Map) entry.getValue(), noSqlSpec.getDescriptor());
                    }
                    GetRequest getRequest = new GetRequest().setKey(key).setTableName(noSqlSpec.getTableName());
                    if (noSqlSpec.getTimeout() > 0) {
                        getRequest.setTimeout(noSqlSpec.getTimeout());
                    }
                    if (noSqlSpec.getConsistency() != null) {
                        getRequest.setConsistency(noSqlSpec.getConsistency());
                    }
                    GetResult getResult = this.connection.getNoSQLHandle().get(getRequest);
                    MapValue values = getResult.getValue();
                    if (values != null) {
                        for (Map.Entry<String, FieldValue> outputEntry : values.entrySet()) {
                            output.put(outputEntry.getKey(), unboxFieldValue(outputEntry.getValue(), spec, isDBFieldJSONType(noSqlSpec.getTableName(), outputEntry.getKey(), fieldNameMapping)));
                        }
                    }
                }
                if (output.isEmpty()) {
                    return null;
                }
                return output;
            } else if ((operation == OracleNoSQLOperation.PUT) || (operation == OracleNoSQLOperation.PUT_IF_ABSENT)
                    || (operation == OracleNoSQLOperation.PUT_IF_PRESENT) || (operation == OracleNoSQLOperation.PUT_IF_VERSION)) {
                for (Map.Entry<?, ?> entry : (Set<Map.Entry<?, ?>>) input.entrySet()) {
                    PutRequest putRequest = null;
                    if (entry.getValue() instanceof byte[]) {
                        DOMRecord domRecord = createDOMRecord((byte[]) entry.getValue());
                        putRequest = createPutRequest(domRecord, noSqlSpec.getTableName(), noSqlSpec.getDescriptor());
                    } else if (entry.getValue() instanceof Map) {
                        putRequest = createPutRequest((Map) entry.getValue(), noSqlSpec.getTableName(), noSqlSpec.getDescriptor());
                    }
                    if (noSqlSpec.getTimeout() > 0) {
                        putRequest.setTimeout(noSqlSpec.getTimeout());
                    }
                    if (noSqlSpec.getDurability() != null) {
                        putRequest.setDurability(noSqlSpec.getDurability());
                    }
                    PutResult putResult = this.connection.getNoSQLHandle().put(putRequest);
                    if (putResult.getVersion() == null) {
                        throw new ResourceException("Attempt to put failed:" + input);
                    }
                }
            } else if (operation == OracleNoSQLOperation.DELETE) {
                for (Map.Entry<?, ?> entry : (Set<Map.Entry<?, ?>>) input.entrySet()) {
                    MapValue key = null;
                    if (entry.getValue() instanceof byte[]) {
                        key = createMapValue(createDOMRecord((byte[]) entry.getValue()), noSqlSpec.getDescriptor());
                    } else if (entry.getValue() instanceof Map) {
                        key = createMapValue((Map) entry.getValue(), noSqlSpec.getDescriptor());
                    }
                    DeleteRequest deleteRequest = new DeleteRequest().setKey(key).setTableName(noSqlSpec.getTableName());
                    if (noSqlSpec.getTimeout() > 0) {
                        deleteRequest.setTimeout(noSqlSpec.getTimeout());
                    }
                    if (noSqlSpec.getDurability() != null) {
                        deleteRequest.setDurability(noSqlSpec.getDurability());
                    }
                    DeleteResult deleteResult = this.connection.getNoSQLHandle().delete(deleteRequest);
                }
            } else if (operation == OracleNoSQLOperation.ITERATOR) {
                OracleNoSQLRecord output = new OracleNoSQLRecord();
                //Handle ReadAll query like SELECT * FROM TEST_TABLE
                QueryRequest queryRequest = new QueryRequest().setStatement("SELECT * FROM " + noSqlSpec.getTableName());
                if (noSqlSpec.getTimeout() > 0) {
                    queryRequest.setTimeout(noSqlSpec.getTimeout());
                }
                if (noSqlSpec.getConsistency() != null) {
                    queryRequest.setConsistency(noSqlSpec.getConsistency());
                }
                do {
                    QueryResult queryResult = this.connection.getNoSQLHandle().query(queryRequest);
                    List<MapValue> results = queryResult.getResults();
                    int rowId = 1;
                    OracleNoSQLRecord outputRow;
                    for (MapValue values : results) {
                        outputRow = new OracleNoSQLRecord();
                        for (Map.Entry<String, FieldValue> outputEntry : values.entrySet()) {
                            outputRow.put(outputEntry.getKey(), unboxFieldValue(outputEntry.getValue(), spec, isDBFieldJSONType(noSqlSpec.getTableName(), outputEntry.getKey(), fieldNameMapping)));
                        }
                        output.put(rowId++, outputRow);
                    }
                } while (!queryRequest.isDone());
                if (output.isEmpty()) {
                    return null;
                }
                return output;
            } else if (operation == OracleNoSQLOperation.ITERATOR_QUERY) {
                OracleNoSQLRecord output = new OracleNoSQLRecord();
                OracleNoSQLRecord inputRecord = null;
                for (Map.Entry<?, ?> entry : (Set<Map.Entry<?, ?>>) input.entrySet()) {
                    if (entry.getValue() instanceof byte[]) {
                        inputRecord = createMapFromDOMRecord(createDOMRecord((byte[]) entry.getValue()));
                    } else {
                        inputRecord = (OracleNoSQLRecord)entry.getValue();
                    }
                }
                //Handle JPQL queries like SELECT * FROM TEST_TABLE WHERE NAME = :name
                String sqlString = (String)inputRecord.get(OracleNoSQLPlatform.QUERY);
                PrepareRequest prepareRequest = new PrepareRequest().setStatement(sqlString);
                if (noSqlSpec.getTimeout() > 0) {
                    prepareRequest.setTimeout(noSqlSpec.getTimeout());
                }
                PrepareResult prepareResult = this.connection.getNoSQLHandle().prepare(prepareRequest);
                PreparedStatement preparedStatement = prepareResult.getPreparedStatement();
                if (inputRecord.get(OracleNoSQLPlatform.QUERY_ARGUMENTS) != null) {
                    StringTokenizer st = new StringTokenizer((String)inputRecord.get(OracleNoSQLPlatform.QUERY_ARGUMENTS), ";");
                    while (st.hasMoreTokens()) {
                        String argumentName = st.nextToken();
                        preparedStatement.setVariable("$" + argumentName, OracleNoSQLPlatform.getFieldValue(argumentName + (String) inputRecord.get(OracleNoSQLPlatform.QUERY_ARGUMENT_TYPE_SUFFIX), (String) inputRecord.get(argumentName + OracleNoSQLPlatform.QUERY_ARGUMENT_VALUE_SUFFIX), false));
                    }
                }
                QueryRequest queryRequest = new QueryRequest().setPreparedStatement(preparedStatement);
                do {
                    QueryResult queryResult = this.connection.getNoSQLHandle().query(queryRequest);
                    List<MapValue> results = queryResult.getResults();
                    int rowId = 1;
                    OracleNoSQLRecord outputRow;
                    for (MapValue values : results) {
                        outputRow = new OracleNoSQLRecord();
                        for (Map.Entry<String, FieldValue> outputEntry : values.entrySet()) {
                            outputRow.put(outputEntry.getKey(), unboxFieldValue(outputEntry.getValue(), spec, isDBFieldJSONType(noSqlSpec.getTableName(), outputEntry.getKey(), fieldNameMapping)));
                        }
                        output.put(rowId++, outputRow);
                    }
                } while (!queryRequest.isDone());
                if (output.isEmpty()) {
                    return null;
                }
                return output;
            } else if (operation == OracleNoSQLOperation.NATIVE_QUERY) {
                OracleNoSQLRecord output = new OracleNoSQLRecord();
                //Handle Native queries like "DECLARE $id INTEGER; SELECT $tab.id, $tab.col_json_object.map[$element.m1 = 5] as component FROM $tab WHERE id = $id"
                String sqlString = (String)input.get(OracleNoSQLPlatform.QUERY);
                PrepareRequest prepareRequest = new PrepareRequest().setStatement(sqlString);
                if (noSqlSpec.getTimeout() > 0) {
                    prepareRequest.setTimeout(noSqlSpec.getTimeout());
                }
                PrepareResult prepareResult = this.connection.getNoSQLHandle().prepare(prepareRequest);
                PreparedStatement preparedStatement = prepareResult.getPreparedStatement();
                if (input.get(OracleNoSQLPlatform.QUERY_ARGUMENTS) != null) {
                    OracleNoSQLRecord arguments = (OracleNoSQLRecord)input.get(OracleNoSQLPlatform.QUERY_ARGUMENTS);
                    for (Map.Entry<DatabaseField, Object> entry : (Set<Map.Entry<DatabaseField, Object>>) arguments.entrySet()) {
                        preparedStatement.setVariable("$" + entry.getKey(), OracleNoSQLPlatform.getFieldValue(entry.getValue().getClass().getName(), entry.getValue(), false));
                    }
                }
                QueryRequest queryRequest = new QueryRequest().setPreparedStatement(preparedStatement);
                do {
                    QueryResult queryResult = this.connection.getNoSQLHandle().query(queryRequest);
                    List<MapValue> results = queryResult.getResults();
                    int rowId = 1;
                    OracleNoSQLRecord outputRow;
                    for (MapValue values : results) {
                        outputRow = new OracleNoSQLRecord();
                        for (Map.Entry<String, FieldValue> outputEntry : values.entrySet()) {
                            outputRow.put(outputEntry.getKey(), unboxFieldValue(outputEntry.getValue(), spec, isDBFieldJSONType(noSqlSpec.getTableName(), outputEntry.getKey(), fieldNameMapping)));
                        }
                        output.put(rowId++, outputRow);
                    }
                } while (!queryRequest.isDone());
                if (output.isEmpty()) {
                    return null;
                }
                return output;
            } else {
                throw new ResourceException("Invalid NoSQL operation:" + operation);
            }
        } catch (Exception exception) {
            throw new ResourceException(exception);
        }
        return null;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public ResourceWarning getWarnings() {
        return null;
    }

    private DOMRecord createDOMRecord(byte[] byteArray) {
        DOMRecord domRecord = new DOMRecord();
        domRecord.transformFromXML(new String(byteArray));
        return domRecord;
    }

    private MapValue createMapValue(DOMRecord domRecord, ClassDescriptor descriptor) {
        Map<String, DatabaseMapping> fieldNameMapping = createFieldNameMappingMap(descriptor, false);
        MapValue mapValue = new MapValue();
        for (Map.Entry<DatabaseField, Element> entry : (Set<Map.Entry<DatabaseField, Element>>) domRecord.entrySet()) {
            String key = entry.getKey().getName();
            String typesKey = descriptor.buildField(entry.getKey()).toString();
            DatabaseMapping mapping = fieldNameMapping.get(typesKey);
            if (mapping == null) {
                mapping = fieldNameMapping.get(typesKey.replace("/text()", ""));
            }
            if (mapping instanceof EISDirectMapping) {
                Converter converter = ((EISDirectMapping) mapping).getConverter();
                String fieldClassificationName = ((EISDirectMapping) mapping).getFieldClassificationClassName();
                String typeName = mapping.getAttributeClassification().getTypeName();
                String value = entry.getValue().getFirstChild().getNodeValue();
                if (converter == null) {
                    mapValue.put(key, OracleNoSQLPlatform.getFieldValue(typeName, value, OracleNoSQLPlatform.isLob(fieldClassificationName)));
                } else {
                    Session session = SessionManager.getManager().getSession(descriptor.getSessionName());
                    Object convertedValue = converter.convertDataValueToObjectValue(value, session);
                    mapValue.put(key, OracleNoSQLPlatform.getFieldValue(typeName, convertedValue, OracleNoSQLPlatform.isLob(fieldClassificationName)));
                }
            } else if (mapping instanceof EISCompositeDirectCollectionMapping) {
                ArrayValue arrayValue = new ArrayValue();
                List<DOMRecord> domRecordList = (List<DOMRecord>) domRecord.getValues(key);
                for (DOMRecord domRecordListItem : domRecordList) {
                    StringValue recordValue = null;
                    for (Node domRecordListItemValue : (List<Node>) domRecordListItem.getValues()) {
                        recordValue = new StringValue(domRecordListItemValue.getNodeValue());
                        arrayValue.add(recordValue);
                    }
                }
                mapValue.put(key, arrayValue);
            } else if (mapping instanceof EISCompositeObjectMapping) {
                MapValue recordValue = new MapValue();
                Map<String, DatabaseMapping> fieldNameMappingCollection = createFieldNameMappingMap(mapping.getReferenceDescriptor(), false);
                //Check DOM Element attributes first
                NamedNodeMap namedNodeMap = entry.getValue().getAttributes();
                for (int i = 0; i < namedNodeMap.getLength(); i++) {
                    Node item = namedNodeMap.item(i);
                    DatabaseMapping mappingCollection = fieldNameMappingCollection.get("@" + item.getLocalName());
                    Converter converter = ((EISDirectMapping) mappingCollection).getConverter();
                    String fieldClassificationName = ((EISDirectMapping) mappingCollection).getFieldClassificationClassName();
                    String typeName = mappingCollection.getAttributeClassification().getTypeName();
                    String value = item.getNodeValue();
                    if (converter == null) {
                        recordValue.put(item.getLocalName(), OracleNoSQLPlatform.getFieldValue(typeName, value, OracleNoSQLPlatform.isLob(typeName)));
                    } else {
                        Session session = SessionManager.getManager().getSession(descriptor.getSessionName());
                        Object convertedValue = converter.convertDataValueToObjectValue(value, session);
                        recordValue.put(item.getLocalName(), OracleNoSQLPlatform.getFieldValue(typeName, convertedValue, OracleNoSQLPlatform.isLob(fieldClassificationName)));
                    }
                }
                //Check DOM Element sub elements
                Node firstChild = entry.getValue().getFirstChild();
                if (firstChild != null && firstChild.getFirstChild() != null) {
                    Node item = firstChild;
                    do {
                        DatabaseMapping mappingCollection = fieldNameMappingCollection.get(item.getLocalName()) != null ? fieldNameMappingCollection.get(item.getLocalName()) : fieldNameMappingCollection.get(item.getLocalName() + "/text()");
                        Converter converter = ((EISDirectMapping) mappingCollection).getConverter();
                        String fieldClassificationName = ((EISDirectMapping) mappingCollection).getFieldClassificationClassName();
                        String typeName = mappingCollection.getAttributeClassification().getTypeName();
                        String value = item.getFirstChild().getNodeValue();
                        if (converter == null) {
                            recordValue.put(item.getLocalName(), OracleNoSQLPlatform.getFieldValue(typeName, value, OracleNoSQLPlatform.isLob(typeName)));
                        } else {
                            Session session = SessionManager.getManager().getSession(descriptor.getSessionName());
                            Object convertedValue = converter.convertDataValueToObjectValue(value, session);
                            recordValue.put(item.getLocalName(), OracleNoSQLPlatform.getFieldValue(typeName, convertedValue, OracleNoSQLPlatform.isLob(fieldClassificationName)));
                        }
                        item = item.getNextSibling();
                    } while (item != null);
                }
                if (recordValue.size() > 0) {
                    mapValue.put(key, recordValue);
                }
            } else if (mapping instanceof EISCompositeCollectionMapping) {
                ArrayValue arrayValue = new ArrayValue();
                Map<String, DatabaseMapping> fieldNameMappingCollection = createFieldNameMappingMap(mapping.getReferenceDescriptor(), false);
                List<DOMRecord> domRecordList = (List) domRecord.getValues(key);
                for (DOMRecord domRecordListItem : domRecordList) {
                    MapValue recordValue = new MapValue();
                    //Check DOM Element attributes first
                    NamedNodeMap attributes = domRecordListItem.getDOM().getAttributes();
                    for (int i = 0; i < attributes.getLength(); i++) {
                        Node item = attributes.item(i);
                        DatabaseMapping mappingCollection = fieldNameMappingCollection.get("@" + item.getLocalName());
                        Converter converter = ((EISDirectMapping) mappingCollection).getConverter();
                        String fieldClassificationName = ((EISDirectMapping) mappingCollection).getFieldClassificationClassName();
                        String typeName = mappingCollection.getAttributeClassification().getTypeName();
                        String value = item.getNodeValue();
                        if (converter == null) {
                            recordValue.put(item.getLocalName(), OracleNoSQLPlatform.getFieldValue(typeName, value, OracleNoSQLPlatform.isLob(typeName)));
                        } else {
                            Session session = SessionManager.getManager().getSession(descriptor.getSessionName());
                            Object convertedValue = converter.convertDataValueToObjectValue(value, session);
                            recordValue.put(item.getLocalName(), OracleNoSQLPlatform.getFieldValue(typeName, convertedValue, OracleNoSQLPlatform.isLob(fieldClassificationName)));
                        }
                    }
                    //Check DOM Element sub elements
                    if (domRecordListItem.getValues().size() > 0) {
                        for (Map.Entry<DatabaseField, Element> listItemEntry : (Set<Map.Entry<DatabaseField, Element>>) domRecordListItem.entrySet()) {
                            DatabaseMapping mappingCollection = fieldNameMappingCollection.get(listItemEntry.getValue().getLocalName()) != null ? fieldNameMappingCollection.get(listItemEntry.getValue().getLocalName()) : fieldNameMappingCollection.get(listItemEntry.getValue().getLocalName() + "/text()");
                            Converter converter = ((EISDirectMapping) mappingCollection).getConverter();
                            String fieldClassificationName = ((EISDirectMapping) mappingCollection).getFieldClassificationClassName();
                            String typeName = mappingCollection.getAttributeClassification().getTypeName();
                            String value = listItemEntry.getValue().getFirstChild().getNodeValue();
                            if (converter == null) {
                                recordValue.put(listItemEntry.getValue().getLocalName(), OracleNoSQLPlatform.getFieldValue(typeName, value, OracleNoSQLPlatform.isLob(typeName)));
                            } else {
                                Session session = SessionManager.getManager().getSession(descriptor.getSessionName());
                                Object convertedValue = converter.convertDataValueToObjectValue(value, session);
                                recordValue.put(listItemEntry.getValue().getLocalName(), OracleNoSQLPlatform.getFieldValue(typeName, convertedValue, OracleNoSQLPlatform.isLob(fieldClassificationName)));
                            }
                        }
                    }
                    if (recordValue.size() > 0) {
                        arrayValue.add(recordValue);
                    }
                }
                mapValue.put(key, arrayValue);
            }
        }
        return mapValue;
    }

    private MapValue createMapValue(Map recordEntry, ClassDescriptor descriptor) {
        Map<String, DatabaseMapping> fieldNameMapping = createFieldNameMappingMap(descriptor, false);
        MapValue mapValue = new MapValue();
        for (Map.Entry<String, Object> entry : (Set<Map.Entry<String, Object>>) recordEntry.entrySet()) {
            if (entry.getValue() != null) {
                String key = entry.getKey();
                String typesKey = descriptor.buildField(entry.getKey()).toString();
                DatabaseMapping mapping = fieldNameMapping.get(typesKey);
                if (mapping == null) {
                    mapping = fieldNameMapping.get(typesKey.replace("/text()", ""));
                }
                if (mapping == null) {
                    mapping = fieldNameMapping.get(descriptor.getTableName() + "." + typesKey.replace("/text()", ""));
                }
                if (mapping instanceof EISDirectMapping) {
                    Converter converter = ((EISDirectMapping) mapping).getConverter();
                    String fieldClassificationName = ((EISDirectMapping) mapping).getFieldClassificationClassName();
                    String typeName = mapping.getAttributeClassification().getTypeName();
                    String value = entry.getValue().toString();
                    if (converter == null) {
                        mapValue.put(key, OracleNoSQLPlatform.getFieldValue(typeName, value, OracleNoSQLPlatform.isLob(fieldClassificationName)));
                    } else {
                        Session session = SessionManager.getManager().getSession(descriptor.getSessionName());
                        Object convertedValue = converter.convertDataValueToObjectValue(value, session);
                        mapValue.put(key, OracleNoSQLPlatform.getFieldValue(typeName, convertedValue, OracleNoSQLPlatform.isLob(fieldClassificationName)));
                    }
                } else if (mapping instanceof EISCompositeDirectCollectionMapping) {
                    ArrayValue arrayValue = new ArrayValue();
                    for (Object collectionItem : (List<Object>) entry.getValue()) {
                        StringValue recordValue;
                        if (collectionItem instanceof String) {
                            recordValue = new StringValue((String)collectionItem);
                            arrayValue.add(recordValue);
                        } else if (collectionItem instanceof String[]) {
                            for (String collectionSubItem : (String[]) collectionItem) {
                                recordValue = new StringValue(collectionSubItem);
                                arrayValue.add(recordValue);
                            }
                        }
                    }
                    mapValue.put(key, arrayValue);
                } else if (mapping instanceof EISCompositeObjectMapping) {
                    MapValue recordValue = new MapValue();
                    Map<String, DatabaseMapping> fieldNameMappingCollection = createFieldNameMappingMap(mapping.getReferenceDescriptor(), false);
                    for (Map<String, String> compositeObject : (List<Map>) entry.getValue()) {
                        for (Map.Entry<String, String> compositeObjectEntry : compositeObject.entrySet()) {
                            DatabaseMapping mappingCollection = fieldNameMappingCollection.get(compositeObjectEntry.getKey());
                            Converter converter = ((EISDirectMapping) mappingCollection).getConverter();
                            String fieldClassificationName = ((EISDirectMapping) mappingCollection).getFieldClassificationClassName();
                            String typeName = mappingCollection.getAttributeClassification().getTypeName();
                            String value = compositeObjectEntry.getValue();
                            if (value != null) {
                                if (converter == null) {
                                    recordValue.put(compositeObjectEntry.getKey(), OracleNoSQLPlatform.getFieldValue(typeName, value, OracleNoSQLPlatform.isLob(typeName)));
                                } else {
                                    Session session = SessionManager.getManager().getSession(descriptor.getSessionName());
                                    Object convertedValue = converter.convertDataValueToObjectValue(value, session);
                                    recordValue.put(compositeObjectEntry.getKey(), OracleNoSQLPlatform.getFieldValue(typeName, convertedValue, OracleNoSQLPlatform.isLob(fieldClassificationName)));
                                }
                            }
                        }
                    }
                    if (recordValue.size() > 0) {
                        mapValue.put(key, recordValue);
                    }
                } else if (mapping instanceof EISCompositeCollectionMapping) {
                    ArrayValue arrayValue = new ArrayValue();
                    Map<String, DatabaseMapping> fieldNameMappingCollection = createFieldNameMappingMap(mapping.getReferenceDescriptor(), false);
                    for (Map<String, Object> compositeObjectList : (List<Map<String, Object>>) entry.getValue()) {
                        MapValue recordValue = new MapValue();
                        for (Map.Entry<String, Object> compositeObjectEntry : compositeObjectList.entrySet()) {
                            DatabaseMapping mappingCollection = fieldNameMappingCollection.get(compositeObjectEntry.getKey());
                            Converter converter = ((EISDirectMapping) mappingCollection).getConverter();
                            String fieldClassificationName = ((EISDirectMapping) mappingCollection).getFieldClassificationClassName();
                            String typeName = mappingCollection.getAttributeClassification().getTypeName();
                            Object value = compositeObjectEntry.getValue();
                            if (value != null) {
                                if (converter == null) {
                                    recordValue.put(compositeObjectEntry.getKey(), OracleNoSQLPlatform.getFieldValue(typeName, value, OracleNoSQLPlatform.isLob(typeName)));
                                } else {
                                    Session session = SessionManager.getManager().getSession(descriptor.getSessionName());
                                    Object convertedValue = converter.convertDataValueToObjectValue(value, session);
                                    recordValue.put(compositeObjectEntry.getKey(), OracleNoSQLPlatform.getFieldValue(typeName, convertedValue, OracleNoSQLPlatform.isLob(fieldClassificationName)));
                                }
                            }
                        }
                        arrayValue.add(recordValue);
                    }
                    mapValue.put(key, arrayValue);
                }
            }
        }
        return mapValue;
    }

    private OracleNoSQLRecord createMapFromDOMRecord(DOMRecord domRecord) {
        OracleNoSQLRecord map = new OracleNoSQLRecord();
        for (Map.Entry<DatabaseField, Element> entry : (Set<Map.Entry<DatabaseField, Element>>) domRecord.entrySet()) {
            String key = entry.getKey().getName();
            String value = entry.getValue().getFirstChild().getNodeValue();
            map.put(key, value);
        }
        return map;
    }

    private Object unboxFieldValue(FieldValue value, InteractionSpec spec, boolean isJson) {
        Object result = null;
        if (value instanceof ArrayValue) {
            if (((OracleNoSQLInteractionSpec)spec).getInteractionType() == OracleNoSQLInteractionSpec.InteractionType.XML) {
                //Output as an array works for XMLInteraction
                result = new int[((ArrayValue) value).size()];
                if (((ArrayValue) value).size() > 0) {
                    result = Array.newInstance((unboxFieldValue(((ArrayValue) value).get(0), spec, isJson).getClass()), ((ArrayValue) value).size());
                }
                for (int i = 0; i < ((ArrayValue) value).size(); i++) {
                    ((Object[]) result)[i] = unboxFieldValue(((ArrayValue) value).get(i), spec, isJson);
                }
            } else if (((OracleNoSQLInteractionSpec)spec).getInteractionType() == OracleNoSQLInteractionSpec.InteractionType.MAPPED) {
                //Output as a collection (List) works for MappedInteraction
                if (((ArrayValue) value).size() > 0) {
                    result = new ArrayList(((ArrayValue) value).size());
                    for (int i = 0; i < ((ArrayValue) value).size(); i++) {
                        ((List) result).add(unboxFieldValue(((ArrayValue) value).get(i), spec, isJson));
                    }
                }
            }
        } else if (value instanceof BinaryValue) {
            result = value.getBinary();
        } else if (value instanceof BooleanValue) {
            result = value.getBoolean();
        } else if (value instanceof DoubleValue) {
            result = value.getDouble();
        } else if (value instanceof IntegerValue) {
            result = value.getInt();
        } else if (value instanceof LongValue) {
            result = value.getLong();
        } else if (value instanceof MapValue) {
            if (isJson) {
                result = value.toJson();
            } else {
                Map<String, FieldValue> mapValue = ((MapValue) value).getMap();
                Map<String, Object> resultMap = new HashMap<>();
                for (Map.Entry<String, FieldValue> entry : mapValue.entrySet()) {
                    resultMap.put(entry.getKey(), unboxFieldValue(entry.getValue(), spec, isJson));
                }
                result = resultMap;
            }
        } else if (value instanceof NullValue) {
            result = null;
        } else if (value instanceof NumberValue) {
            result = value.getNumber();
        } else if (value instanceof StringValue) {
            result = value.getString();
        } else if (value instanceof TimestampValue) {
            result = value.getTimestamp();
        }
        return result;
    }

    private PutRequest createPutRequest(DOMRecord domRecord, String tableName, ClassDescriptor descriptor) {
        MapValue mapValue = createMapValue(domRecord, descriptor);
        return new PutRequest().setValue(mapValue).setTableName(tableName);
    }

    private PutRequest createPutRequest(Map input, String tableName, ClassDescriptor descriptor) {
        MapValue mapValue = createMapValue(input, descriptor);
        return new PutRequest().setValue(mapValue).setTableName(tableName);
    }

    private Map<String, DatabaseMapping> createFieldNameMappingMap(ClassDescriptor descriptor, boolean withoutTextXPath) {
        Map<String, DatabaseMapping> result = new HashMap<>();
        for (DatabaseMapping mapping : descriptor.getMappings()) {
            for (DatabaseField field : mapping.getFields()) {
                result.put((withoutTextXPath) ? field.getQualifiedName().replace("/text()", "") : field.getQualifiedName(), mapping);
            }
        }
        return result;
    }

    private boolean isDBFieldJSONType(String tableName, String fieldName, Map<String, DatabaseMapping> fieldNameMapping) {
        DatabaseMapping mapping = fieldNameMapping.get(tableName + "." + fieldName);
        if (mapping == null) {
            mapping = fieldNameMapping.get(tableName + "." + fieldName.toUpperCase());
        }
        return JSON_DEFAULT_TYPE.equalsIgnoreCase(mapping.getField().getColumnDefinition());
    }
}
