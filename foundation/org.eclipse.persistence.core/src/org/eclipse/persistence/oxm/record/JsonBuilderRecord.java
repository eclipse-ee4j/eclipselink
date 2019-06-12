/*
 * Copyright (c) 2013, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.6 - initial implementation
package org.eclipse.persistence.oxm.record;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.oxm.ConversionManager;

public class JsonBuilderRecord extends JsonRecord<JsonBuilderRecord.Level> {

    private JsonObjectBuilder rootJsonObjectBuilder;
    private JsonArrayBuilder rootJsonArrayBuilder;

    public JsonBuilderRecord() {
        super();
        isLastEventStart = false;
    }

    public JsonBuilderRecord(JsonObjectBuilder jsonObjectBuilder) {
        this();
        rootJsonObjectBuilder = jsonObjectBuilder;
    }

    public JsonBuilderRecord(JsonArrayBuilder jsonArrayBuilder) {
        this();
        rootJsonArrayBuilder = jsonArrayBuilder;
        isRootArray = true;
    }

    @Override
    protected Level createNewLevel(boolean collection, Level parentLevel, boolean nestedArray) {
        return new Level(collection, position, nestedArray);
    }

    @Override
    protected void startRootObject() {
        super.startRootObject();
        position.setJsonObjectBuilder(rootJsonObjectBuilder);
        setComplex(position, true);
    }

    @Override
    protected void finishLevel() {
        if (!(position.isCollection && position.isEmptyCollection() && position.getKeyName() == null)) {

            Level parentLevel = (Level) position.parentLevel;

            if (parentLevel != null) {
                if (parentLevel.isCollection) {
                    if (position.isCollection) {
                        parentLevel.getJsonArrayBuilder().add(position.getJsonArrayBuilder());
                    } else {
                        parentLevel.getJsonArrayBuilder().add(position.getJsonObjectBuilder());
                    }
                } else {
                    if (position.isCollection) {
                        parentLevel.getJsonObjectBuilder().add(position.getKeyName(), position.getJsonArrayBuilder());
                    } else {
                        parentLevel.getJsonObjectBuilder().add(position.getKeyName(), position.getJsonObjectBuilder());
                    }
                }
            }
        }
        super.finishLevel();
    }

    @Override
    protected void startRootLevelCollection() {
        if (rootJsonArrayBuilder == null) {
            rootJsonArrayBuilder = Json.createArrayBuilder();
        }
        position.setJsonArrayBuilder(rootJsonArrayBuilder);
    }

    @Override
    public void endCollection() {
        finishLevel();
    }

    @Override
    protected void setComplex(Level level, boolean complex) {
        boolean isAlreadyComplex = level.isComplex;
        super.setComplex(level, complex);
        if (complex && !isAlreadyComplex) {
            if (level.jsonObjectBuilder == null) {
                level.jsonObjectBuilder = Json.createObjectBuilder();
            }
        }
    }

    @Override
    protected void writeEmptyCollection(Level level, String keyName) {
        level.getJsonObjectBuilder().add(keyName, Json.createArrayBuilder());
    }

    @Override
    protected void addValueToObject(Level level, String keyName, Object value, QName schemaType) {
        JsonObjectBuilder jsonObjectBuilder = level.getJsonObjectBuilder();
        if (value == NULL) {
            jsonObjectBuilder.addNull(keyName);
        } else if (value instanceof Integer) {
            jsonObjectBuilder.add(keyName, (Integer) value);
        } else if (value instanceof BigDecimal) {
            jsonObjectBuilder.add(keyName, (BigDecimal) value);
        } else if (value instanceof BigInteger) {
            jsonObjectBuilder.add(keyName, (BigInteger) value);
        } else if (value instanceof Boolean) {
            jsonObjectBuilder.add(keyName, (Boolean) value);
        } else if (value instanceof Character) {
            jsonObjectBuilder.add(keyName, (Character) value);
        } else if (value instanceof Double) {
            jsonObjectBuilder.add(keyName, (Double) value);
        } else if (value instanceof Float) {
            jsonObjectBuilder.add(keyName, (Float) value);
        } else if (value instanceof Long) {
            jsonObjectBuilder.add(keyName, (Long) value);
        } else if (value instanceof String) {
            jsonObjectBuilder.add(keyName, (String) value);
        } else {
            ConversionManager conversionManager = getConversionManager();
            String convertedValue = (String) conversionManager.convertObject(value, CoreClassConstants.STRING, schemaType);
            Class theClass = conversionManager.javaType(schemaType);
            if ((schemaType == null || theClass == null) && (CoreClassConstants.NUMBER.isAssignableFrom(value.getClass()))) {
                //if it's still a number and falls through the cracks we dont want "" around the value
                BigDecimal convertedNumberValue = ((BigDecimal) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.BIGDECIMAL, schemaType));
                jsonObjectBuilder.add(keyName, convertedNumberValue);
            } else {
                jsonObjectBuilder.add(keyName, convertedValue);
            }

        }
    }

    @Override
    protected void addValueToArray(Level level, Object value, QName schemaType) {
        JsonArrayBuilder jsonArrayBuilder = level.getJsonArrayBuilder();
        if (value == NULL) {
            jsonArrayBuilder.addNull();
        } else if (value instanceof Integer) {
            jsonArrayBuilder.add((Integer) value);
        } else if (value instanceof BigDecimal) {
            jsonArrayBuilder.add((BigDecimal) value);
        } else if (value instanceof BigInteger) {
            jsonArrayBuilder.add((BigInteger) value);
        } else if (value instanceof Boolean) {
            jsonArrayBuilder.add((Boolean) value);
        } else if (value instanceof Character) {
            jsonArrayBuilder.add((Character) value);
        } else if (value instanceof Double) {
            jsonArrayBuilder.add((Double) value);
        } else if (value instanceof Float) {
            jsonArrayBuilder.add((Float) value);
        } else if (value instanceof Long) {
            jsonArrayBuilder.add((Long) value);
        } else if (value instanceof String) {
            jsonArrayBuilder.add((String) value);
        } else {
            ConversionManager conversionManager = getConversionManager();
            String convertedValue = (String) conversionManager.convertObject(value, CoreClassConstants.STRING, schemaType);
            Class theClass = conversionManager.javaType(schemaType);
            if ((schemaType == null || theClass == null) && (CoreClassConstants.NUMBER.isAssignableFrom(value.getClass()))) {
                //if it's still a number and falls through the cracks we dont want "" around the value
                BigDecimal convertedNumberValue = ((BigDecimal) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.BIGDECIMAL, schemaType));
                jsonArrayBuilder.add(convertedNumberValue);
            } else {
                jsonArrayBuilder.add(convertedValue);
            }
        }
    }


    /**
     * Instances of this class are used to maintain state about the current
     * level of the JSON message being marshalled.
     */
    protected static class Level extends JsonRecord.Level {

        private JsonObjectBuilder jsonObjectBuilder;
        private JsonArrayBuilder jsonArrayBuilder;

        public Level(boolean isCollection, Level position, boolean nestedArray) {
            super(isCollection, position, nestedArray);
        }

        @Override
        public void setCollection(boolean isCollection) {
            super.setCollection(isCollection);
            if (isCollection && jsonArrayBuilder == null) {
                jsonArrayBuilder = Json.createArrayBuilder();
            }
        }

        public JsonObjectBuilder getJsonObjectBuilder() {
            return jsonObjectBuilder;
        }

        public void setJsonObjectBuilder(JsonObjectBuilder jsonObjectBuilder) {
            this.jsonObjectBuilder = jsonObjectBuilder;
        }

        public JsonArrayBuilder getJsonArrayBuilder() {
            return jsonArrayBuilder;
        }

        public void setJsonArrayBuilder(JsonArrayBuilder jsonArrayBuilder) {
            this.jsonArrayBuilder = jsonArrayBuilder;
        }

    }

}
