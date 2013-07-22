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
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.core.helper.CoreConversionManager;
import org.eclipse.persistence.internal.oxm.CharacterEscapeHandler;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.ConversionManager;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.ObjectBuilder;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.XMLBinaryDataHelper;
import org.eclipse.persistence.internal.oxm.XMLMarshaller;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.record.ExtendedContentHandler;
import org.eclipse.persistence.internal.oxm.record.XMLFragmentReader;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class JsonObjectBuilderWriterRecord extends MarshalRecord <XMLMarshaller> {

    private Level position;
    private JsonObjectBuilder rootJsonObjectBuilder;
    private JsonArrayBuilder rootJsonArrayBuilder;
    private CharacterEscapeHandler characterEscapeHandler;

    private String attributePrefix;
    private boolean isRootArray;
    private static final String NULL="null";
    private boolean isLastEventStart;
        
    public JsonObjectBuilderWriterRecord(){
        super();
        isLastEventStart = false;
    }
    
    public JsonObjectBuilderWriterRecord(JsonObjectBuilder jsonObjectBuilder){
        this();
        rootJsonObjectBuilder = jsonObjectBuilder;
    }
    
    public JsonObjectBuilderWriterRecord(JsonArrayBuilder jsonArrayBuilder){
        this();
        rootJsonArrayBuilder = jsonArrayBuilder;
    }
    
    /**
     * INTERNAL:
     */
    public void setMarshaller(XMLMarshaller marshaller) {
        super.setMarshaller(marshaller);
        attributePrefix = marshaller.getAttributePrefix();
        if (marshaller.getValueWrapper() != null) {
            textWrapperFragment = new XPathFragment();
            textWrapperFragment.setLocalName(marshaller.getValueWrapper());
        }
        characterEscapeHandler = marshaller.getCharacterEscapeHandler();
    }
    
    @Override
    public void startDocument(String encoding, String version) {      
        if(isRootArray){
            position.setEmptyCollection(false);
            
            Level newLevel = new Level(false, position);
            position = newLevel;
            
            isLastEventStart = true;
        }else{
            Level rootLevel = new Level(false, null);
            position = rootLevel;
            if(rootJsonObjectBuilder == null){
                rootJsonObjectBuilder = Json.createObjectBuilder();
            }  
            
            rootLevel.setJsonObjectBuilder(rootJsonObjectBuilder);
        }
    }

    @Override
    public void endDocument() {
        if(position != null){
            if(position.parentLevel != null && position.parentLevel.isCollection){
                popAndSetInParentBuilder();
            }else{
                //this is the root level list case
                position = position.parentLevel;
            }            
        }
    }
    
    private void popAndSetInParentBuilder(){
        Level removedLevel = position;
        Level parentLevel = position.parentLevel;
        position = position.parentLevel;
        if(removedLevel.isCollection && removedLevel.isEmptyCollection() && removedLevel.keyName == null){
            return;
        }
      
        if(parentLevel != null){                  
            if(parentLevel.isCollection){
                if(removedLevel.isCollection){
                    parentLevel.getJsonArrayBuilder().add(removedLevel.getJsonArrayBuilder());
                }else{                   
                    parentLevel.getJsonArrayBuilder().add(removedLevel.getJsonObjectBuilder());
                }
            }else{
                if(removedLevel.isCollection){                    
                    parentLevel.getJsonObjectBuilder().add(removedLevel.getKeyName(), removedLevel.getJsonArrayBuilder());
                }else{
                    parentLevel.getJsonObjectBuilder().add(removedLevel.getKeyName(), removedLevel.getJsonObjectBuilder());
                }
            }
        }
        
    }    
    
    public void startCollection() {
        if(position == null){
             isRootArray = true;              
             Level rootLevel = new Level(true, null);
             if(rootJsonArrayBuilder == null){
                  rootJsonArrayBuilder = Json.createArrayBuilder();
             }
             rootLevel.setJsonArrayBuilder(rootJsonArrayBuilder);
             position = rootLevel;
        } else {            
            if(isLastEventStart){
                position.setComplex(true);           
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
    
    @Override    
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        super.openStartElement(xPathFragment, namespaceResolver);
        if(position != null){
            Level newLevel = new Level(false, position);            
            
            if(isLastEventStart){ 
                //this means 2 startevents in a row so the last this is a complex object
                position.setComplex(true);                                
            }
                      
            String keyName = getKeyName(xPathFragment);
           
            if(position.isCollection && position.isEmptyCollection() ){
                position.setKeyName(keyName);
            }else{
                newLevel.setKeyName(keyName);    
            }
            position = newLevel;   
            isLastEventStart = true;
        }
    }
    
    protected String getKeyName(XPathFragment xPathFragment){
        String keyName = xPathFragment.getLocalName(); 
       
        if(isNamespaceAware()){
            if(xPathFragment.getNamespaceURI() != null){
                String prefix = null;
                if(getNamespaceResolver() !=null){
                    prefix = getNamespaceResolver().resolveNamespaceURI(xPathFragment.getNamespaceURI());
                } else if(namespaceResolver != null){
                    prefix = namespaceResolver.resolveNamespaceURI(xPathFragment.getNamespaceURI());
                }
                if(prefix != null && !prefix.equals(Constants.EMPTY_STRING)){
                    keyName = prefix + getNamespaceSeparator() +  keyName;                           
                }
            }
        } 
        if(xPathFragment.isAttribute() && attributePrefix != null){
            keyName = attributePrefix + keyName;
        }

        return keyName;
    }

    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver,  Object value, QName schemaType){
        if(xPathFragment.getNamespaceURI() != null && xPathFragment.getNamespaceURI() == javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI){
            return;
        }
        xPathFragment.setAttribute(true);
        openStartElement(xPathFragment, namespaceResolver);
        characters(schemaType, value, null, false, true);
        endElement(xPathFragment, namespaceResolver);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void marshalWithoutRootElement(ObjectBuilder treeObjectBuilder, Object object, Descriptor descriptor, Root root, boolean isXMLRoot){
        if(treeObjectBuilder != null){
            addXsiTypeAndClassIndicatorIfRequired(descriptor, null, descriptor.getDefaultRootElementField(), root, object, isXMLRoot, true);
            treeObjectBuilder.marshalAttributes(this, object, session);
        }         
     }
    
    /**
     * INTERNAL:
     * The character used to separate the prefix and uri portions when namespaces are present 
     * @since 2.4
     */
    public char getNamespaceSeparator(){        
        return marshaller.getNamespaceSeparator();
    }
    
    /**
     * INTERNAL:
     * The optional fragment used to wrap the text() mappings
     * @since 2.4
     */
    public XPathFragment getTextWrapperFragment() {
        return textWrapperFragment;
    }
    
    @Override
    public boolean isWrapperAsCollectionName() {
        return marshaller.isWrapperAsCollectionName();
    }
    
    @Override
    public void element(XPathFragment frag) {
        isLastEventStart = false;
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
                     position.setComplex(false);
                     position.parentLevel.getJsonObjectBuilder().add(keyName, Json.createArrayBuilder());                     
                 }else{ 
                     if(isLastEventStart){
                         position.setComplex(true);                  
                     }                 
                     String keyName =  getKeyName(xPathFragment);
                     if(keyName != null){
                        position.getJsonObjectBuilder().add(keyName, Json.createArrayBuilder());
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
    public void attribute(XPathFragment xPathFragment,NamespaceResolver namespaceResolver, String value) {
        attribute(xPathFragment, namespaceResolver, value, null);
    }

    @Override
    public void attribute(String namespaceURI, String localName, String qName, String value) {
        XPathFragment xPathFragment = new XPathFragment();
        xPathFragment.setNamespaceURI(namespaceURI);
        xPathFragment.setAttribute(true);
        xPathFragment.setLocalName(localName);

        openStartElement(xPathFragment, namespaceResolver);
        characters(null, value, null, false, true);

        endElement(xPathFragment, namespaceResolver);
        
    }

    @Override
    public void closeStartElement() {}

    @Override
    public void endElement(XPathFragment xPathFragment,NamespaceResolver namespaceResolver) {
        if(position != null){
            if(isLastEventStart){
                position.setComplex(true);
            }
            if(position.isComplex){
                popAndSetInParentBuilder();
            }else{
                position = position.parentLevel;
            }            
            isLastEventStart = false;          
        }
    }

    @Override
    public void characters(String value) {
        writeValue(value, null, false);
    }

    @Override
    public void characters(QName schemaType, Object value, String mimeType, boolean isCDATA){          
        characters(schemaType, value, mimeType, isCDATA, false);
     }
    
    public void characters(QName schemaType, Object value, String mimeType, boolean isCDATA, boolean isAttribute){
        if(mimeType != null) {
            if(value instanceof List){
               value = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesListForBinaryValues((List)value, marshaller, mimeType);
           }else{

            value = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(value, marshaller, mimeType).getData();
           }
        }         
        if(schemaType != null && Constants.QNAME_QNAME.equals(schemaType)){
            String convertedValue = getStringForQName((QName)value);
            writeValue(convertedValue, null, isAttribute);
        } 
        else if(value.getClass() == String.class){          
            //if schemaType is set and it's a numeric or boolean type don't treat as a string
            if(schemaType != null && isNumericOrBooleanType(schemaType)){
                Class theClass = (Class) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(schemaType);
                Object convertedValue = ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, theClass, schemaType);
                writeValue(convertedValue, schemaType, isAttribute);
            }else if(isCDATA){
                cdata((String)value);
            }else{
                writeValue((String)value, null, isAttribute);                
            }
       }else{
           Class theClass = (Class) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(schemaType);          

           if(schemaType == null || theClass == null){
               if(value.getClass() == CoreClassConstants.BOOLEAN || CoreClassConstants.NUMBER.isAssignableFrom(value.getClass())){
                   writeValue(value, schemaType, isAttribute);
               }else{
                   String convertedValue = ((String) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.STRING, schemaType));
                   writeValue(convertedValue, schemaType, isAttribute);
               }
           }else if(schemaType != null && !isNumericOrBooleanType(schemaType)){
               //if schemaType exists and is not boolean or number do write quotes (convert to string)
               String convertedValue = ((String) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.STRING, schemaType));
               writeValue(convertedValue, schemaType, isAttribute);
           } else if(isCDATA){
               String convertedValue = ((String) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.STRING, schemaType));
               cdata(convertedValue);
           }else{
               writeValue(value, schemaType, isAttribute);           
           }
       }        
    }
    
    
    private boolean isNumericOrBooleanType(QName schemaType){
        if(schemaType == null){
            return false;
        }else if(schemaType.equals(Constants.BOOLEAN_QNAME)
                || schemaType.equals(Constants.INTEGER_QNAME)
                || schemaType.equals(Constants.INT_QNAME)
                || schemaType.equals(Constants.BYTE_QNAME)
                || schemaType.equals(Constants.DECIMAL_QNAME)
                || schemaType.equals(Constants.FLOAT_QNAME)
                || schemaType.equals(Constants.DOUBLE_QNAME)
                || schemaType.equals(Constants.SHORT_QNAME)
                || schemaType.equals(Constants.LONG_QNAME)
                || schemaType.equals(Constants.NEGATIVE_INTEGER_QNAME)
                || schemaType.equals(Constants.NON_NEGATIVE_INTEGER_QNAME)
                || schemaType.equals(Constants.NON_POSITIVE_INTEGER_QNAME)
                || schemaType.equals(Constants.POSITIVE_INTEGER_QNAME)
                || schemaType.equals(Constants.UNSIGNED_BYTE_QNAME)
                || schemaType.equals(Constants.UNSIGNED_INT_QNAME)
                || schemaType.equals(Constants.UNSIGNED_LONG_QNAME)
                || schemaType.equals(Constants.UNSIGNED_SHORT_QNAME)
        ){
            return true;
        }
        return false;
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
            currentLevel.setComplex(true);          
        }
        if(currentLevel.isCollection()){
            currentLevel.setEmptyCollection(false);
            addValueToArrayBuilder(currentLevel.getJsonArrayBuilder(), value, schemaType);            
        } else {
            JsonObjectBuilder builder = currentLevel.getJsonObjectBuilder();                   
            addValueToObjectBuilder(builder, keyName, value, schemaType);          
        }
        isLastEventStart = false;
        if(textWrapperOpened){    
             endElement(textWrapperFragment, namespaceResolver);
        }    
    }
    
    private void addValueToObjectBuilder(JsonObjectBuilder jsonObjectBuilder, String keyName, Object value, QName schemaType){
        if(value == NULL){
            jsonObjectBuilder.addNull(keyName);
        }else if(value instanceof Integer){
            jsonObjectBuilder.add(keyName, (Integer)value);  
        }else if(value instanceof BigDecimal){
            jsonObjectBuilder.add(keyName, (BigDecimal)value);   
        }else if(value instanceof BigInteger){
            jsonObjectBuilder.add(keyName, (BigInteger)value);               
        }else if(value instanceof Boolean){
            jsonObjectBuilder.add(keyName, (Boolean)value);
        }else if(value instanceof Character){
            jsonObjectBuilder.add(keyName, (Character)value);  
        }else if(value instanceof Double){
            jsonObjectBuilder.add(keyName, (Double)value);
        }else if(value instanceof Float){
            jsonObjectBuilder.add(keyName, (Float)value);
        }else if(value instanceof Long){
            jsonObjectBuilder.add(keyName, (Long)value);
        }else if(value instanceof String){
            jsonObjectBuilder.add(keyName, (String)value);                
        }else{
            String convertedValue = ((String) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.STRING, schemaType));
            Class theClass = (Class) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(schemaType);          
            if((schemaType == null || theClass == null) && (CoreClassConstants.NUMBER.isAssignableFrom(value.getClass()))){
                //if it's still a number and falls through the cracks we dont want "" around the value
                    BigDecimal convertedNumberValue = ((BigDecimal) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.BIGDECIMAL, schemaType));
                    jsonObjectBuilder.add(keyName, (BigDecimal)convertedNumberValue);
            }else{
                jsonObjectBuilder.add(keyName, convertedValue);
            }
                
        }
    }
    
    private void addValueToArrayBuilder(JsonArrayBuilder jsonArrayBuilder, Object value, QName schemaType){
        if(value == NULL){
            jsonArrayBuilder.addNull();
        }else if(value instanceof Integer){
            jsonArrayBuilder.add((Integer)value);  
        }else if(value instanceof BigDecimal){
            jsonArrayBuilder.add((BigDecimal)value);   
        }else if(value instanceof BigInteger){
            jsonArrayBuilder.add((BigInteger)value);               
        }else if(value instanceof Boolean){                
            jsonArrayBuilder.add((Boolean)value);
        }else if(value instanceof Character){
            jsonArrayBuilder.add((Character)value);  
        }else if(value instanceof Double){
            jsonArrayBuilder.add((Double)value);
        }else if(value instanceof Float){
            jsonArrayBuilder.add((Float)value);
        }else if(value instanceof Long){
            jsonArrayBuilder.add((Long)value);
        }else if(value instanceof String){
            jsonArrayBuilder.add((String)value);
        }else{
            String convertedValue = ((String) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.STRING, schemaType));
            Class theClass = (Class) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(schemaType);          
            if((schemaType == null || theClass == null) && (CoreClassConstants.NUMBER.isAssignableFrom(value.getClass()))){
                //if it's still a number and falls through the cracks we dont want "" around the value
                    BigDecimal convertedNumberValue = ((BigDecimal) ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, CoreClassConstants.BIGDECIMAL, schemaType));
                    jsonArrayBuilder.add((BigDecimal)convertedNumberValue);
            }else{
                jsonArrayBuilder.add(convertedValue);
            }
        }
    }
    
    @Override
    public void cdata(String value) {
        characters(value);        
    }

    @Override
    public void node(Node node, NamespaceResolver resolver, String uri, String name) {
       
        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            Attr attr = (Attr) node;
            String resolverPfx = null;
            if (getNamespaceResolver() != null) {
                resolverPfx = this.getNamespaceResolver().resolveNamespaceURI(attr.getNamespaceURI());
            }
            String namespaceURI = attr.getNamespaceURI();
            // If the namespace resolver contains a prefix for the attribute's URI,
            // use it instead of what is set on the attribute
            if (resolverPfx != null) {
                attribute(attr.getNamespaceURI(), Constants.EMPTY_STRING, resolverPfx+Constants.COLON+attr.getLocalName(), attr.getNodeValue());
            } else {
                attribute(attr.getNamespaceURI(), Constants.EMPTY_STRING, attr.getName(), attr.getNodeValue());
                // May need to declare the URI locally
                if (attr.getNamespaceURI() != null) {
                    attribute(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, Constants.EMPTY_STRING, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON + attr.getPrefix(), attr.getNamespaceURI());
                    this.getNamespaceResolver().put(attr.getPrefix(), attr.getNamespaceURI());
                }
            }
        } else if (node.getNodeType() == Node.TEXT_NODE) {
            writeValue(node.getNodeValue(), null, false);
        } else {
            try {
                JsonObjectBuilderRecordContentHandler wrcHandler = new JsonObjectBuilderRecordContentHandler();
                
                XMLFragmentReader xfragReader = new XMLFragmentReader(namespaceResolver);
                xfragReader.setContentHandler(wrcHandler);
                xfragReader.setProperty("http://xml.org/sax/properties/lexical-handler", wrcHandler);
                xfragReader.parse(node, uri, name);
            } catch (SAXException sex) {
                throw XMLMarshalException.marshalException(sex);
            }
        }
        
    }        
    
    protected String getStringForQName(QName qName){
        if(null == qName) {
            return null;
        }
        CoreConversionManager xmlConversionManager = getSession().getDatasourcePlatform().getConversionManager();

        return (String) xmlConversionManager.convertObject(qName, String.class);       
    }

    /**
     * INTERNAL:
     */
     public void namespaceDeclarations(NamespaceResolver namespaceResolver) {
     }

     public void namespaceDeclaration(String prefix, String namespaceURI){
     }
     
     public void defaultNamespaceDeclaration(String defaultNamespace){
     }
     
    /**
     * INTERNAL:
     */
     public void nilComplex(XPathFragment xPathFragment, NamespaceResolver namespaceResolver){
         XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
         closeStartGroupingElements(groupingFragment);
         openStartElement(xPathFragment, namespaceResolver);
         characters(NULL);
         endElement(xPathFragment, namespaceResolver);
     }

    /**
     * INTERNAL:
     */
     public void nilSimple(NamespaceResolver namespaceResolver){
         XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);         
         characters(NULL);        
         closeStartGroupingElements(groupingFragment);
     }

     /**
      * Used when an empty simple value should be written
      * @since EclipseLink 2.4
      */
     public void emptySimple(NamespaceResolver namespaceResolver){
         nilSimple(namespaceResolver);
     }
     
     public void emptyAttribute(XPathFragment xPathFragment,NamespaceResolver namespaceResolver){
         XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
         openStartElement(xPathFragment, namespaceResolver);
         characters(NULL);
         endElement(xPathFragment, namespaceResolver);
         closeStartGroupingElements(groupingFragment);
     }

     /**
      * Used when an empty complex item should be written
      * @since EclipseLink 2.4
      */
     public void emptyComplex(XPathFragment xPathFragment, NamespaceResolver namespaceResolver){
         XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
         closeStartGroupingElements(groupingFragment);
         openStartElement(xPathFragment, namespaceResolver);
         endElement(xPathFragment, namespaceResolver);
     }
    
    
     
     /**
      * This class will typically be used in conjunction with an XMLFragmentReader.
      * The XMLFragmentReader will walk a given XMLFragment node and report events
      * to this class - the event's data is then written to the enclosing class'
      * writer.
      *
      * @see org.eclipse.persistence.internal.oxm.record.XMLFragmentReader
      */
     protected class JsonObjectBuilderRecordContentHandler implements ExtendedContentHandler, LexicalHandler {

         JsonObjectBuilderRecordContentHandler() {
         }

         // --------------------- CONTENTHANDLER METHODS --------------------- //
         public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
                 XPathFragment xPathFragment = new XPathFragment(localName);
                 xPathFragment.setNamespaceURI(namespaceURI);
                 openStartElement(xPathFragment, namespaceResolver);
                 handleAttributes(atts);
         }

         public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
             XPathFragment xPathFragment = new XPathFragment(localName);
             xPathFragment.setNamespaceURI(namespaceURI);
             
             JsonObjectBuilderWriterRecord.this.endElement(xPathFragment, namespaceResolver);        
         }

         public void startPrefixMapping(String prefix, String uri) throws SAXException {
         }

         public void characters(char[] ch, int start, int length) throws SAXException {
             String characters = new String (ch, start, length);
             characters(characters);
         }

         public void characters(CharSequence characters) throws SAXException {           
             JsonObjectBuilderWriterRecord.this.characters(characters.toString());      
         }

         // --------------------- LEXICALHANDLER METHODS --------------------- //
         public void comment(char[] ch, int start, int length) throws SAXException {
         }

         public void startCDATA() throws SAXException {
         }

         public void endCDATA() throws SAXException {
         }

         // --------------------- CONVENIENCE METHODS --------------------- //
            protected void handleAttributes(Attributes atts) {
             for (int i=0, attsLength = atts.getLength(); i<attsLength; i++) {
                 String qName = atts.getQName(i);
                 if((qName != null && (qName.startsWith(javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON) || qName.equals(javax.xml.XMLConstants.XMLNS_ATTRIBUTE)))) {
                     continue;
                 }
                 attribute(atts.getURI(i), atts.getLocalName(i), qName, atts.getValue(i));
             }
         }

         protected void writeComment(char[] chars, int start, int length) {        
         }
        
         protected void writeCharacters(char[] chars, int start, int length) {
             try {
                 characters(chars, start, length);
             } catch (SAXException e) {
                 throw XMLMarshalException.marshalException(e);
             }           
         }
         // --------------- SATISFY CONTENTHANDLER INTERFACE --------------- //
         public void endPrefixMapping(String prefix) throws SAXException {}
         public void processingInstruction(String target, String data) throws SAXException {}
         public void setDocumentLocator(Locator locator) {}
         public void startDocument() throws SAXException {}
         public void endDocument() throws SAXException {}
         public void skippedEntity(String name) throws SAXException {}
         public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}

         // --------------- SATISFY LEXICALHANDLER INTERFACE --------------- //
         public void startEntity(String name) throws SAXException {}
         public void endEntity(String name) throws SAXException {}
         public void startDTD(String name, String publicId, String systemId) throws SAXException {}
         public void endDTD() throws SAXException {}
         @Override
         public void setNil(boolean isNil) {}

     }

     
     /**
     * Instances of this class are used to maintain state about the current
     * level of the JSON message being marshalled.
     */
    protected static class Level {
        
        private boolean isCollection;
        private boolean emptyCollection;
        private String keyName;        
        private JsonObjectBuilder jsonObjectBuilder;
        private JsonArrayBuilder jsonArrayBuilder;
        private boolean isComplex;
        private Level parentLevel;
        
        public Level(boolean isCollection, Level parentLevel) {
            setCollection(isCollection);
            emptyCollection = true;
            this.parentLevel = parentLevel;
        }

        public boolean isCollection() {
            return isCollection;
        }

        public void setCollection(boolean isCollection) {
            this.isCollection = isCollection;
            if(isCollection && jsonArrayBuilder == null){
                jsonArrayBuilder = Json.createArrayBuilder();
            }
        }

        public String getKeyName() {
            return keyName;
        }

        public void setKeyName(String keyName) {
            this.keyName = keyName;
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

        public boolean isEmptyCollection() {
            return emptyCollection;
        }

        public void setEmptyCollection(boolean emptyCollection) {
            this.emptyCollection = emptyCollection;
        }
        public boolean isComplex() {
            return isComplex;
        }

        public void setComplex(boolean isComplex) {
            this.isComplex = isComplex;
            if(isComplex && jsonObjectBuilder == null){
                jsonObjectBuilder = Json.createObjectBuilder();
            }
        }        
    }

}
