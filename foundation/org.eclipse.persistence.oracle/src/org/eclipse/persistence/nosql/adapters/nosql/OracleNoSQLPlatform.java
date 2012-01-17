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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
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
                Object value = (String)entry.getValue();
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
            call.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.PUT);
            queryManager.setInsertCall(call);
        }
        
        // Update
        if (!queryManager.hasUpdateQuery()) {
            EISInteraction call = isXML ? new XMLInteraction() : new MappedInteraction();
            call.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.PUT);
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
            call.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.GET);
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
}
