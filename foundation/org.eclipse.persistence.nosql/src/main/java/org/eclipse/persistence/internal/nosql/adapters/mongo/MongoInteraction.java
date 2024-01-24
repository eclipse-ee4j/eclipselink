/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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

import com.mongodb.client.MongoCursor;
import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.Interaction;
import jakarta.resource.cci.InteractionSpec;
import jakarta.resource.cci.ResourceWarning;

import org.bson.Document;
import org.eclipse.persistence.eis.EISException;

import com.mongodb.BasicDBList;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

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
    @Override
    public boolean execute(InteractionSpec spec, jakarta.resource.cci.Record input, jakarta.resource.cci.Record output) throws ResourceException {
        if (!(spec instanceof MongoInteractionSpec mongoSpec)) {
            throw EISException.invalidInteractionSpecType();
        }
        if (!(input instanceof MongoRecord record) || !(output instanceof MongoRecord translationRecord)) {
            throw EISException.invalidRecordType();
        }
        MongoOperation operation = mongoSpec.getOperation();
        String collectionName = mongoSpec.getCollection();
        if (operation == null) {
            throw new ResourceException("Mongo operation must be set");
        }
        if (collectionName == null) {
            throw new ResourceException("DB Collection name must be set");
        }
        try {
            MongoCollection collection = this.connection.getDB().getCollection(collectionName);
            Document object = buildDocument(record);
            Document translation = buildDocument(translationRecord);
            if (operation == MongoOperation.UPDATE) {
                UpdateResult result = null;
                Document document = new Document("$set", object);
                UpdateOptions updateOptions = new UpdateOptions().upsert(mongoSpec.isUpsert());
                if (mongoSpec.isMulti()) {
                    result = collection.updateMany(translation, document, updateOptions);
                } else {
                    result = collection.updateOne(translation, document, updateOptions);
                }
                return result.getModifiedCount() > 0;
            } else {
                throw new ResourceException("Invalid operation: " + operation);
            }
        } catch (Exception exception) {
            throw new ResourceException(exception.toString(), exception);
        }
    }

    /**
     * Execute the interaction and return output record.
     * The spec is either GET, PUT or DELETE interaction.
     */
    @Override
    public jakarta.resource.cci.Record execute(InteractionSpec spec, jakarta.resource.cci.Record record) throws ResourceException {
        if (!(spec instanceof MongoInteractionSpec mongoSpec)) {
            throw EISException.invalidInteractionSpecType();
        }
        if (!(record instanceof MongoRecord input)) {
            throw EISException.invalidRecordType();
        }
        MongoOperation operation = mongoSpec.getOperation();
        String collectionName = mongoSpec.getCollection();
        if (operation == null) {
            throw new ResourceException("Mongo operation must be set");
        }
        if (operation == MongoOperation.EVAL) {
            Document document = this.connection.getDB().runCommand(Document.parse(mongoSpec.getCode()));
            MongoListRecord results = null;
            if (document.get("cursor") != null && ((Document)document.get("cursor")).get("firstBatch") != null) {
                List<Document> resultList = (List)(((Document)(document.get("cursor"))).get("firstBatch"));
                results = new MongoListRecord();
                for (Document result: resultList)
                    results.add(buildRecordFromDocument(result));
            }
            return results;
        }
        if (collectionName == null) {
            throw new ResourceException("DB Collection name must be set");
        }
        try {
            MongoCollection collection = this.connection.getDB().getCollection(collectionName);
            if (mongoSpec.getOptions() > 0) {
                // FIXME doesn't exist in new MongoDB driver: collection.setOptions(mongoSpec.getOptions());
            }
            if (mongoSpec.getReadPreference() != null) {
                collection = collection.withReadPreference(mongoSpec.getReadPreference());
            }
            if (mongoSpec.getWriteConcern() != null) {
                collection = collection.withWriteConcern(mongoSpec.getWriteConcern());
            }
            if (operation == MongoOperation.INSERT) {
                Document document = buildDocument(input);
                collection.insertOne(document);
            } else if (operation == MongoOperation.REMOVE) {
                Document document = buildDocument(input);
                collection.deleteOne(document);
            } else if (operation == MongoOperation.FIND) {
                Document sort = null;
                if (input.containsKey(MongoRecord.SORT)) {
                    sort = buildDocument((MongoRecord)input.get(MongoRecord.SORT));
                    input.remove(MongoRecord.SORT);
                }
                Document select = null; // $select in the input must be removed otherwise it leads into Exception
                if (input.containsKey("$select")) {
                    select = buildDocument((MongoRecord)input.get("$select"));
                    input.remove("$select");
                }
                Document document = buildDocument(input);
                FindIterable<Document> iterable = collection.find(document);
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
                        results.add(buildRecordFromDocument(result));
                    }
                    return results;
                } finally {
                    cursor.close();
                }

            } else {
                throw new ResourceException("Invalid operation: " + operation);
            }
        } catch (Exception exception) {
            throw new ResourceException(exception);
        }
        return null;
    }

    /**
     * Build the Document (BSON flexible) from the Map record.
     */
    public Document buildDocument(MongoRecord record) {
        Document document = new Document();
        for (Iterator<Map.Entry<String, ?>> iterator = record.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, ?> entry = iterator.next();
            if (entry.getValue() instanceof MongoRecord) {
                document.put(entry.getKey(), buildDocument((MongoRecord)entry.getValue()));
            } else {
                document.put(entry.getKey(), entry.getValue());
            }
        }
        return document;
    }

    /**
     * Build the Map record from the Document (BSON flexible).
     */
    @SuppressWarnings({"rawtypes"})
    public MongoRecord buildRecordFromDocument(Document document) {
        MongoRecord record = new MongoRecord();
        for (Iterator iterator = document.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            if (entry.getValue() instanceof BasicDBList) {
                List<Object> values = new ArrayList<>();
                for (Iterator<Object> valuesIterator = ((BasicDBList)entry.getValue()).iterator(); valuesIterator.hasNext(); ) {
                    Object value = valuesIterator.next();
                    if (value instanceof Document) {
                        values.add(buildRecordFromDocument((Document) value));
                    } else {
                        values.add(value);
                    }
                }
                record.put(entry.getKey(), values);
            } else if (entry.getValue() instanceof Document) {
                MongoRecord nestedRecord = buildRecordFromDocument((Document)entry.getValue());
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
