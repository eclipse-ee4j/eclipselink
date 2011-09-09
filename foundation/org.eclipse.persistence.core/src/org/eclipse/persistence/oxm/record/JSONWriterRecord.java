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
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.record.ExtendedContentHandler;
import org.eclipse.persistence.internal.oxm.record.XMLFragmentReader;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * <p>Use this type of MarshalRecord when the marshal target is a Writer and the
 * JSON should be not be formatted with carriage returns and indenting.</p>
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
    protected NamespaceResolver namespaces;
    protected String namespaceSeperator = ".";
    protected boolean namespaceAware;
    
    /**
     * INTERNAL:
     */
    public void setMarshaller(XMLMarshaller marshaller) {
        super.setMarshaller(marshaller);
        attributePrefix = marshaller.getAttributePrefix();
        namespaces = marshaller.getNamespaceResolver();
        namespaceAware = marshaller.isNamespaceAware();
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

    /**
     * INTERNAL:
     */
    public void startDocument(String encoding, String version) {
        try {
             if(levels.isEmpty()) {            	
                 levels.push(new Level(true));                
             } 
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
            Level position = null;
            if(levels.isEmpty()) {
                levels.push(new Level(true));
            } else {
                position = levels.peek();
                levels.push(new Level(true));
                if(position.isFirst()) {
                    position.setFirst(false);
                } else {
                    writer.write(',');
                    writer.write(' ');
                }
            }
            if(position == null || !position.isCollection() || position.isEmptyCollection()){

               super.openStartElement(xPathFragment, namespaceResolver);
                isStartElementOpen = true;
                writer.write('"');   
                if(xPathFragment.isAttribute() && attributePrefix != null){
                	writer.write(attributePrefix);
                }
                           
                if(namespaceAware){
                    if(xPathFragment.getNamespaceURI() != null){
                        String prefix = null;
                    	if(namespaces !=null){
                	        prefix = namespaces.resolveNamespaceURI(xPathFragment.getNamespaceURI());
                    	} else if(namespaceResolver != null){
                	    	prefix = namespaceResolver.resolveNamespaceURI(xPathFragment.getNamespaceURI());
                	    }
                    	if(prefix != null && !prefix.equals(XMLConstants.EMPTY_STRING)){
                    		writer.write(prefix);
                    		writer.write(namespaceSeperator);
                    	}
                    }
                }
                
                writer.write(xPathFragment.getLocalName());
                writer.write("\" : ");
                
                if((xPathFragment.getNextFragment() == null || xPathFragment.getNextFragment().nameIsText()) && position != null && position.isCollection()) {
                   writer.write('[');
                }
                if(position !=null && position.isEmptyCollection()){
                	position.setEmptyCollection(false);
                }
            }
           
           if(addOpenBrace){
        	  XPathFragment next = xPathFragment.getNextFragment();
        	  if(!(xPathFragment.isAttribute() || xPathFragment.nameIsText() || (next != null && next.nameIsText()))){              
            	    writer.write('{');
              }
           }

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
    	attribute(namespaceURI, localName, qName, value, true);
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
    
    private void attribute(String namespaceURI, String localName, String qName, String value, boolean wrapInQuotes) {
    	 if(namespaceURI != null && namespaceURI == XMLConstants.XMLNS_URL){
    		 return;
    	 }
        try {        	
            Level position = null;
            if(!levels.isEmpty()){
                position = levels.peek();
                if(position.isFirst()) {
                    position.setFirst(false);
                } else {
                    writer.write(',');
                    writer.write(' ');
                }        		
            }        	
        	
            writer.write('"');
            if(attributePrefix != null){
            	writer.write(attributePrefix);	
            }
            writer.write(localName);
            writer.write('"');				
            writer.write(':');
            if(position != null && position.isCollection()) {
                writer.write('[');
            }

            if(wrapInQuotes){
                writeStringValueCharacters(value);
            }else{
                characters(value);		    	
            }
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
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
        if(!levels.isEmpty()) {
            levels.pop();
        }
        try {
             if(addCloseBrace){
                //if(!(xPathFragment.getHasText() || xPathFragment.isAttribute())) {
            	  XPathFragment next = xPathFragment.getNextFragment();
            	  if(!(xPathFragment.isAttribute() || xPathFragment.nameIsText() || (next != null && next.nameIsText()))){
                
                    writer.write('}');
                }             
            }
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    @Override
    public void startCollection() {
        levels.peek().setCollection(true);
        levels.peek().setEmptyCollection(true);
    }

    @Override
    public void endCollection() {
        try {
            writer.write(']');
            if(!levels.isEmpty()) {
                levels.peek().setCollection(false);
            }
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
     public void characters(String value) {
           writeValue(value);
     }

     public void attribute(XPathFragment xPathFragment, NamespaceResolver namespaceResolver,  Object value, QName schemaType){
    	 if(xPathFragment.getNamespaceURI() != null && xPathFragment.getNamespaceURI() == XMLConstants.XMLNS_URL){
    		 return;
    	 }
    	 xPathFragment.setAttribute(true);
    	 xPathFragment.setAttribute(true);
         openStartElement(xPathFragment, namespaceResolver);
         characters(schemaType, value, false);
      	 endElement(xPathFragment, namespaceResolver);
     }     
     
     
     
     
     public void characters(QName schemaType, Object value, boolean isCDATA){      
    	 
         if(schemaType != null && XMLConstants.QNAME_QNAME.equals(schemaType)){
             String convertedValue = getStringForQName((QName)value);
             writeStringValueCharacters((String)value);
         } else if(value.getClass() == String.class){
             //if schemaType is set and it's a numeric or boolean type don't treat as a string
             if(schemaType != null && isNumericOrBooleanType(schemaType)){
                 String convertedValue = ((String) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, ClassConstants.STRING, schemaType));
                 characters(convertedValue);
             }else if(isCDATA){
                 cdata((String)value);        	    
             }else{
 	             writeStringValueCharacters((String)value);
 	         }
        }else{
            String convertedValue = ((String) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(value, ClassConstants.STRING, schemaType));
            //if schemaType exists and is not boolean or number do write quotes
            if(schemaType != null && !isNumericOrBooleanType(schemaType)){
                writeStringValueCharacters(convertedValue);
            } else if(isCDATA){
                cdata(convertedValue);        	    
            }else{
                characters(convertedValue);
            }
        }   
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
    	 characters(NULL);
    	 endElement(xPathFragment, namespaceResolver, false);
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
      * INTERNAL:
      */
     protected void writeStringValueCharacters(String value){
        try {   
            writer.write('"');
            characters(value);
            writer.write('"');
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
     }
   
    /**
     * INTERNAL:
     */
    public void cdata(String value) {
        try {
            if(isStartElementOpen) {
                isStartElementOpen = false;
                writer.write('>');
            }
            writer.write("<![CDATA[");
            writer.write(value);
            writer.write("]]>");
        } catch(IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
    }

    /**
     * INTERNAL:
     */
    protected void writeValue(String value) {
        try {
            if(value.indexOf('"') > -1)  {
                  char[] chars = value.toCharArray();
                  for (int x = 0, charsSize = chars.length; x < charsSize; x++) {
                      char character = chars[x];
                      if('"' == character) {
                          writer.write("\\\"");
                      } else {
                          writer.write(character);
                      }
                  }
            } else {
                writer.write(value);
            }
        } catch (IOException e) {
            throw XMLMarshalException.marshalException(e);
        }
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
            characters(node.getNodeValue());
        } else {
            try {
                WriterRecordContentHandler wrcHandler = new WriterRecordContentHandler();
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
    protected class WriterRecordContentHandler implements ExtendedContentHandler, LexicalHandler {
        Map<String, String> prefixMappings;

        WriterRecordContentHandler() {
            prefixMappings = new HashMap<String, String>();
        }

        // --------------------- CONTENTHANDLER METHODS --------------------- //
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            try {
                if (isStartElementOpen) {
                    writer.write('>');
                }

                writer.write('<');
                writer.write(qName);
                isStartElementOpen = true;
                // Handle attributes
                handleAttributes(atts);
                // Handle prefix mappings
                writePrefixMappings();
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            try {
                if (isStartElementOpen) {
                    writer.write('/');
                    writer.write('>');
                } else {
                        writer.write('<');
                        writer.write('/');
                        writer.write(qName);
                        writer.write('>');
                }
                isStartElementOpen = false;
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            String namespaceUri = getNamespaceResolver().resolveNamespacePrefix(prefix);
            if(namespaceUri == null || !namespaceUri.equals(uri)) {
                prefixMappings.put(prefix, uri);
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            String characters = new String (ch, start, length);
            characters(characters);
        }

        public void characters(CharSequence characters) throws SAXException {
            if (isProcessingCData) {
                cdata(characters.toString());
                return;
            }

            if (isStartElementOpen) {
                try {
                    writer.write('>');
                    isStartElementOpen = false;
                } catch (IOException e) {
                    throw XMLMarshalException.marshalException(e);
                }
            }
            writeValue(characters.toString());
        }

        // --------------------- LEXICALHANDLER METHODS --------------------- //
        public void comment(char[] ch, int start, int length) throws SAXException {
            try {
                if (isStartElementOpen) {
                    writer.write('>');
                    isStartElementOpen = false;
                }
                writeComment(ch, start, length);
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        public void startCDATA() throws SAXException {
            isProcessingCData = true;
        }

        public void endCDATA() throws SAXException {
            isProcessingCData = false;
        }

        // --------------------- CONVENIENCE METHODS --------------------- //
        protected void writePrefixMappings() {
            try {
                if (!prefixMappings.isEmpty()) {
                    for (java.util.Iterator<String> keys = prefixMappings.keySet().iterator(); keys.hasNext();) {
                        String prefix = keys.next();
                        writer.write(' ');
                        writer.write(XMLConstants.XMLNS);
                        if(null != prefix && prefix.length() > 0) {
                            writer.write(XMLConstants.COLON);
                            writer.write(prefix);
                        }
                        writer.write('=');
                        writer.write('"');
                        String uri = prefixMappings.get(prefix);
                        if(null != uri) {
                            writer.write(prefixMappings.get(prefix));
                        }
                        writer.write('"');
                    }
                    prefixMappings.clear();
                }
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

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
            try {
                writer.write('<');
                writer.write('!');
                writer.write('-');
                writer.write('-');
                for (int x = start; x < length; x++) {
                    writer.write(chars[x]);
                }
                writer.write('-');
                writer.write('-');
                writer.write('>');
            } catch (IOException e) {
                throw XMLMarshalException.marshalException(e);
            }
        }

        protected void writeCharacters(char[] chars, int start, int length) {
            try {
                for (int x = start; x < length; x++) {
                    writer.write(chars[x]);
                }
            } catch (IOException e) {
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
      
		public boolean isEmptyCollection() {
			return emptyCollection;
		}

		public void setEmptyCollection(boolean emptyCollection) {
			this.emptyCollection = emptyCollection;
		}

		public Level(boolean value) {
            this.first = value;
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