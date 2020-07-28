/*
 * Copyright (c) 2011, 2020 Oracle and/or its affiliates. All rights reserved.
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

    @Override
    public void clearWarnings() {
    }

    @Override
    public void close() {
    }

    /**
     * Output records are not supported/required.
     */
    public boolean execute(InteractionSpec spec, javax.resource.cci.Record input, javax.resource.cci.Record output) throws ResourceException {
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
                WriteResult result = collection.update(translation, object, mongoSpec.isUpsert(), mongoSpec.isMulti());
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
    public javax.resource.cci.Record execute(InteractionSpec spec, javax.resource.cci.Record record) throws ResourceException {
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
        if (operation == MongoOperation.EVAL) {
            Object result = this.connection.getDB().eval(mongoSpec.getCode());
            return buildRecordFromDBObject((DBObject)result);
        }
        if (collectionName == null) {
            ResourceException resourceException = new ResourceException("DB Collection name must be set");
            throw resourceException;
        }
        try {
            DBCollection collection = this.connection.getDB().getCollection(collectionName);
            if (mongoSpec.getOptions() > 0) {
                collection.setOptions(mongoSpec.getOptions());
            }
            if (mongoSpec.getReadPreference() != null) {
                collection.setReadPreference(mongoSpec.getReadPreference());
            }
            if (mongoSpec.getWriteConcern() != null) {
                collection.setWriteConcern(mongoSpec.getWriteConcern());
            }
            if (operation == MongoOperation.INSERT) {
                DBObject object = buildDBObject(input);
                collection.insert(object);
            } else if (operation == MongoOperation.REMOVE) {
                DBObject object = buildDBObject(input);
                collection.remove(object);
            } else if (operation == MongoOperation.FIND) {
                DBObject sort = null;
                if (input.containsKey(MongoRecord.SORT)) {
                    sort = buildDBObject((MongoRecord)input.get(MongoRecord.SORT));
                    input.remove(MongoRecord.SORT);
                }
                DBObject select = null;
                if (input.containsKey("$select")) {
                    select = buildDBObject((MongoRecord)input.get("$select"));
                    input.remove("$select");
                }
                DBObject object = buildDBObject(input);
                DBCursor cursor = collection.find(object, select);
                if (sort != null) {
                    cursor.sort(sort);
                }
                try {
                    if (mongoSpec.getSkip() > 0) {
                        cursor.skip(mongoSpec.getSkip());
                    }
                    if (mongoSpec.getLimit() != 0) {
                        cursor.limit(mongoSpec.getLimit());
                    }
                    if (mongoSpec.getBatchSize() != 0) {
                        cursor.batchSize(mongoSpec.getBatchSize());
                    }
                    if (!cursor.hasNext()) {
                        return null;
                    }
                    MongoListRecord results = new MongoListRecord();
                    while (cursor.hasNext()) {
                        DBObject result = cursor.next();
                        results.add(buildRecordFromDBObject(result));
                    }
                    return results;
                } finally {
                    cursor.close();
                }

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

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public ResourceWarning getWarnings() {
        return null;
    }
}
