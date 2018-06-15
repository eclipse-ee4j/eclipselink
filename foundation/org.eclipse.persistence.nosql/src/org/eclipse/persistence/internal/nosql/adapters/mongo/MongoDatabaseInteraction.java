/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;
import javax.resource.cci.ResourceWarning;

import org.bson.Document;
import org.eclipse.persistence.eis.EISException;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

/**
 * Interaction to Mongo JCA adapter.
 * Executes the interaction spec to enqueue or dequeue a message.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoDatabaseInteraction implements Interaction {

    /** Store the connection the interaction was created from. */
    protected MongoDatabaseConnection connection;

    /**
     * Default constructor.
     */
    public MongoDatabaseInteraction(MongoDatabaseConnection connection) {
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
            MongoCollection<Document> collection = this.connection.getDB().getCollection(collectionName);
            BasicDBObject object = buildDBObject(record);
            BasicDBObject translation = buildDBObject(translationRecord);
            if (operation == MongoOperation.UPDATE) {
                Document update = new Document("$set", object);
                UpdateOptions options = new UpdateOptions().upsert(mongoSpec.isUpsert());
                UpdateResult result;
                if (mongoSpec.isMulti()) {
                    result = collection.updateMany(translation, update, options);
                } else {
                    result = collection.updateOne(translation, update, options);
                }
                return result.getModifiedCount() > 0;
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
    @Override
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
        if (operation == MongoOperation.EVAL) {
            Document commandDocument = new Document("$eval", mongoSpec.getCode())/*.append("args", asList(args))*/;
            Document result = this.connection.getDB().runCommand(commandDocument);
            return buildRecordFromDBObject((Document)result.get("retval"));
        }
        if (collectionName == null) {
            ResourceException resourceException = new ResourceException("DB Collection name must be set");
            throw resourceException;
        }
        try {
            MongoCollection<Document> collection = this.connection.getDB().getCollection(collectionName);
            if (mongoSpec.getOptions() > 0) {
                // FIXME: collection.setOptions(mongoSpec.getOptions());
            }
            if (mongoSpec.getReadPreference() != null) {
                collection = collection.withReadPreference(mongoSpec.getReadPreference());
            }
            if (mongoSpec.getWriteConcern() != null) {
                collection = collection.withWriteConcern(mongoSpec.getWriteConcern());
            }
            if (operation == MongoOperation.INSERT) {
                Document object = buildDocument(input);
                collection.insertOne(object);
            } else if (operation == MongoOperation.REMOVE) {
                Document object = buildDocument(input);
                collection.deleteOne(object);
            } else if (operation == MongoOperation.FIND) {
                BasicDBObject sort = null;
                if (input.containsKey(MongoRecord.SORT)) {
                    sort = buildDBObject((MongoRecord)input.get(MongoRecord.SORT));
                    input.remove(MongoRecord.SORT);
                }
                BasicDBObject select = null; // FIXME: select?
                if (input.containsKey("$select")) {
                    select = buildDBObject((MongoRecord)input.get("$select"));
                    input.remove("$select");
                }
                BasicDBObject object = buildDBObject(input);
                FindIterable<Document> iterable = collection.find(object);
                if (sort != null) {
                    iterable.sort(sort);
                }
                MongoCursor<Document> cursor = iterable.iterator();
                try {
                    if (mongoSpec.getSkip() > 0) {
                        iterable.skip(mongoSpec.getSkip());
                    }
                    if (mongoSpec.getLimit() != 0) {
                        iterable.limit(mongoSpec.getLimit());
                    }
                    if (mongoSpec.getBatchSize() != 0) {
                        iterable.batchSize(mongoSpec.getBatchSize());
                    }
                    if (!cursor.hasNext()) {
                        return null;
                    }
                    MongoListRecord results = new MongoListRecord();
                    while (cursor.hasNext()) {
                        Document result = cursor.next();
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
    public BasicDBObject buildDBObject(MongoRecord record) {
        BasicDBObject object = new BasicDBObject();
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
     * Build the Mongo Document from the Map record.
     */
    public Document buildDocument(MongoRecord record) {
        Document object = new Document();
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
     * Build the Map record from the Mongo Document.
     */
    public MongoRecord buildRecordFromDBObject(Document object) {
        MongoRecord record = new MongoRecord();
        for (Iterator iterator = object.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            if (entry.getValue() instanceof BasicDBList) {
                List values = new ArrayList();
                for (Iterator valuesIterator = ((BasicDBList)entry.getValue()).iterator(); valuesIterator.hasNext(); ) {
                    Object value = valuesIterator.next();
                    if (value instanceof Document) {
                        values.add(buildRecordFromDBObject((Document)value));
                    } else {
                        values.add(value);
                    }
                }
                record.put(entry.getKey(), values);
            } else if (entry.getValue() instanceof Document) {
                MongoRecord nestedRecord = buildRecordFromDBObject((Document)entry.getValue());
                record.put(entry.getKey(), nestedRecord);
            } else {
                record.put(entry.getKey(), entry.getValue());
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
