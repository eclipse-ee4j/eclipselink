/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

import javax.json.stream.JsonGenerator;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.oxm.ConversionManager;

public class JsonGeneratorRecord extends JsonRecord<JsonRecord.Level> {

    private JsonGenerator jsonGenerator;
    private String rootKeyName;

    public JsonGeneratorRecord(JsonGenerator generator, String rootKeyName){
        super();
        this.jsonGenerator = generator;
        this.rootKeyName = rootKeyName;
    }

    protected void startRootObject(){
        super.startRootObject();
        position.setKeyName(rootKeyName);
        setComplex(position, true);
    }

    protected void finishLevel(){
        if(!(position.isCollection && position.isEmptyCollection() && position.getKeyName() == null)){
            jsonGenerator.writeEnd();
        }
        super.finishLevel();
    }

    protected void startRootLevelCollection(){
        if(rootKeyName != null){
            jsonGenerator.writeStartArray(rootKeyName);
         }else{
             jsonGenerator.writeStartArray();
         }
    }

    @Override
    public void endCollection() {
        finishLevel();
    }

    protected void setComplex(Level level, boolean complex){
        boolean isAlreadyComplex = level.isComplex;
        super.setComplex(level, complex);
        if(complex && !isAlreadyComplex){
            Level parentLevel = level.parentLevel;
            if(parentLevel != null && parentLevel.isCollection && parentLevel.isEmptyCollection()){
                parentLevel.setEmptyCollection(false);
            }
            if((parentLevel != null && parentLevel.isCollection && !parentLevel.isEmptyCollection()) || level.keyName == null){
                jsonGenerator.writeStartObject();
            }else{
                jsonGenerator.writeStartObject(level.keyName);
            }
        }
    }

    protected void startEmptyCollection(){
        if (position.parentLevel != null && position.parentLevel.getSkipCount() > 0) {
            jsonGenerator.writeStartArray();
        } else {
            jsonGenerator.writeStartArray(position.keyName);
        }
    }

    protected void writeEmptyCollection(Level level, String keyName){
        jsonGenerator.writeStartArray(keyName);
        jsonGenerator.writeEnd();
    }

    protected void addValueToObject(Level level, String keyName, Object value, QName schemaType){

        if(value == NULL){
            jsonGenerator.writeNull(keyName);
        }else if(value instanceof Integer){
            jsonGenerator.write(keyName, (Integer)value);
        }else if(value instanceof BigDecimal){
            jsonGenerator.write(keyName, (BigDecimal)value);
        }else if(value instanceof BigInteger){
            jsonGenerator.write(keyName, (BigInteger)value);
        }else if(value instanceof Boolean){
            jsonGenerator.write(keyName, (Boolean)value);
        }else if(value instanceof Character){
            jsonGenerator.write(keyName, (Character)value);;
        }else if(value instanceof Double){
            jsonGenerator.write(keyName, (Double)value);
        }else if(value instanceof Float){
            jsonGenerator.write(keyName, (Float)value);
        }else if(value instanceof Long){
            jsonGenerator.write(keyName, (Long)value);
        }else if(value instanceof String){
            jsonGenerator.write(keyName, (String)value);
        }else{
            ConversionManager conversionManager = getConversionManager();
            String convertedValue = (String) conversionManager.convertObject(value, CoreClassConstants.STRING, schemaType);
            Class theClass = conversionManager.javaType(schemaType);
            if((schemaType == null || theClass == null) && (CoreClassConstants.NUMBER.isAssignableFrom(value.getClass()))){
                //if it's still a number and falls through the cracks we dont want "" around the value
                    BigDecimal convertedNumberValue = ((BigDecimal) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.BIGDECIMAL, schemaType));
                    jsonGenerator.write(keyName, (BigDecimal)convertedNumberValue);
            }else{
                jsonGenerator.write(keyName, convertedValue);
            }

        }
    }

    protected void addValueToArray(Level level, Object value, QName schemaType){
        if(value == NULL){
            jsonGenerator.writeNull();
        }else if(value instanceof Integer){
            jsonGenerator.write((Integer)value);
        }else if(value instanceof BigDecimal){
            jsonGenerator.write((BigDecimal)value);
        }else if(value instanceof BigInteger){
            jsonGenerator.write((BigInteger)value);
        }else if(value instanceof Boolean){
            jsonGenerator.write((Boolean)value);
        }else if(value instanceof Character){
            jsonGenerator.write((Character)value);
        }else if(value instanceof Double){
            jsonGenerator.write((Double)value);
        }else if(value instanceof Float){
            jsonGenerator.write((Float)value);
        }else if(value instanceof Long){
            jsonGenerator.write((Long)value);
        }else if(value instanceof String){
            jsonGenerator.write((String)value);
        }else{
            ConversionManager conversionManager = getConversionManager();
            String convertedValue = (String) conversionManager.convertObject(value, CoreClassConstants.STRING, schemaType);
            Class theClass = conversionManager.javaType(schemaType);
            if((schemaType == null || theClass == null) && (CoreClassConstants.NUMBER.isAssignableFrom(value.getClass()))){
                //if it's still a number and falls through the cracks we dont want "" around the value
                    BigDecimal convertedNumberValue = ((BigDecimal) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.BIGDECIMAL, schemaType));
                    jsonGenerator.write((BigDecimal)convertedNumberValue);

            }else{
                jsonGenerator.write(convertedValue);
            }
        }
    }
}
