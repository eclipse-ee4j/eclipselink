/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.internal.oxm.record.deferred;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.oxm.record.ExtendedContentHandler;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * <p><b>Purpose</b>: ContentHandler to store events until we know if we are dealing with a simple, complex or empty element.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Store events until will know if the element is simple, complex or empty
 * <li> Return control to the original unmarshalRecord
 * </ul>
 */
public abstract class DeferredContentHandler implements ExtendedContentHandler, LexicalHandler {
    private int levelIndex;
    private List<SAXEvent> events;
    private UnmarshalRecord parent;
    private boolean startOccurred;
    private boolean charactersOccurred;
    private boolean attributesOccurred;

    public DeferredContentHandler(UnmarshalRecord parentRecord) {
        levelIndex = 0;
        events = new ArrayList<SAXEvent>();
        this.parent = parentRecord;
    }


    @Override
    public void setNil(boolean isNil) {}
    
    protected abstract void processEmptyElement() throws SAXException;

    protected abstract void processComplexElement() throws SAXException;

    protected abstract void processSimpleElement() throws SAXException;

    protected void processEmptyElementWithAttributes() throws SAXException {
        processEmptyElement();
    }

    protected void executeEvents(UnmarshalRecord unmarshalRecord) throws SAXException {
        for (int i = 0; i < events.size(); i++) {
            SAXEvent nextEvent = events.get(i);
            nextEvent.processEvent(unmarshalRecord);
        }
        XMLReader parentXMLReader = parent.getXMLReader();
        if (parentXMLReader.getContentHandler().equals(this)) {
            parentXMLReader.setContentHandler(unmarshalRecord);
            parentXMLReader.setLexicalHandler(unmarshalRecord);
        }
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        StartPrefixMappingEvent event = new StartPrefixMappingEvent(prefix, uri);
        events.add(event);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        EndPrefixMappingEvent event = new EndPrefixMappingEvent(prefix);
        events.add(event);
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        levelIndex++;
        
        //Copy attributes because some parsers reuse the Attributes object across start element events
        Attributes copiedAttrs = buildAttributeList(atts);
        
        StartElementEvent event = new StartElementEvent(uri, localName, qName, copiedAttrs);
        events.add(event);        
        
        if (startOccurred) {
            //we know it's complex and non-null
            processComplexElement();
            return;
        }
        
        startOccurred = true;
    }

    protected AttributeList buildAttributeList(Attributes attrs) throws SAXException {
        int attrsLength = attrs.getLength();
        AttributeList attributes = new AttributeList(attrsLength); 
        for (int i = 0; i < attrsLength; i++) {
            String qName = attrs.getQName(i);
            String uri = attrs.getURI(i);
            attributes.addAttribute(attrs.getLocalName(i), qName, uri, attrs.getType(i), attrs.getValue(i), i);
            if(!javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(uri) && (null != qName && !qName.startsWith(javax.xml.XMLConstants.XMLNS_ATTRIBUTE))) {
                attributesOccurred = true;
            }
        }
        return attributes;
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        levelIndex--;
        EndElementEvent event = new EndElementEvent(uri, localName, qName);
        events.add(event);

        if (charactersOccurred) {
            //we know it is a simple element
            processSimpleElement();
        } else if(startOccurred){
            //we know it is an empty element
            if(attributesOccurred) {
                processEmptyElementWithAttributes();
            } else {
                processEmptyElement();
            }
        }

        if ((levelIndex == 0) && (parent != null)) {
            XMLReader xmlReader = parent.getXMLReader();
            xmlReader.setContentHandler(parent);
            xmlReader.setLexicalHandler(parent);
        }
    }

    public void setDocumentLocator(Locator locator) {
        DocumentLocatorEvent event = new DocumentLocatorEvent(locator);
        events.add(event);
    }

    public void startDocument() throws SAXException {
        StartDocumentEvent event = new StartDocumentEvent();
        events.add(event);
    }

    public void endDocument() throws SAXException {
        EndDocumentEvent event = new EndDocumentEvent();
        events.add(event);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        charactersOccurred = true;
        CharactersEvent event = new CharactersEvent(ch, start, length);
        events.add(event);
    }

    public void characters(CharSequence characters) {
        charactersOccurred = true;
        CharactersEvent event = new CharactersEvent(characters);
        events.add(event);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        IgnorableWhitespaceEvent event = new IgnorableWhitespaceEvent(ch, start, length);
        events.add(event);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        ProcessingInstructionEvent event = new ProcessingInstructionEvent(target, data);
        events.add(event);
    }

    public void skippedEntity(String name) throws SAXException {
        SkippedEntityEvent event = new SkippedEntityEvent(name);
        events.add(event);
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        StartDTDEvent event = new StartDTDEvent(name, publicId, systemId);
        events.add(event);
    }

    public void endDTD() throws SAXException {
        EndDTDEvent event = new EndDTDEvent();
        events.add(event);
    }

    public void startEntity(String name) throws SAXException {
        StartEntityEvent event = new StartEntityEvent(name);
        events.add(event);
    }

    public void endEntity(String name) throws SAXException {
        EndEntityEvent event = new EndEntityEvent(name);
        events.add(event);
    }

    public void startCDATA() throws SAXException {
        StartCDATAEvent event = new StartCDATAEvent();
        events.add(event);
    }

    public void endCDATA() throws SAXException {
        EndCDATAEvent event = new EndCDATAEvent();
        events.add(event);
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        CommentEvent event = new CommentEvent(ch, start, length);
        events.add(event);
    }

    protected UnmarshalRecord getParent() {
        return parent;
    }

    protected List getEvents() {
        return events;
    }
    
    /**
     * Implementation of Attributes - used to pass along a given node's attributes
     * to the startElement method of the reader's content handler.
     */
    public class AttributeList implements org.xml.sax.Attributes {
      
      private String[] localNames;      
      private String[] uris;
      private String[] values;
      private String[] types;   	
      private ArrayList<String> qNames;    	
      
        public AttributeList(int size) {
          qNames = new ArrayList(size);
          localNames = new String[size];        	
          uris = new String[size];
          types= new String[size];
          values = new String[size];        	           
        }
        
        public void addAttribute(String localName, String qName, String uri, String type, String value, int index) {        	
          qNames.add(index, qName);
          localNames[index] = localName;          
          uris[index] = uri;
          types[index] = type;
          values[index] = value;          
        }
                    
        public String getQName(int index) {
            return qNames.get(index);            
        }
        
        public String getType(String namespaceUri, String localName) {

       	 for(int i=0;i <localNames.length; i++){
           	 String nextLocalName = localNames[i];
           	 if(nextLocalName != null && localName!= null && localName.equals(nextLocalName)){           		 
               String uriAtIndex = uris[i];
               if(uriAtIndex == null) {
                   uriAtIndex="";
               }
               if(uriAtIndex.equals(namespaceUri)){
                  return types[i];           			 
           		 }
           	 }        	 
            }             
           return null;
        	
        }
        
        public String getType(int index) {                               
          return types[index];            
        }
        
        public String getType(String qname) {        	
          return types[getIndex(qname)];    
        }
        
        public int getIndex(String qname) {
        	return qNames.indexOf(qname);        	          	
        }
        
        public int getIndex(String uri, String localName) {
    
        	 for(int i=0;i <localNames.length; i++){
            	 String nextLocalName = localNames[i];
            	 if(nextLocalName != null && localName!= null && localName.equals(nextLocalName)){
            		 String uriAtIndex = uris[i];
            		 if(uriAtIndex == null) {
            		     uriAtIndex="";
            		 }
            		 if(uriAtIndex.equals(uri)){
            			 return i;
            		 }
            	 }        	 
             }             
            return -1;
        }
       
        public int getLength() {
            return localNames.length;
        }
        
        public String getLocalName(int index) {
            return localNames[index];          	  
        }        

        public String getURI(int index) {
            return uris[index];                	
        }
        
        public String getValue(int index) {
       	    return values[index];            
        }
        
        public String getValue(String qname) {
          return values[getIndex(qname)];        	
        }

        public String getValue(String uri, String localName) {
         for(int i=0;i <localNames.length; i++){
        	 String nextLocalName = localNames[i];
        	 if(nextLocalName != null && localName!= null && localName.equals(nextLocalName)){
        		 String uriAtIndex = uris[i];
        		 //handle null/empty namespace case
                 if(uriAtIndex == null) {
                     uriAtIndex="";
                 }
                 if(uriAtIndex.equals(uri)){
        			 return values[i];
        		 }
        	 }        	 
         }
         return null;      	
        }
        
    }
}
