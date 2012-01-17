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
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.resource.*;
import javax.resource.cci.*;

import org.eclipse.persistence.eis.EISException;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * Interaction to Mongo JCA adapter.
 * Executes the interaction spec to enqueue or dequeue a message.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoInteraction implements Interaction {

    /** Store the connection the interaction was created from. */
    protected MongoConnection connection;

    /**
     * Default constructor.
     */
    public MongoInteraction(MongoConnection connection) {
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
        if (!(spec instanceof MongoInteractionSpec)) {
            throw EISException.invalidInteractionSpecType();
        }
        if (!(input instanceof MongoRecord) || !(output instanceof MongoRecord)) {
            throw EISException.invalidRecordType();
        }
        MongoInteractionSpec mongoSpec = (MongoInteractionSpec)spec;
        MongoRecord record = (MongoRecord) input;
        MongoRecord translationRecord = (MongoRecord) output;
        MongoOperation operation = mongoSpec.getOperation();
        String collectionName = mongoSpec.getCollection();
        if (operation == null) {
            throw new ResourceException("Mongo operation must be set");
        }
        if (collectionName == null) {
            throw new ResourceException("DB Collection name must be set");
        }
        try {
            DBCollection collection = this.connection.getDB().getCollection(collectionName);
            DBObject object = buildDBObject(record);
            DBObject translation = buildDBObject(translationRecord);
            if (operation == MongoOperation.UPDATE) {
                WriteResult result = collection.update(translation, object);
                return result.getN() > 0;
            } else {
                throw new ResourceException("Invalid operation: " + operation);                
            }
        } catch (Exception exception) {
            ResourceException resourceException = new ResourceException(exception.toString());
            resourceException.initCause(exception);
            throw resourceException;
        }
    }

    /**
     * Execute the interaction and return output record.
     * The spec is either GET, PUT or DELETE interaction.
     */
    public Record execute(InteractionSpec spec, Record record) throws ResourceException {
        if (!(spec instanceof MongoInteractionSpec)) {
            throw EISException.invalidInteractionSpecType();
        }
        if (!(record instanceof MongoRecord)) {
            throw EISException.invalidRecordType();
        }
        MongoInteractionSpec mongoSpec = (MongoInteractionSpec)spec;
        MongoRecord input = (MongoRecord) record;
        MongoOperation operation = mongoSpec.getOperation();
        String collectionName = mongoSpec.getCollection();
        if (operation == null) {
            ResourceException resourceException = new ResourceException("Mongo operation must be set");
            throw resourceException;            
        }
        if (collectionName == null) {
            ResourceException resourceException = new ResourceException("DB Collection name must be set");
            throw resourceException;            
        }
        try {
            DBCollection collection = this.connection.getDB().getCollection(collectionName);
            DBObject object = buildDBObject(input);
            if (operation == MongoOperation.INSERT) {
                collection.insert(object);
            } else if (operation == MongoOperation.REMOVE) {
                collection.remove(object);
            } else if (operation == MongoOperation.FIND) {
                DBCursor cursor = collection.find(object);
                if (!cursor.hasNext()) {
                    return null;
                }
                MongoListRecord results = new MongoListRecord();
                while (cursor.hasNext()) {
                    DBObject result = cursor.next();
                    results.add(buildRecordFromDBObject(result));
                }
                return results;
            } else {
                throw new ResourceException("Invalid operation: " + operation);                
            }
        } catch (Exception exception) {
            ResourceException resourceException = new ResourceException(exception.toString());
            resourceException.initCause(exception);
            throw resourceException;
        }
        return null;
    }
    
    /**
     * Build the Mongo DBObject from the Map record.
     */
    public DBObject buildDBObject(MongoRecord record) {
        DBObject object = new BasicDBObject();
        for (Iterator iterator = record.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            if (entry.getValue() instanceof MongoRecord) {
                object.put((String)entry.getKey(), buildDBObject((MongoRecord)entry.getValue()));             
            } else {
                object.put((String)entry.getKey(), entry.getValue());
            }
        }
        return object;
    }
    
    /**
     * Build the Map record from the Mongo DBObject.
     */
    public MongoRecord buildRecordFromDBObject(DBObject object) {
        MongoRecord record = new MongoRecord();
        for (Iterator iterator = object.toMap().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            if (entry.getValue() instanceof BasicDBList) {
                List values = new ArrayList();
                for (Iterator valuesIterator = ((BasicDBList)entry.getValue()).iterator(); valuesIterator.hasNext(); ) {
                    Object value = valuesIterator.next();
                    if (value instanceof DBObject) {
                        values.add(buildRecordFromDBObject((DBObject)value));                        
                    } else {
                        values.add(value);
                    }
                }
                record.put((String)entry.getKey(), values);                
            } else if (entry.getValue() instanceof DBObject) {
                MongoRecord nestedRecord = buildRecordFromDBObject((DBObject)entry.getValue());
                record.put((String)entry.getKey(), nestedRecord);
            } else {
                record.put((String)entry.getKey(), entry.getValue());
            }
        }
        return record;
    }
    
    public Connection getConnection() {
        return connection;
    }

    public ResourceWarning getWarnings() {
        return null;
    }
}
