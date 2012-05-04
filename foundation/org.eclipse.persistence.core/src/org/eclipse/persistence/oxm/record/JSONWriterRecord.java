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
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.oxm.record;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.TreeObjectBuilder;
import org.eclipse.persistence.internal.oxm.XMLBinaryDataHelper;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.ExtendedContentHandler;
import org.eclipse.persistence.internal.oxm.record.XMLFragmentReader;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * <p>Use this type of MarshalRecord when the marshal target is a Writer and the
 * JSON should not be formatted with carriage returns or indenting.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * JSONRecord jsonWriterRecord = new JSONWriterRecord();<br>
 * jsonWriterRecord.setWriter(myWriter);<br>
 * xmlMarshaller.marshal(myObject, jsonWriterRecord);<br>
 * </code></p>
 * <p>If the marshal(Writer) and setMediaType(MediaType.APPLICATION_JSON) and
 * setFormattedOutput(false) method is called on XMLMarshaller, then the Writer
 * is automatically wrapped in a JSONWriterRecord.</p>
 * <p><code>
 * XMLContext xmlContext = new XMLContext("session-name");<br>
 * XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();<br>
 * xmlMarshaller.setMediaType(MediaType.APPLICATION_JSON);
 * xmlMarshaller xmlMarshaller.setFormattedOutput(false);<br>
 * xmlMarshaller.marshal(myObject, myWriter);<br>
 * </code></p>
 * @see org.eclipse.persistence.oxm.XMLMarshaller
 */
public class JSONWriterRecord extends MarshalRecord {

    protected Writer writer;
    protected boolean isStartElementOpen = false;
    protected boolean isProcessingCData = false;
    protected Stack<Level> levels = new Stack<Level>();
    protected static final String NULL="null";
    protected String attributePrefix;
    protected boolean charactersAllowed = false;
    protected CharsetEncoder encoder;
    protected String space;

    public JSONWriterRecord(){
        super();
        space = XMLConstants.EMPTY_STRING;
    }

    /**
     * INTERNAL:
     */
    public void setMarshaller(XMLMarshaller marshaller) {
        super.setMarshaller(marshaller);
        attributePrefix = marshaller.getAttributePrefix();
        encoder = Charset.forName(marshaller.getEncoding()).newEncoder();
    }

    /**
     * Return the Writer that the object will be marshalled to.
     * @return The marshal target.
     */
    public Writer getWriter() {
        return writer;
    }

    /**
     * Set the Writer that the object will be marshalled to.
     * @param writer The marshal target.
     */
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public void namespaceDeclaration(String prefix, String namespaceURI){
    }
    
    public void defaultNamespaceDeclaration(String defaultNamespace){
    }
    
    /**
     * INTERNAL:
     */
    public void startDocument(String encoding, String version) {
        try {
             if(!levels.isEmpty()) {
                 Level level = levels.peek();
                 if(level.isFirst()) {
                     level.setFirst(false);
                 } else {
                     writer.write(",");
                     writer.write(space);
                 }
             }
             levels.push(new Level(true, false));
             writer.write('{');
         } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void endDocument() {
        try {
            writer.write('}');
            levels.pop();
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        openStartElement(xPathFragment, namespaceResolver, true);
    }

    /**
     * INTERNAL:
     */
    private void openStartElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, boolean addOpenBrace) {    	
        try {
        	Level newLevel = null;
            Level position = null;
            if(levels.isEmpty()) {
            	newLevel = new Level(true, true);
                levels.push(newLevel);
            } else {
                position = levels.peek();
                newLevel = new Level(true, true);
                levels.push(newLevel);
                if(position.isFirst()) {
                    position.setFirst(false);
                } else {
                    writer.write(',');                    
                }
            }
            
            if(xPathFragment.nameIsText()){
                if(position != null && position.isCollection() && position.isEmptyCollection()) {
                	if(!charactersAllowed){
                		 throw JAXBException.jsonValuePropertyRequired("[");   
                	}
                    writer.write('[');
                    position.setEmptyCollection(false);
                    position.setNeedToOpenComplex(false);
                    charactersAllowed = true;
                    return;
                }
            }
            
            if(position == null || !position.isCollection() || position.isEmptyCollection()){
            	   if(position !=null && position.needToOpenComplex){
                       writer.write('{');
                       position.needToOpenComplex = false;
                       position.needToCloseComplex = true;
                   }
                   writeKey(xPathFragment);
             
                if(position !=null && position.isEmptyCollection()){
               	    XPathFragment nextFragment =xPathFragment.getNextFragment();
                	
                    if(nextFragment !=null && !nextFragment.nameIsText()&& !nextFragment.isAttribute()){ 
               		newLevel.setEmptyCollection(true);
                    	newLevel.setCollection(true);
                    	position.setEmptyCollection(false);
                   }else{
                		writer.write('[');
                		position.setEmptyCollection(false);
                	}
                }
             }
            charactersAllowed = true;
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void element(XPathFragment frag) {
        try {
            if (isStartElementOpen) {
                writer.write('>');
                isStartElementOpen = false;
            }
            writer.write('<');
            writer.write(frag.getShortName());
            writer.write('/');
            writer.write('>');
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    public void attribute(String namespaceURI, String localName, String qName, String value) {
        XPathFragment xPathFragment = new XPathFragment();
        xPathFragment.setNamespaceURI(namespaceURI);
        xPathFragment.setAttribute(true);
        xPathFragment.setLocalName(localName);

        openStartElement(xPathFragment, namespaceResolver);
        characters(null, value, null, false);
        endElement(xPathFragment, namespaceResolver);
    }

    /**
     * INTERNAL:
     */
    public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, String value) {
        attribute(xPathFragment, namespaceResolver, value, null);
    }

    /**
     * INTERNAL:
     * override so we don't iterate over namespaces when startPrefixMapping doesn't do anything
     */
    public void startPrefixMappings(NamespaceResolver namespaceResolver) {
    }

    /**
     * INTERNAL:
     * override so we don't iterate over namespaces when endPrefixMapping doesn't do anything
     */
    public void endPrefixMappings(NamespaceResolver namespaceResolver) {
    }

    /**
     * INTERNAL:
     */
    public void closeStartElement() {}

	/**
     * INTERNAL:
     */
    public void endElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver) {
        endElement(xPathFragment, namespaceResolver, true);
    }
    /**
     * INTERNAL:
     */
    private void endElement(XPathFragment xPathFragment, NamespaceResolver namespaceResolver, boolean addCloseBrace) {
        try{
            if(!levels.isEmpty()) {
                Level position = levels.pop();
                if(position.needToOpenComplex){
                    writer.write('{');
                    writer.write('}');
                } else if(position.needToCloseComplex){
                    closeComplex();
                }
                charactersAllowed = false;
            }
        } catch (IOException e) {
             throw XMLMarshalException.marshalException(e);
        }
    }

    protected void closeComplex() throws IOException {
        writer.write('}');
    }

    @Override
    public void startCollection() {
        if(levels.isEmpty()) {
            try {
                writer.write('[');
                levels.push(new Level(true, false));
            } catch(IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        } else {
            levels.peek().setCollection(true);
            levels.peek().setEmptyCollection(true);
        }
    }

    @Override
    public void endCollection() {
        try {
            if(levels.size() == 1) {
                writer.write(']');
            } else {
                Level position = levels.peek();
                if(position != null && position.isCollection() && !position.isEmptyCollection()) {
                    writer.write(']');
                }
            }
            levels.peek().setCollection(false);
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
     public void characters(String value) {
    	  boolean textWrapperOpened = false;
          if(!charactersAllowed){    
        	   if(textWrapperFragment != null){
        		   openStartElement(textWrapperFragment, namespaceResolver);
        		   textWrapperOpened = true;
        	   }
    	   }
    	 
           Level position = levels.peek();
           position.setNeedToOpenComplex(false);
           try {
               writer.write('"');
               writeValue(value);
               writer.write('"');
           } catch (IOException e) {
               throw XMLMarshalException.marshalException(e);
           }        
           if(textWrapperOpened){    
          	   if(textWrapperFragment != null){
               	   endElement(textWrapperFragment, namespaceResolver);
          	   }
      	   }           
     }

     public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver,  Object value, QName schemaType){
         if(xPathFragment.getNamespaceURI() != null && xPathFragment.getNamespaceURI() == XMLConstants.XMLNS_URL){
             return;
         }
         xPathFragment.setAttribute(true);
         openStartElement(xPathFragment, namespaceResolver);
         characters(schemaType, value, null, false);
         endElement(xPathFragment, namespaceResolver);
     }

     public void characters(QName schemaType, Object value, String mimeType, boolean isCDATA){    	     	 
    	
         Level position = levels.peek();
         position.setNeedToOpenComplex(false);
         if(mimeType != null) {
             value = XMLBinaryDataHelper.getXMLBinaryDataHelper().getBytesForBinaryValue(//
                     value, marshaller, mimeType).getData();
         }
         if(schemaType != null && XMLConstants.QNAME_QNAME.equals(schemaType)){
             String convertedValue = getStringForQName((QName)value);
             characters((String)convertedValue);
         } else if(value.getClass() == String.class){
             //if schemaType is set and it's a numeric or boolean type don't treat as a string
             if(schemaType != null && isNumericOrBooleanType(schemaType)){
                 String convertedValue = ((String) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, ClassConstants.STRING, schemaType));
                 nonStringCharacters(convertedValue);
             }else if(isCDATA){
                 cdata((String)value);
             }else{
                 characters((String)value);
             }
        }else{
            String convertedValue = ((String) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, ClassConstants.STRING, schemaType));
            if(schemaType == null){
                if(value.getClass() == ClassConstants.BOOLEAN || ClassConstants.NUMBER.isAssignableFrom(value.getClass())){
                    nonStringCharacters(convertedValue);
                }else{
                    characters(convertedValue);

                }
            }else if(schemaType != null && !isNumericOrBooleanType(schemaType)){
                //if schemaType exists and is not boolean or number do write quotes
                characters(convertedValue);
            } else if(isCDATA){
                cdata(convertedValue);
            }else{
                nonStringCharacters(convertedValue);
            }
        }
         charactersAllowed = false;
       
     }

     
     private boolean isNumericOrBooleanType(QName schemaType){
         if(schemaType == null){
             return false;
         }else if(schemaType.equals(XMLConstants.BOOLEAN_QNAME)
                 || schemaType.equals(XMLConstants.INTEGER_QNAME)
                 || schemaType.equals(XMLConstants.INT_QNAME)
                 || schemaType.equals(XMLConstants.DECIMAL_QNAME)
                 || schemaType.equals(XMLConstants.FLOAT_QNAME)
                 || schemaType.equals(XMLConstants.DOUBLE_QNAME)
                 || schemaType.equals(XMLConstants.SHORT_QNAME)
         ){
             return true;
         }
         return false;
     }

    /**
     * INTERNAL:
     */
     public void namespaceDeclarations(NamespaceResolver namespaceResolver) {
     }

    /**
     * INTERNAL:
     */
     public void nilComplex(XPathFragment xPathFragment, NamespaceResolver namespaceResolver){
         XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
         closeStartGroupingElements(groupingFragment);
         openStartElement(xPathFragment, namespaceResolver, false);
         nonStringCharacters(NULL);
         endElement(xPathFragment, namespaceResolver, false);
     }

    /**
     * INTERNAL:
     */
     public void nilSimple(NamespaceResolver namespaceResolver){
         XPathFragment groupingFragment = openStartGroupingElements(namespaceResolver);
         nonStringCharacters(NULL);
         closeStartGroupingElements(groupingFragment);
     }

     /**
      * Used when an empty simple value should be written
      * @since EclipseLink 2.4
      */
     public void emptySimple(NamespaceResolver namespaceResolver){
         nilSimple(namespaceResolver);
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

     public void marshalWithoutRootElement(TreeObjectBuilder treeObjectBuilder, Object object, XMLDescriptor descriptor, XMLRoot root, boolean isXMLRoot){
    	 if(treeObjectBuilder != null){
             treeObjectBuilder.addXsiTypeAndClassIndicatorIfRequired(this, descriptor, null, descriptor.getDefaultRootElementField(), root, object, isXMLRoot, true);
             treeObjectBuilder.marshalAttributes(this, object, session);
    	 }         
      }
      

     protected void nonStringCharacters(String value){
    	boolean textWrapperOpened = false;
        if(!charactersAllowed){    
           if(textWrapperFragment != null){
        	   openStartElement(textWrapperFragment, namespaceResolver);
        	   textWrapperOpened = true;
           }
    	}
    	 
          
        Level position = levels.peek();
        position.setNeedToOpenComplex(false);
        try {
            writer.write(value);
         } catch (IOException e) {
             throw XMLMarshalException.marshalException(e);
         }
        if(textWrapperOpened){    
           if(textWrapperFragment != null){
           	   endElement(textWrapperFragment, namespaceResolver);
           }
    	}          
      }

    /**
     * INTERNAL:
     */
    public void cdata(String value) {
        characters(value);
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

    protected void writeKey(XPathFragment xPathFragment) throws IOException {
        super.openStartElement(xPathFragment, namespaceResolver);
        isStartElementOpen = true;
        writer.write('"');
        if(xPathFragment.isAttribute() && attributePrefix != null){
            writer.write(attributePrefix);
        }

        if(isNamespaceAware()){
            if(xPathFragment.getNamespaceURI() != null){
                String prefix = null;
                if(getNamespaceResolver() !=null){
                    prefix = getNamespaceResolver().resolveNamespaceURI(xPathFragment.getNamespaceURI());
                } else if(namespaceResolver != null){
                    prefix = namespaceResolver.resolveNamespaceURI(xPathFragment.getNamespaceURI());
                }
                if(prefix != null && !prefix.equals(XMLConstants.EMPTY_STRING)){
                    writer.write(prefix);
                    writer.write(getNamespaceSeparator());
                }
            }
        }

        writer.write(xPathFragment.getLocalName());
        writer.write("\"");
        writer.write(space);
        writer.write(XMLConstants.COLON);
        writer.write(space);        
    }

    /**
     * INTERNAL:
     */
    protected void writeValue(String value) {
        try {
              char[] chars = value.toCharArray();
              for (int x = 0, charsSize = chars.length; x < charsSize; x++) {
                  char character = chars[x];
                  switch (character){
                      case '"' : {
                          writer.write("\\\"");
                          break;
                      }
                      case '\b': {
                          writer.write("\\");
                          writer.write("b");
                          break;
                      }
                      case '\f': {
                          writer.write("\\");
                          writer.write("f");
                          break;
                      }
                      case '\n': {
                          writer.write("\\");
                          writer.write("n");
                          break;
                      }
                      case '\r': {
                          writer.write("\\");
                          writer.write("r");
                          break;
                      }
                      case '\t': {
                          writer.write("\\");
                          writer.write("t");
                          break;
                      }
                      case '\\': {
                          writer.write("\\");
                          writer.write("\\");
                          break;
                      }
                      default: {
                          if(Character.isISOControl(character) || !encoder.canEncode(character)){
                              writer.write("\\u");
                              String hex = Integer.toHexString(character).toUpperCase();
                              for(int i=hex.length(); i<4; i++){
                                  writer.write("0");
                              }
                              writer.write(hex);
                          }else{
                              writer.write(character);
                          }
                      }
                  }
              }
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }
    
    protected String getStringForQName(QName qName){
        if(null == qName) {
            return null;
        }
        XMLConversionManager xmlConversionManager = (XMLConversionManager) getSession().getDatasourcePlatform().getConversionManager();

        return (String) xmlConversionManager.convertObject(qName, String.class);       
    }

    /**
     * Receive notification of a node.
     * @param node The Node to be added to the document
     * @param namespaceResolver The NamespaceResolver can be used to resolve the
     * namespace URI/prefix of the node
     */
    public void node(Node node, NamespaceResolver namespaceResolver) {
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
                attribute(attr.getNamespaceURI(), XMLConstants.EMPTY_STRING, resolverPfx+XMLConstants.COLON+attr.getLocalName(), attr.getNodeValue());
            } else {
                attribute(attr.getNamespaceURI(), XMLConstants.EMPTY_STRING, attr.getName(), attr.getNodeValue());
                // May need to declare the URI locally
                if (attr.getNamespaceURI() != null) {
                    attribute(XMLConstants.XMLNS_URL, XMLConstants.EMPTY_STRING,XMLConstants.XMLNS + XMLConstants.COLON + attr.getPrefix(), attr.getNamespaceURI());
                    this.getNamespaceResolver().put(attr.getPrefix(), attr.getNamespaceURI());
                }
            }
        } else if (node.getNodeType() == Node.TEXT_NODE) {
            nonStringCharacters(node.getNodeValue());
        } else {
            try {
            	JSONWriterRecordContentHandler wrcHandler = new JSONWriterRecordContentHandler();
            	
                XMLFragmentReader xfragReader = new XMLFragmentReader(namespaceResolver);
                xfragReader.setContentHandler(wrcHandler);
                xfragReader.setProperty("http://xml.org/sax/properties/lexical-handler", wrcHandler);
                xfragReader.parse(node);
            } catch (SAXException sex) {
                throw XMLMarshalException.marshalException(sex);
            }
        }
    }

    /**
     * This class will typically be used in conjunction with an XMLFragmentReader.
     * The XMLFragmentReader will walk a given XMLFragment node and report events
     * to this class - the event's data is then written to the enclosing class'
     * writer.
     *
     * @see org.eclipse.persistence.internal.oxm.record.XMLFragmentReader
     */
    protected class JSONWriterRecordContentHandler implements ExtendedContentHandler, LexicalHandler {

    	JSONWriterRecordContentHandler() {
        }

        // --------------------- CONTENTHANDLER METHODS --------------------- //
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            	XPathFragment xPathFragment = new XPathFragment(localName);
            	xPathFragment.setNamespaceURI(namespaceURI);
            	openStartElement(xPathFragment, namespaceResolver);
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        	XPathFragment xPathFragment = new XPathFragment(localName);
        	xPathFragment.setNamespaceURI(namespaceURI);
        	
        	JSONWriterRecord.this.endElement(xPathFragment, namespaceResolver);        
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            String characters = new String (ch, start, length);
            characters(characters);
        }

        public void characters(CharSequence characters) throws SAXException {        	
        	JSONWriterRecord.this.characters(characters.toString());      
        }

        // --------------------- LEXICALHANDLER METHODS --------------------- //
        public void comment(char[] ch, int start, int length) throws SAXException {
        }

        public void startCDATA() throws SAXException {
            isProcessingCData = true;
        }

        public void endCDATA() throws SAXException {
            isProcessingCData = false;
        }

        // --------------------- CONVENIENCE METHODS --------------------- //
           protected void handleAttributes(Attributes atts) {
            for (int i=0, attsLength = atts.getLength(); i<attsLength; i++) {
                String qName = atts.getQName(i);
                if((qName != null && (qName.startsWith(XMLConstants.XMLNS + XMLConstants.COLON) || qName.equals(XMLConstants.XMLNS)))) {
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

    }

    /**
     * Instances of this class are used to maintain state about the current
     * level of the JSON message being marshalled.
     */
    protected static class Level {

        private boolean first;
        private boolean collection;
        private boolean emptyCollection;
        private boolean needToOpenComplex;
        private boolean needToCloseComplex;

        public Level(boolean value, boolean needToOpen) {
            this.first = value;
            needToOpenComplex = needToOpen;
        }

        public boolean isNeedToOpenComplex() {
            return needToOpenComplex;
        }

        public void setNeedToOpenComplex(boolean needToOpenComplex) {
            this.needToOpenComplex = needToOpenComplex;
        }

        public boolean isNeedToCloseComplex() {
            return needToCloseComplex;
        }

        public void setNeedToCloseComplex(boolean needToCloseComplex) {
            this.needToCloseComplex = needToCloseComplex;
        }

        public boolean isEmptyCollection() {
            return emptyCollection;
        }

        public void setEmptyCollection(boolean emptyCollection) {
            this.emptyCollection = emptyCollection;
        }

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean value) {
            this.first = value;
        }

        public boolean isCollection() {
            return collection;
        }

        public void setCollection(boolean collection) {
            this.collection = collection;
        }

    }

}