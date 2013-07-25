/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.oxm.record;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.json.stream.JsonGenerator;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.oxm.ConversionManager;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;

public class JsonGeneratorRecord extends JsonRecord {

    private Level position;    
    private JsonGenerator jsonGenerator;
    private String rootKeyName;
          
    public JsonGeneratorRecord(JsonGenerator generator, String rootKeyName){
        super();       
        this.jsonGenerator = generator;
        this.rootKeyName = rootKeyName;
    }
    
    @Override
    public void startDocument(String encoding, String version) {      
        if(isRootArray){  
            if(position == null){
                startCollection();
            }                            
            position.setEmptyCollection(false); 
            Level newLevel = new Level(false, position);
            position = newLevel;
            isLastEventStart = true;
        }else{
            Level rootLevel = new Level(false, null);
            rootLevel.setKeyName(rootKeyName);
            position = rootLevel;
            setComplex(position, true);
        }
    }

    @Override
    public void endDocument() {        
        if(position != null){
            jsonGenerator.writeEnd(); 
            position = position.parentLevel;
        }
    }
    
    private void popAndSetInParentBuilder(){
        if(!(position.isCollection && position.isEmptyCollection() && position.getKeyName() == null)){
            jsonGenerator.writeEnd(); 
        }
        position = position.parentLevel;      
    }    
    
    public void startCollection() {
        if(position == null){
             isRootArray = true;              
             Level rootLevel = new Level(true, null); 
             position = rootLevel;
             if(rootKeyName != null){
                jsonGenerator.writeStartArray(rootKeyName);
             }else{
                 jsonGenerator.writeStartArray();
             }
        } else {            
            if(isLastEventStart){
                setComplex(position, true);         
            }            
            Level level = new Level(true, position);            
            position = level;
        }      
        isLastEventStart = false;
    }
    
    @Override
    public void endCollection() {        
         popAndSetInParentBuilder();    
    }

    private void setComplex(Level level, boolean complex){
        boolean isAlreadyComplex = level.isComplex;
        level.setComplex(complex);
        if(complex && !isAlreadyComplex){
            Level parentLevel = level.parentLevel;
            if((parentLevel != null && parentLevel.isCollection && !parentLevel.isEmptyCollection()) || level.keyName == null){
                jsonGenerator.writeStartObject();                    
            }else{
                jsonGenerator.writeStartObject(level.keyName);
            }
        }
    }
    
    @Override    
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        super.openStartElement(xPathFragment, namespaceResolver);
        if(position != null){
            Level newLevel = new Level(false, position);            
            
            if(isLastEventStart){                              
                //this means 2 startevents in a row so the last this is a complex object
                setComplex(position, true);
            }
                      
            String keyName = getKeyName(xPathFragment);
           
            if(position.isCollection && position.isEmptyCollection() ){
                position.setKeyName(keyName);
                jsonGenerator.writeStartArray(keyName);
                position.setEmptyCollection(false);
            }else{
                newLevel.setKeyName(keyName);    
            }
            position = newLevel;   
            isLastEventStart = true;
        }
    }    
    
    /**
     * Handle marshal of an empty collection.  
     * @param xPathFragment
     * @param namespaceResolver
     * @param openGrouping if grouping elements should be marshalled for empty collections
     * @return
     */    
    public boolean emptyCollection(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, boolean openGrouping) {

         if(marshaller.isMarshalEmptyCollections()){
             super.emptyCollection(xPathFragment, namespaceResolver, true);
             
             if (null != xPathFragment) {
                 
                 if(xPathFragment.isSelfFragment() || xPathFragment.nameIsText()){
                     String keyName = position.getKeyName();
                     setComplex(position, false);
                     jsonGenerator.writeStartArray(keyName);
                     jsonGenerator.writeEnd();
                 }else{ 
                     if(isLastEventStart){
                         setComplex(position, true);
                     }                 
                     String keyName =  getKeyName(xPathFragment);
                     if(keyName != null){
                         jsonGenerator.writeStartArray(keyName);
                         jsonGenerator.writeEnd();
                     }
                 }
                 isLastEventStart = false;   
             }
                  
             return true;
         }else{
             return super.emptyCollection(xPathFragment, namespaceResolver, openGrouping);
         }
    } 
    @Override
    public void endElement(XPathFragment xPathFragment,NamespaceResolver namespaceResolver) {
        if(position != null){
            if(isLastEventStart){
                setComplex(position, true);
            }
            if(position.isComplex){
                popAndSetInParentBuilder();
            }else{
                position = position.parentLevel;
            }            
            isLastEventStart = false;          
        }
    }
    
    public void writeValue(Object value, QName schemaType, boolean isAttribute) {
        
        if (characterEscapeHandler != null && value instanceof String) {
            try {
                StringWriter stringWriter = new StringWriter();
                characterEscapeHandler.escape(((String)value).toCharArray(), 0, ((String)value).length(), isAttribute, stringWriter);
                value = stringWriter.toString();
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }
        
        boolean textWrapperOpened = false;                       
        if(!isLastEventStart){
             openStartElement(textWrapperFragment, namespaceResolver);
             textWrapperOpened = true;
        }
      
        Level currentLevel = position;
        String keyName = position.getKeyName();
        if(!position.isComplex){           
            currentLevel = position.parentLevel;         
        }
        addValue(currentLevel, keyName, value, schemaType);
        isLastEventStart = false;
        if(textWrapperOpened){    
             endElement(textWrapperFragment, namespaceResolver);
        }    
    }
    private void addValue(Level currentLevel, String keyName, Object value, QName schemaType){        
        if(currentLevel.isCollection()){
            currentLevel.setEmptyCollection(false);
            addValueToArray(value, schemaType);            
        } else {
            addValueToObject(keyName, value, schemaType);
            
        }
    }
    
    private void addValueToObject(String keyName, Object value, QName schemaType){
        
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
            String convertedValue = ((String) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.STRING, schemaType));
            Class theClass = (Class) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(schemaType);          
            if((schemaType == null || theClass == null) && (CoreClassConstants.NUMBER.isAssignableFrom(value.getClass()))){
                //if it's still a number and falls through the cracks we dont want "" around the value
                    BigDecimal convertedNumberValue = ((BigDecimal) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.BIGDECIMAL, schemaType));
                    jsonGenerator.write(keyName, (BigDecimal)convertedNumberValue);
            }else{
                jsonGenerator.write(keyName, convertedValue);                
            }
                
        }
    }
    
    private void addValueToArray(Object value, QName schemaType){
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
            String convertedValue = ((String) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.STRING, schemaType));
            Class theClass = (Class) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(schemaType);          
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
