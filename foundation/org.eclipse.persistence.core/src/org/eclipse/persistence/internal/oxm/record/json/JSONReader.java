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
package org.eclipse.persistence.internal.oxm.record.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.libraries.antlr.runtime.ANTLRInputStream;
import org.eclipse.persistence.internal.libraries.antlr.runtime.ANTLRReaderStream;
import org.eclipse.persistence.internal.libraries.antlr.runtime.CharStream;
import org.eclipse.persistence.internal.libraries.antlr.runtime.RecognitionException;
import org.eclipse.persistence.internal.libraries.antlr.runtime.TokenRewriteStream;
import org.eclipse.persistence.internal.libraries.antlr.runtime.TokenStream;
import org.eclipse.persistence.internal.libraries.antlr.runtime.tree.CommonTree;
import org.eclipse.persistence.internal.libraries.antlr.runtime.tree.Tree;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.record.SAXUnmarshallerHandler;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.oxm.record.XMLReaderAdapter;
import org.eclipse.persistence.internal.oxm.record.deferred.DeferredContentHandler;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.oxm.record.XMLRootRecord;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class JSONReader extends XMLReaderAdapter {

    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private Properties properties;
    private String attributePrefix = null;
    private NamespaceResolver namespaces = null;
    private boolean includeRoot;
    private String textWrapper;
    private Class unmarshalClass;

    public JSONReader(String attrPrefix, NamespaceResolver nr, boolean namespaceAware, boolean includeRoot, Character namespaceSeparator, ErrorHandler errorHandler, String textWrapper){
        this(attrPrefix, nr, namespaceAware, includeRoot, namespaceSeparator, errorHandler, textWrapper, null);        
    }

    public JSONReader(String attrPrefix, NamespaceResolver nr, boolean namespaceAware, boolean includeRoot, Character namespaceSeparator, ErrorHandler errorHandler, String textWrapper, Class unmarshalClass){
        this.attributePrefix = attrPrefix;
    	if(attributePrefix == XMLConstants.EMPTY_STRING){
    	    attributePrefix = null;    	    	
    	}
    	namespaces = nr;
    	this.namespaceAware = namespaceAware;
    	if(namespaceSeparator == null){
            this.namespaceSeparator = XMLConstants.DOT;
    	}else{
    	    this.namespaceSeparator = namespaceSeparator;
    	}
    	this.includeRoot = includeRoot;   
    	this.setErrorHandler(errorHandler);
    	this.textWrapper = textWrapper;
    	this.unmarshalClass = unmarshalClass;
    }
    
    private JSONAttributes attributes = new JSONAttributes();

	@Override
    public void parse(InputSource input) throws IOException, SAXException {
        try {
            CharStream charStream;
            InputStream inputStream = null;
            if(null != input.getByteStream()) {
                charStream = new ANTLRInputStream(input.getByteStream());
            } else if (null != input.getCharacterStream()){
                charStream = new ANTLRReaderStream(input.getCharacterStream());
            } else {
                URL url = new URL(input.getSystemId());
                inputStream = url.openStream();
                charStream = new ANTLRInputStream(inputStream);
            }
            JSONLexer lexer = new JSONLexer(charStream);
            TokenRewriteStream tokens = new TokenRewriteStream(lexer);
            ExtendedJSONParser parser = new ExtendedJSONParser(tokens, input, getErrorHandler());                 
            CommonTree commonTree = (CommonTree) parser.message().getTree();
            parseRoot(commonTree);
            if(null != inputStream) {
                inputStream.close();
            }
        } catch(RecognitionException e) {
            SAXParseException saxParseException = new SAXParseException(e.getLocalizedMessage(), input.getPublicId(), input.getSystemId(), e.line, e.index, e);
            getErrorHandler().fatalError(saxParseException);
        } catch(SAXExceptionWrapper e){
    	   throw e.getCause();
        }
       }

    private void parseRoot(Tree tree) throws SAXException {
    	
    	if(namespaces != null){
    		Map <String, String> namespacePairs = namespaces.getPrefixesToNamespaces();
    		Iterator<String> keys = namespacePairs.keySet().iterator();
    		while(keys.hasNext()){
    			String nextKey = keys.next();
    			contentHandler.startPrefixMapping(nextKey, namespacePairs.get(nextKey));	
    		}
    	}
    		    	
    	if(tree.getType() == JSONLexer.OBJECT){
    	    contentHandler.startDocument();
    		int children = tree.getChildCount();
    		if(children == 0 && unmarshalClass == null){
    			return;
    		}
    		if(includeRoot){
    			parse((CommonTree) tree.getChild(0));
    		}else{
    			contentHandler.startElement(XMLConstants.EMPTY_STRING, XMLConstants.EMPTY_STRING, null, attributes.setTree(tree, attributePrefix, namespaces, namespaceSeparator, namespaceAware));
    			for(int x=0, size=children; x<size; x++) {
    	           parse((CommonTree) tree.getChild(x));
    	        }
    			contentHandler.endElement(XMLConstants.EMPTY_STRING,XMLConstants.EMPTY_STRING, null);
    		}
    		contentHandler.endDocument();
        } else if(tree.getType() == JSONLexer.ARRAY){
        	
        	SAXUnmarshallerHandler rootContentHandler = null;  
      	    if(getContentHandler() instanceof SAXUnmarshallerHandler) {
      		  rootContentHandler = (SAXUnmarshallerHandler)getContentHandler();
      	    }
      	
            int size = tree.getChildCount();
            List list = new ArrayList(size);
            for(int x=0; x<size; x++) {
                parseRoot(tree.getChild(x));
                if(getContentHandler() instanceof SAXUnmarshallerHandler) {
                    SAXUnmarshallerHandler saxUnmarshallerHandler = (SAXUnmarshallerHandler) contentHandler;
                    list.add(saxUnmarshallerHandler.getObject());
                    saxUnmarshallerHandler.setObject(null);
                } else if(getContentHandler() instanceof UnmarshalRecord) {
                    UnmarshalRecord unmarshalRecord = (UnmarshalRecord) contentHandler;
                    Object unmarshalledObject = unmarshalRecord.getCurrentObject();
                    if(includeRoot && unmarshalClass != null){
                    	XMLRoot xmlRoot = new XMLRoot();
                    	xmlRoot.setNamespaceURI(unmarshalRecord.getRootElementNamespaceUri());
                    	xmlRoot.setLocalName(unmarshalRecord.getLocalName());
                        xmlRoot.setObject(unmarshalledObject);
                        unmarshalledObject = xmlRoot;
                    }
                    list.add(unmarshalledObject);
                    unmarshalRecord.setCurrentObject(null);
                    unmarshalRecord.setRootElementName(null);
            		unmarshalRecord.setLocalName(null);
                }
            }
            if(getContentHandler() instanceof SAXUnmarshallerHandler) {
                ((SAXUnmarshallerHandler) getContentHandler()).setObject(list);
            } else if(getContentHandler() instanceof UnmarshalRecord) {
                ((UnmarshalRecord) getContentHandler()).setCurrentObject(list);
               	((UnmarshalRecord) getContentHandler()).setRootElementName(XMLConstants.EMPTY_STRING);
               	((UnmarshalRecord) getContentHandler()).setLocalName(XMLConstants.EMPTY_STRING);
                if(rootContentHandler != null){
                	rootContentHandler.setObject(list);
                }
            }
            
        }
    }
    
    private void parse(Tree tree) throws SAXException {
    	if(tree == null){
    		return;
    	}
    	
        switch(tree.getType()) {
        case JSONLexer.PAIR: {
            Tree valueTree = tree.getChild(1);
            if(valueTree.getType() == JSONLexer.ARRAY) {
                parse(valueTree);
            } else {            	
                Tree stringTree = tree.getChild(0);
                String qualifiedName = stringTree.getText().substring(1, stringTree.getText().length() - 1);
                String localName = qualifiedName;
                if(attributePrefix != null && qualifiedName.startsWith(attributePrefix)){
                	break;
                }
                String uri = XMLConstants.EMPTY_STRING;
                if(namespaceAware && namespaces != null){
                	int nsIndex = localName.indexOf(namespaceSeparator);
                	if(nsIndex > -1){
                		String prefix = localName.substring(0, nsIndex);
                		localName = localName.substring(nsIndex + 1);
                		uri = namespaces.resolveNamespacePrefix(prefix);                		
                	}
                	if(localName.equals(XMLConstants.SCHEMA_TYPE_ATTRIBUTE) && uri.equals(XMLConstants.SCHEMA_INSTANCE_URL)){
            			break;
            		}  
                }
                
                if(contentHandler instanceof XMLRootRecord || contentHandler instanceof DeferredContentHandler){
                	//if its not namespaceAware don't report the "type" child as it is will be read by the xsi:type lookup
                	if(!namespaceAware && localName.equals(XMLConstants.SCHEMA_TYPE_ATTRIBUTE)){
                		break;
                    }
                	if(textWrapper != null && textWrapper.equals(localName)){
                    	parse(valueTree);
                    	break;
                    }
                }else if(contentHandler instanceof UnmarshalRecord && ((UnmarshalRecord)contentHandler).getXPathNode() != null){
                	if(!namespaceAware && localName.equals(XMLConstants.SCHEMA_TYPE_ATTRIBUTE) && !((UnmarshalRecord)contentHandler).getXPathNode().hasTypeChild()){
                		break;
                	}
                	boolean isTextValue = isTextValue(localName);
                	if(isTextValue){
                		  parse(valueTree);
             		      break;
                	}
                }
             
                contentHandler.startElement(uri, localName, localName, attributes.setTree(valueTree, attributePrefix, namespaces, namespaceSeparator, namespaceAware));
                parse(valueTree);
                contentHandler.endElement(uri, localName, localName);                
            }
            break;
        }
        case JSONLexer.STRING: {
            String string = string(tree.getChild(0).getText());
            contentHandler.characters(string);
            break;
        }
        case JSONLexer.NUMBER: {
            contentHandler.characters(tree.getChild(0).getText());
            break;
        }
        case JSONLexer.TRUE: {
            contentHandler.characters(TRUE);
            break;
        }
        case JSONLexer.FALSE: {
            contentHandler.characters(FALSE);
            break;
        }
        case JSONLexer.NULL: {
            break;
        }
        case JSONLexer.ARRAY: {
            Tree parentStringTree = tree.getParent().getChild(0);
            String parentLocalName = parentStringTree.getText().substring(1, parentStringTree.getText().length() - 1);
            
            String uri = XMLConstants.EMPTY_STRING;
            if(namespaceAware && namespaces != null){
            	int nsIndex = parentLocalName.indexOf(namespaceSeparator);
            	if(nsIndex > -1){
            		String prefix = parentLocalName.substring(0, nsIndex);
            		parentLocalName = parentLocalName.substring(nsIndex + 1);
            		uri = namespaces.resolveNamespacePrefix(prefix);                		
            	}
            }           
                                            	
        	boolean isTextValue = isTextValue(parentLocalName);           
            
          for(int x=0, size=tree.getChildCount(); x<size; x++) {
        	CommonTree nextChildTree = (CommonTree) tree.getChild(x);
        	if(!isTextValue){
         	   contentHandler.startElement(uri, parentLocalName, parentLocalName, attributes.setTree(nextChildTree, attributePrefix, namespaces, namespaceSeparator, namespaceAware));
         	}
             parse(nextChildTree);
             if(!isTextValue){
                contentHandler.endElement(uri, parentLocalName, parentLocalName);
             }
          }
            
           
            break;
        }
        default: {
            for(int x=0, size=tree.getChildCount(); x<size; x++) {
                parse((CommonTree) tree.getChild(x));
            }
        }
        }
    }
    
    private boolean isTextValue(String localName){
   		XPathNode currentNode = ((UnmarshalRecord)contentHandler).getXPathNode();	
   		if(currentNode == null){
   			return textWrapper != null && textWrapper.equals(localName);
   		}

    	return((currentNode.getNonAttributeChildrenMap() == null 
    			|| currentNode.getNonAttributeChildrenMap().size() ==0 
    			|| (currentNode.getNonAttributeChildrenMap().size() == 1 &&  currentNode.getTextNode() != null))
    			&& textWrapper != null && textWrapper.equals(localName));
    }
    
    private static String string(String string) {
    	string = string.substring(1, string.length()-1);
    	string = string.replace("\r\n", "\n");
    	String returnString = "";                
        
    	int slashIndex = string.indexOf('\\');
             
        if(slashIndex == -1){
            return string;
        }
      
        int position = 0;
        while(slashIndex > -1){              	        	
        	String subString = string.substring(position, slashIndex);
            returnString += subString;
            position = slashIndex;
            
            char nextChar = string.charAt(slashIndex + 1);
            switch (nextChar){
               case 'b':{
            	   position += 2;
                   returnString += '\b';
                   break;
               }
               case 'r':{
            	   position += 2;
                   returnString += '\r';
                   break;
               }
               case 'f':{
            	   position += 2;
                   returnString += '\f';
                   break;
               }
               case 'n':{
            	   position += 2;
                   returnString += '\n';
                   break;
               }
               case 't': {
            	   position += 2;
                   returnString += '\t';
                   break;
               }
               case '"': {
            	    position += 2;
                    returnString += '"';
                    break;
               }
               case '\\':{
            	   position += 2;
                   returnString += '\\';
                   break;
               }
               case '/':{
            	   position += 2;
                   returnString += '/';
                   break;
               }
               case 'u':{
            	   position += 6;
                   String hexValue = string.substring(slashIndex+2, slashIndex+6);
                   returnString += Character.toString((char)Integer.parseInt(hexValue, 16));
                   break;
               }
            }            
            slashIndex = string.indexOf('\\', position);
        }
        //If there is content after the last '\' then append it.
        if(position < string.length()){
        	String subString = string.substring(position, string.length());
        	returnString += subString;
        }
        return returnString;
    }
    
    /**
     * INTERNAL:
     * @since 2.4
     */
    public Object convertValueBasedOnSchemaType(XMLField xmlField, Object value, XMLConversionManager xmlConversionManager, XMLRecord record) {
        if (xmlField.getSchemaType() != null) { 
        	if(XMLConstants.QNAME_QNAME.equals(xmlField.getSchemaType())){
        		String stringValue = (String)value;
        		int indexOpen = stringValue.indexOf('{');
        		int indexClose = stringValue.indexOf('}');
        		String uri = null;    
        		String localName = null;
        		if(indexOpen > -1 && indexClose > -1){
        		    uri = stringValue.substring(indexOpen+1, indexClose);
        		    localName = stringValue.substring(indexClose + 1);
        		}else{
        			localName = stringValue;
        		}
        		if(uri != null){
        			return new QName(uri, localName);
        		}else{
        			return new QName(localName);
        		}
        	}else{
	            Class fieldType = xmlField.getType();
	            if (fieldType == null) {
	                fieldType = xmlField.getJavaClass(xmlField.getSchemaType());
	            }            
	            return xmlConversionManager.convertObject(value, fieldType, xmlField.getSchemaType());
        	}
        }
        return value;
    }

    private static class JSONAttributes extends IndexedAttributeList {

        private Tree tree;
        private String attributePrefix;
        private char namespaceSeparator;
        private NamespaceResolver namespaces;
        private boolean namespaceAware;

        public JSONAttributes setTree(Tree tree, String attributePrefix, NamespaceResolver nr, char namespaceSeparator, boolean namespaceAware) {
            reset();
            this.tree = tree;
            this.attributePrefix = attributePrefix;
            this.namespaces = nr;
            this.namespaceSeparator = namespaceSeparator;
            this.namespaceAware = namespaceAware;
            return this;
        }
               
        private void addSimpleAttribute(List attributes, String uri, String attributeLocalName,Tree childValueTree){
        	 switch(childValueTree.getType()) {
             case JSONLexer.STRING: {                 
                 String stringValue = JSONReader.string(childValueTree.getChild(0).getText());
            	 attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, stringValue));
                 break;
             }
             case JSONLexer.NUMBER: {
                 attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, childValueTree.getChild(0).getText()));
                 break;
             }
             case JSONLexer.TRUE: {
                 attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, TRUE));
                 break;
             }
             case JSONLexer.FALSE: {
                 attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, FALSE));
                 break;
             }
             case JSONLexer.NULL: {
                 attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, XMLConstants.EMPTY_STRING));
                 break;
             } 
        	 }
        }

        public int getIndex(String uri, String localName) {
            if(null == localName) {
                return -1;
            }
            int index = 0;            
            for(Attribute attribute : attributes()) {
            	if(namespaceAware){
	                QName testQName = new QName(uri, localName);
	                if(localName.equals(attribute.getLocalName()) && uri.equals(attribute.getUri())){
	              	  return index;
	              	}
            	}else{
            		if(attribute.getName().equals(localName)) {
	                    return index;
	                }
            	}
                index++;
            }
            return -1;
        }

        @Override
        protected Attribute[] attributes() {
            if(null == attributes) {
                
                if(tree.getType() == JSONLexer.NULL){
                    attributes = new Attribute[1];
                    attributes[0] = new Attribute(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_NIL_ATTRIBUTE, XMLConstants.SCHEMA_NIL_ATTRIBUTE, "true");
                    return attributes;

                }
                if(tree.getType() == JSONLexer.OBJECT) {
                    ArrayList<Attribute> attributesList = new ArrayList<Attribute>(tree.getChildCount());
                    for(int x=0; x<tree.getChildCount(); x++) {
                        Tree childTree = tree.getChild(x);
                        String attributeLocalName = childTree.getChild(0).getText().substring(1, childTree.getChild(0).getText().length() - 1);

                        if(attributePrefix != null){
                            if(attributeLocalName.startsWith(attributePrefix)){
                                attributeLocalName = attributeLocalName.substring(attributePrefix.length());
                            }else{
                                break;
                            }
                        }

                        String uri = XMLConstants.EMPTY_STRING;
                        if(namespaceAware && namespaces != null){
                            int nsIndex = attributeLocalName.indexOf(namespaceSeparator);
                            if(nsIndex > -1){
                                String prefix = attributeLocalName.substring(0, nsIndex);
                                attributeLocalName = attributeLocalName.substring(nsIndex + 1);
                                uri = namespaces.resolveNamespacePrefix(prefix);                        
                            }
                        }

                        Tree childValueTree = childTree.getChild(1);
                        if(childValueTree.getType() == JSONLexer.ARRAY){
                            for(int y=0, size=childValueTree.getChildCount(); y<size; y++) {
                                CommonTree nextChildTree = (CommonTree) childValueTree.getChild(y);
                                addSimpleAttribute(attributesList, uri, attributeLocalName, nextChildTree);
                            }
                        }else{
                            addSimpleAttribute(attributesList, uri, attributeLocalName, childValueTree);
                        }
                    }

                    attributes = attributesList.toArray(new Attribute[attributesList.size()]);
                } else {
                    attributes = NO_ATTRIBUTES;
                }
            }
            return attributes;
        }

    }

    /**
     * JSONParser is a generated class and maybe regenerated.
     * this is a subclass to throw errors that may occur instead of just logging them.
     */
    private static class ExtendedJSONParser extends JSONParser {
       
    	private InputSource inputSource;
    	private ErrorHandler errorHandler;
    	
    	public ExtendedJSONParser(TokenStream input, InputSource inputSource, ErrorHandler errorHandler) {
    		super(input);
    		this.inputSource = inputSource;
    		this.errorHandler = errorHandler;
    	}
    	
    	public void displayRecognitionError(String[] tokenNames,RecognitionException re){    		
    		super.displayRecognitionError(tokenNames, re);
    		SAXParseException saxParseException = new SAXParseException(re.getLocalizedMessage(), inputSource.getPublicId(),inputSource.getSystemId(), re.line, re.index, re);
    		try {
                    errorHandler.fatalError(saxParseException);
                } catch (SAXException e) {				
                    throw new SAXExceptionWrapper(e);
                }    		
    	}    	
    }
    
    //Runtime exception to wrap a SAXException
    private static class SAXExceptionWrapper extends RuntimeException {
    	
    	SAXExceptionWrapper(SAXException e){
    		super(e);
    	}
    	
    	public SAXException getCause() {
            return (SAXException)super.getCause();
        }
   
    }

}