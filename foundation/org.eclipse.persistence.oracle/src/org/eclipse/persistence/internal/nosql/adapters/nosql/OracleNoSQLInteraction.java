/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.nosql.adapters.nosql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.resource.*;
import javax.resource.cci.*;

import oracle.kv.Direction;
import oracle.kv.Key;
import oracle.kv.KeyValueVersion;
import oracle.kv.Operation;
import oracle.kv.OperationResult;
import oracle.kv.Value;
import oracle.kv.ValueVersion;

import org.eclipse.persistence.eis.EISException;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * Interaction to Oracle NoSQL JCA adapter.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class OracleNoSQLInteraction implements Interaction {

    /** Store the connection the interaction was created from. */
    protected OracleNoSQLConnection connection;

    /**
     * Default constructor.
     */
    public OracleNoSQLInteraction(OracleNoSQLConnection connection) {
        this.connection = connection;
    }

    public void clearWarnings() {
    }

    public void close() {
    }

    /**
     * Output records are not supported/required.
     */
    public boolean execute(InteractionSpec spec, Record input, Record output) throws ResourceException {
        throw ValidationException.operationNotSupported("execute(InteractionSpec, Record, Record)");
    }

    /**
     * Execute the interaction and return output record.
     * The spec is either GET, PUT or DELETE interaction.
     */
    public Record execute(InteractionSpec spec, Record record) throws ResourceException {
        if (!(spec instanceof OracleNoSQLInteractionSpec)) {
            throw EISException.invalidInteractionSpecType();
        }
        if (!(record instanceof OracleNoSQLRecord)) {
            throw EISException.invalidRecordType();
        }
        OracleNoSQLInteractionSpec noSqlSpec = (OracleNoSQLInteractionSpec)spec;
        OracleNoSQLRecord input = (OracleNoSQLRecord) record;
        try {
            OracleNoSQLOperation operation = noSqlSpec.getOperation();
            if (operation == OracleNoSQLOperation.GET) {
                OracleNoSQLRecord output = new OracleNoSQLRecord();
                for (Map.Entry entry : (Set<Map.Entry>)input.entrySet()) {
                    Key key = Key.createKey(createMajorKey(entry.getKey()));
                    Map<Key, ValueVersion> values = this.connection.getStore().multiGet(
                            key, null, null, noSqlSpec.getConsistency(), noSqlSpec.getTimeout(), TimeUnit.MILLISECONDS);
                    if ((values != null) && (!values.isEmpty())) {
                        if (values.size() == 1) {
                            byte[] bytes = values.values().iterator().next().getValue().toByteArray();
                            output.put(entry.getKey(), convertBytes(bytes));                            
                        } else {
                            OracleNoSQLRecord nestedRecord = new OracleNoSQLRecord();
                            for (Map.Entry<Key, ValueVersion> nestedEntry : values.entrySet()) {
                                byte[] bytes =  nestedEntry.getValue().getValue().toByteArray();
                                OracleNoSQLRecord currentNestedRecord = nestedRecord;
                                // Nest each minor path.
                                List<String> minorPaths = nestedEntry.getKey().getMinorPath();
                                if (minorPaths.isEmpty()) {
                                    output.put(entry.getKey(), convertBytes(bytes));                                    
                                } else {
                                    for (int index = 0; index < (minorPaths.size() - 1); index++) {
                                        String path = minorPaths.get(index);
                                        Object nextNestedRecord = currentNestedRecord.get(path);
                                        if (!(nextNestedRecord instanceof OracleNoSQLRecord)) {
                                            nextNestedRecord = new OracleNoSQLRecord();
                                            currentNestedRecord.put(path, nextNestedRecord);
                                        }
                                        currentNestedRecord = (OracleNoSQLRecord)nextNestedRecord;
                                    }
                                    currentNestedRecord.put(minorPaths.get(minorPaths.size() - 1), convertBytes(bytes));
                                }
                            }
                            output.put(entry.getKey(), nestedRecord);
                        }
                    }
                }
                if (output.isEmpty()) {
                    return null;
                }
                return output;
            } else if (operation == OracleNoSQLOperation.ITERATOR) {
                OracleNoSQLRecord output = new OracleNoSQLRecord();
                for (Map.Entry entry : (Set<Map.Entry>)input.entrySet()) {
                    Key key = Key.createKey(createMajorKey(entry.getKey()));
                    Iterator<KeyValueVersion> values = this.connection.getStore().storeIterator(
                            Direction.UNORDERED, 0, key, null, null, noSqlSpec.getConsistency(), noSqlSpec.getTimeout(), TimeUnit.MILLISECONDS);
                    List<String> lastMajorPath = null;
                    OracleNoSQLRecord nestedRecord = null;
                    while (values.hasNext()) {
                        KeyValueVersion pair = values.next();
                        byte[] bytes = pair.getValue().toByteArray();
                        if (pair.getKey().getMinorPath().isEmpty()) {
                            output.put(pair.getKey().getMajorPath(), convertBytes(bytes));
                        } else {
                            if ((lastMajorPath == null) || !lastMajorPath.equals(pair.getKey().getMajorPath())) {
                                lastMajorPath = pair.getKey().getMajorPath();
                                nestedRecord = new OracleNoSQLRecord();
                                output.put(lastMajorPath, nestedRecord);
                            }
                            OracleNoSQLRecord currentNestedRecord = nestedRecord;
                            // Nest each minor path.
                            List<String> minorPaths = pair.getKey().getMinorPath();
                            for (int index = 0; index < (minorPaths.size() - 1); index++) {
                                String path = minorPaths.get(index);
                                Object nextNestedRecord = currentNestedRecord.get(path);
                                if (!(nextNestedRecord instanceof OracleNoSQLRecord)) {
                                    nextNestedRecord = new OracleNoSQLRecord();
                                    currentNestedRecord.put(path, nextNestedRecord);
                                }
                                currentNestedRecord = (OracleNoSQLRecord)nextNestedRecord;
                            }
                            currentNestedRecord.put(minorPaths.get(minorPaths.size() - 1), convertBytes(bytes));
                        }
                    }
                }
                if (output.isEmpty()) {
                    return null;
                }
                return output;
            } else if ((operation == OracleNoSQLOperation.PUT) || (operation == OracleNoSQLOperation.PUT_IF_ABSENT)
                    || (operation == OracleNoSQLOperation.PUT_IF_PRESENT) || (operation == OracleNoSQLOperation.PUT_IF_VERSION)) {
                List<Operation> operations = new ArrayList();
                for (Map.Entry entry : (Set<Map.Entry>)input.entrySet()) {
                    Object inputValue = entry.getValue();
                    List majorKeys = createMajorKey(entry.getKey());
                    List<String> minorKeys = new ArrayList<String>();
                    putValues(inputValue, majorKeys, minorKeys, noSqlSpec, operations);
                }
                List<OperationResult> results = this.connection.getStore().execute(operations, noSqlSpec.getDurability(), noSqlSpec.getTimeout(), TimeUnit.MILLISECONDS);
                for (OperationResult result : results) {
                    if (!result.getSuccess()) {
                        throw new ResourceException("Attempt to put failed:" + input);
                    }
                }
            } else if (operation == OracleNoSQLOperation.DELETE) {
                for (Map.Entry entry : (Set<Map.Entry>)input.entrySet()) {
                    Key key = Key.createKey(createMajorKey(entry.getKey()));
                    this.connection.getStore().multiDelete(key, null, null, noSqlSpec.getDurability(), noSqlSpec.getTimeout(), TimeUnit.MILLISECONDS);
                }
            } else if (operation == OracleNoSQLOperation.DELETE_IF_VERSION) {
                for (Map.Entry entry : (Set<Map.Entry>)input.entrySet()) {
                    Key key = Key.createKey(createMajorKey(entry.getKey()));
                    boolean success = this.connection.getStore().deleteIfVersion(key, noSqlSpec.getVersion(), null, noSqlSpec.getDurability(), noSqlSpec.getTimeout(), TimeUnit.MILLISECONDS);
                    if (!success) {
                        throw new ResourceException("Attempt to delete key failed:" + input);
                    }
                }
            } else {
                throw new ResourceException("Invalid NoSQL operation:" + operation);
            }
        } catch (Exception exception) {
            ResourceException resourceException = new ResourceException(exception.toString());
            resourceException.initCause(exception);
            throw resourceException;
        }
        return null;
    }

    public Connection getConnection() {
        return connection;
    }

    public ResourceWarning getWarnings() {
        return null;
    }

    /**
     * Convert the key into a proper key list.
     * NoSQL uses a List of key values for major keys,
     * this allows for a special key syntax to be used, or a list.
     * "[<key1>,<key2>,..]"
     */
    protected List createMajorKey(Object key) {
        List majorKeys = null;
        if (key instanceof List) {
            majorKeys = (List)key;                        
        } else {
            majorKeys = new ArrayList<String>();
            String keyString = key.toString();
            if ((keyString.length() > 2) && (keyString.charAt(0) == '[') && (keyString.charAt(keyString.length() - 1) == ']')) {
                int startIndex = 1;
                while (startIndex < (keyString.length() - 1)) {
                    int endIndex = keyString.indexOf(',', startIndex);
                    if (endIndex == -1) {
                        endIndex = keyString.length() - 1;
                    }
                    String nextKey = keyString.substring(startIndex, endIndex);
                    majorKeys.add(nextKey);
                    startIndex = endIndex + 1;
                }
            } else {
                majorKeys.add(keyString);
            }
        }
        return majorKeys;
    }
    
    /**
     * Put the value at the major and minor keys.
     * If a nested record, then put each nested value as minor keys.
     */
    protected void putValues(Object element, List<String> majorKeys, List<String> minorKeys, OracleNoSQLInteractionSpec spec, List<Operation> operations) {
        if (element instanceof Collection) {
            element = ((List)element).get(0);
            // Append nested record using minor keys.
            for (Map.Entry nestedEntry : (Set<Map.Entry>)((OracleNoSQLRecord)element).entrySet()) {
                List<String> nestedMinorKeys = new ArrayList<String>(minorKeys);
                nestedMinorKeys.add(nestedEntry.getKey().toString());
                putValues(nestedEntry.getValue(), majorKeys, nestedMinorKeys, spec, operations);
            }            
        } else if (element instanceof OracleNoSQLRecord) {
            // Append nested record using minor keys.
            for (Map.Entry nestedEntry : (Set<Map.Entry>)((OracleNoSQLRecord)element).entrySet()) {
                List<String> nestedMinorKeys = new ArrayList<String>(minorKeys);
                nestedMinorKeys.add(nestedEntry.getKey().toString());
                putValues(nestedEntry.getValue(), majorKeys, nestedMinorKeys, spec, operations);
            }
        } else {
            Key key = Key.createKey(majorKeys, minorKeys);
            Value value = createValue(element);
            if (spec.getOperation() == OracleNoSQLOperation.PUT) {
                operations.add(this.connection.getStore().getOperationFactory().createPut(key, value));
            } else if (spec.getOperation() == OracleNoSQLOperation.PUT_IF_ABSENT) {
                operations.add(this.connection.getStore().getOperationFactory().createPutIfAbsent(key, value));
            } else if (spec.getOperation() == OracleNoSQLOperation.PUT_IF_PRESENT) {
                operations.add(this.connection.getStore().getOperationFactory().createPutIfPresent(key, value));
            } else if (spec.getOperation() == OracleNoSQLOperation.PUT_IF_VERSION) {
                operations.add(this.connection.getStore().getOperationFactory().createPutIfVersion(key, value, spec.getVersion()));
            }
        }
    }
    
    /**
     * Create the NoSQL Value from the data's bytes.
     */
    protected Value createValue(Object data) {
        if (data == null) {
            data = new byte[0];
        } else if (!(data instanceof byte[])) {
            if (!(data instanceof String)) {
                data = data.toString();
            }
            data = ((String)data).getBytes();
        }
        return Value.createValue((byte[])data);
    }
    
    /**
     * Allow processing of the raw bytes.
     */
    protected Object convertBytes(byte[] bytes) {
        if ((bytes.length > 0) && (bytes[0] == 0)) {
            // Some kind of bug in NoSQL it prepends a 0 char.
            bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        }
        if (bytes.length == 0) {
            return null;
        }
        return bytes;
    }
}
