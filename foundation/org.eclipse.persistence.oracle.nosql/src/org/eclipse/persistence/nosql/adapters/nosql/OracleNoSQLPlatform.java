/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.nosql.adapters.nosql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.resource.cci.InteractionSpec;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;

import oracle.kv.Consistency;
import oracle.kv.Durability;
import oracle.kv.Version;

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
import org.eclipse.persistence.internal.nosql.adapters.nosql.OracleNoSQLInteractionSpec;
import org.eclipse.persistence.internal.nosql.adapters.nosql.OracleNoSQLOperation;
import org.eclipse.persistence.internal.nosql.adapters.nosql.OracleNoSQLRecord;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.UUIDSequence;
import org.w3c.dom.Element;

/**
 * Platform for Oracle NoSQL database.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class OracleNoSQLPlatform extends EISPlatform {

    /** OracleNoSQL interaction spec properties. */
    public static String OPERATION = "nosql.operation";
    public static String CONSISTENCY = "nosql.consistency";
    public static String DURABILITY = "nosql.durability";
    public static String TIMEOUT = "nosql.timeout";
    public static String VERSION = "nosql.version";

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
            OracleNoSQLInteractionSpec noSqlSpec = new OracleNoSQLInteractionSpec();
            Object operation = interaction.getProperty(OPERATION);
            if (operation == null) {
                throw new EISException("'" + OPERATION + "' property must be set on the query's interation.");
            }
            if (operation instanceof String) {
                operation = OracleNoSQLOperation.valueOf((String)operation);
            }
            noSqlSpec.setOperation((OracleNoSQLOperation)operation);
            
            // Allows setting of consistency as a property.
            Object consistency = interaction.getProperty(CONSISTENCY);
            if (consistency == null) {
                // Default to session property.
                consistency = interaction.getQuery().getSession().getProperty(CONSISTENCY);
            }
            if (consistency instanceof Consistency) {
                noSqlSpec.setConsistency((Consistency)consistency);
            } else if (consistency instanceof String) {
                String constant = (String)consistency;
                if (constant.equals("ABSOLUTE")) {
                    noSqlSpec.setConsistency(Consistency.ABSOLUTE);
                } else if (constant.equals("NONE_REQUIRED")) {
                    noSqlSpec.setConsistency(Consistency.NONE_REQUIRED );
                } else {
                    throw new EISException("Invalid consistency property value: " + constant);                    
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
                String constant = (String)durability;
                if (constant.equals("COMMIT_NO_SYNC")) {
                    noSqlSpec.setDurability(Durability.COMMIT_NO_SYNC);
                } else if (constant.equals("COMMIT_SYNC")) {
                    noSqlSpec.setDurability(Durability.COMMIT_SYNC );
                }  else if (constant.equals("COMMIT_WRITE_NO_SYNC")) {
                    noSqlSpec.setDurability(Durability.COMMIT_WRITE_NO_SYNC );
                } else {
                    throw new EISException("Invalid durability property value: " + constant);                    
                }
            }
            
            // Allows setting of timeout as a property.
            Object timeout = interaction.getProperty(TIMEOUT);
            if (timeout == null) {
                // Default to session property.
                timeout = interaction.getQuery().getSession().getProperty(TIMEOUT);
            }
            if (timeout instanceof Number) {
                noSqlSpec.setTimeout(((Number)timeout).longValue());
            } else if (timeout instanceof String) {
                noSqlSpec.setTimeout(Long.valueOf(((String)timeout)));
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
                        DatabaseField field = (DatabaseField)((SelectedFieldsLockingPolicy)descriptor.getOptimisticLockingPolicy()).getLockFields().get(0);
                        if (interaction.getInputRow() != null) {
                            version = interaction.getInputRow().get(field);
                        }
                    }
                }
            }
            if (version instanceof Version) {
                noSqlSpec.setVersion((Version)version);
            } else if (version instanceof byte[]) {
                noSqlSpec.setVersion(Version.fromByteArray((byte[])version));
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
    public AbstractRecord buildRow(Record record, EISInteraction interaction, EISAccessor accessor) {
        if (record == null) {
            return null;
        }
        OracleNoSQLRecord output = (OracleNoSQLRecord)record;
        if ((output.size() == 1) && (interaction.getQuery().getDescriptor() != null)) {
            // Check for a nested mapped record.
            Object value = output.values().iterator().next();
            if (value instanceof OracleNoSQLRecord) {
                convertRecordBytesToString((OracleNoSQLRecord)value);
                output = (OracleNoSQLRecord)value;
            }
        }
        Record result = output;
        if (getRecordConverter() != null) {
            result = getRecordConverter().converterFromAdapterRecord(output);
        }
        return interaction.buildRow(result, accessor);
    }

    /**
     * Allow the platform to handle record to row conversion.
     */
    @Override
    public Vector buildRows(Record record, EISInteraction interaction, EISAccessor accessor) {
        if (record == null) {
            return new Vector(0);
        }
        OracleNoSQLRecord output = (OracleNoSQLRecord)record;
        if ((output.size() == 1) && (interaction.getQuery().getDescriptor() != null)) {
            // Check for a nested mapped record.
            Object value = output.values().iterator().next();
            if (value instanceof OracleNoSQLRecord) {
                Vector rows = new Vector(1);
                convertRecordBytesToString((OracleNoSQLRecord)value);
                rows.add(interaction.buildRow((OracleNoSQLRecord)value, accessor));
                return rows;
            } else if (value instanceof Collection) {
                Vector rows = new Vector(((Collection)value).size());
                for (Object nestedValue : (Collection)value) {
                    if (nestedValue instanceof OracleNoSQLRecord) {
                        rows.add(interaction.buildRow((OracleNoSQLRecord)nestedValue, accessor));
                    }
                }
                return rows;
            }
        }
        if (interaction.getQuery().getDescriptor() != null) {
            // Check for a map of values.
            Vector rows = new Vector();
            for (Object value : output.values()) {
                if (value instanceof OracleNoSQLRecord) {
                    convertRecordBytesToString((OracleNoSQLRecord)value);
                    rows.add(interaction.buildRow((OracleNoSQLRecord)value, accessor));
                } else if (value instanceof byte[]) {
                    EISDOMRecord domRecord = new EISDOMRecord();
                    domRecord.transformFromXML(new String((byte[])value));
                    rows.add(domRecord);
                }
            }
            return rows;
        }
        return interaction.buildRows(record, accessor);
    }

    /**
     * INTERNAL:
     * Convert the record and nested records bytes to strings.
     */
    protected void convertRecordBytesToString(OracleNoSQLRecord record) {
        // Convert byte[] to String.
        for (Iterator<Map.Entry> iterator = record.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = iterator.next();
            if (entry.getValue() instanceof byte[]) {
                entry.setValue(new String((byte[])entry.getValue()));
            } else if (entry.getValue() instanceof OracleNoSQLRecord) {
                convertRecordBytesToString((OracleNoSQLRecord)entry.getValue());
            }
        }
    }
    
    /**
     * Allow the platform to create the appropriate type of record for the interaction.
     * Convert the nested local mapped record to a flat global keyed record.
     */
    @Override
    public Record createInputRecord(EISInteraction interaction, EISAccessor accessor) {
        if (interaction instanceof XMLInteraction) {
            return super.createInputRecord(interaction, accessor);
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
     * XML is stored in Oracle NoSQL but storing the XML text, keyed on the object's "<dataTypeName>/<id>".
     */
    @Override
    public void setDOMInRecord(Element dom, Record record, EISInteraction interaction, EISAccessor accessor) {
        OracleNoSQLRecord noSqlRecord = (OracleNoSQLRecord)record;
        org.eclipse.persistence.oxm.record.DOMRecord domRecord = new org.eclipse.persistence.oxm.record.DOMRecord(dom);
        domRecord.setSession(interaction.getQuery().getSession());
        // Create the key from the objects id.
        ClassDescriptor descriptor = interaction.getQuery().getDescriptor();
        if (descriptor == null) {
            throw new EISException("XMLInteraction is only valid for object queries, use MappedIneraction for native queries: " + interaction);
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
        List key = new ArrayList(descriptor.getPrimaryKeyFields().size() + 1);
        if (((EISDescriptor)descriptor).getDataTypeName().length() > 0) {
            key.add(((EISDescriptor)descriptor).getDataTypeName());
        }
        if (id != null) {
            if (id instanceof CacheId) {
                Object[] idValues = ((CacheId)id).getPrimaryKey();
                for (Object idValue : idValues) {
                    String idString = (String)accessor.getDatasourcePlatform().getConversionManager().convertObject(idValue, String.class);
                    key.add(idString);
                }
            } else {
                String idString = (String)accessor.getDatasourcePlatform().getConversionManager().convertObject(id, String.class);
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
    public AbstractRecord createDatabaseRowFromDOMRecord(Record record, EISInteraction call, EISAccessor accessor) {
        if (record == null) {
            return null;
        }
        EISDOMRecord domRecord = null;
        OracleNoSQLRecord noSqlRecord = (OracleNoSQLRecord)record;
        if (noSqlRecord.size() == 0) {
            return null;
        } else if (noSqlRecord.size() == 1) {
            domRecord = new EISDOMRecord();
            Object value = noSqlRecord.values().iterator().next();
            String xml = null;
            if (value instanceof byte[]) {
                xml = new String ((byte[])value);
            } else {
                xml = (String) value;
            }
            if (xml != null) {
                domRecord.transformFromXML(xml);
            }
        } else {
            domRecord = new EISDOMRecord();
            for (Map.Entry entry : (Set<Map.Entry>)noSqlRecord.entrySet()) {
                Object value = entry.getValue();
                String xml = null;
                if (value instanceof byte[]) {
                    xml = new String ((byte[])value);
                } else {
                    xml = (String)value;
                }
                if (xml != null) {
                    EISDOMRecord dom = new EISDOMRecord();
                    dom.transformFromXML(xml);
                    domRecord.put(entry.getKey(), dom);
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
}
