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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.bson.BsonReader;
import org.bson.BsonTimestamp;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClient;

/**
 * Provides java.sql.Timestamp, java.sql.Date and java.sql.Time codecs.
 *
 * @see <a href="http://mongodb.github.io/mongo-java-driver/3.0/bson/codecs/">Codec and CodecRegistry</a>
 * @see <a href="https://jira.mongodb.org/browse/JAVA-1741">JAVA-1741: 3.x: How to save java.sql.Timestamp?</a>
 */
public class MongoCodecs {
    private static volatile CodecRegistry codecRegistry;

    public static CodecRegistry codecRegistry() {
        if (codecRegistry == null) {
            synchronized (MongoCodecs.class) {
                if (codecRegistry == null) {
                    Map<BsonType, Class<?>> replacements = new HashMap<BsonType, Class<?>>();
                    replacements.put(BsonType.TIMESTAMP, Timestamp.class);
                    // make it use ByteArrayCodec instead of BinaryCodec
                    // see also https://jira.mongodb.org/browse/JAVA-2025
                    replacements.put(BsonType.BINARY, byte[].class);
                    // TODO: replacement for java.sql.Date and java.sql.Time
                    //  need a way to add two distinct replacements
                    //  can't use DATE_TIME, used for java.util.Date
                    BsonTypeClassMap bsonTypeClassMap = new BsonTypeClassMap(replacements);
                    DocumentCodecProvider documentCodecProvider =
                            new DocumentCodecProvider(bsonTypeClassMap);
                    codecRegistry = CodecRegistries.fromRegistries(
                            CodecRegistries.fromCodecs(
                                    new TimestampCodec(),
                                    new DateCodec(),
                                    new TimeCodec()),
                            CodecRegistries.fromProviders(documentCodecProvider),
                            MongoClient.getDefaultCodecRegistry());
                }
            }
        }
        return codecRegistry;
    }

    public static class TimestampCodec implements Codec<Timestamp> {
        @Override
        public void encode(BsonWriter writer, Timestamp value, EncoderContext encoderContext) {
            writer.writeTimestamp(new BsonTimestamp((int)(value.getTime() / 1000), 0));
        }

        @Override
        public Timestamp decode(BsonReader reader, DecoderContext decoderContext) {
            return new Timestamp(reader.readTimestamp().getTime() * 1000L);
        }

        @Override
        public Class<Timestamp> getEncoderClass() {
            return Timestamp.class;
        }
    }

    static class DateCodec implements Codec<Date> {
        @Override
        public void encode(BsonWriter writer, Date value, EncoderContext encoderContext) {
            writer.writeDateTime(value.getTime());
        }

        @Override
        public Date decode(BsonReader reader, DecoderContext decoderContext) {
            return new Date(reader.readDateTime());
        }

        @Override
        public Class<Date> getEncoderClass() {
            return Date.class;
        }
    }

    static class TimeCodec implements Codec<Time> {
        @Override
        public void encode(BsonWriter writer, Time value, EncoderContext encoderContext) {
            writer.writeDateTime(value.getTime());
        }

        @Override
        public Time decode(BsonReader reader, DecoderContext decoderContext) {
            return new Time(reader.readDateTime());
        }

        @Override
        public Class<Time> getEncoderClass() {
            return Time.class;
        }
    }

}
