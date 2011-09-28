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
import java.util.Collections;
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
import org.eclipse.persistence.internal.libraries.antlr.runtime.tree.CommonTree;
import org.eclipse.persistence.internal.libraries.antlr.runtime.tree.Tree;
import org.eclipse.persistence.internal.oxm.record.XMLReaderAdapter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class JSONReader extends XMLReaderAdapter {

    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private Properties properties;
    private String attributePrefix = null;
    private NamespaceResolver namespaces = null;
    public JSONReader(String attrPrefix, NamespaceResolver nr, boolean namespaceAware){
        this(attrPrefix, nr, namespaceAware, XMLConstants.DOT);        
    }
    
    public JSONReader(String attrPrefix, NamespaceResolver nr, boolean namespaceAware, char namespaceSeparator){
        this.attributePrefix = attrPrefix;
    	if(attributePrefix == XMLConstants.EMPTY_STRING){
    	    attributePrefix = null;    	    	
    	}
    	namespaces = nr;
    	this.namespaceAware = namespaceAware;
    	this.namespaceSeparator = namespaceSeparator;
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
            JSONParser parser = new JSONParser(tokens);
            CommonTree commonTree = (CommonTree) parser.object().getTree();
            contentHandler.startDocument();
            parseRoot(commonTree);
            contentHandler.endDocument();
            if(null != inputStream) {
                inputStream.close();
            }
        } catch(RecognitionException e) {
            throw new SAXParseException(e.getLocalizedMessage(), input.getPublicId(), input.getSystemId(), e.line, e.index, e);
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
    		int children = tree.getChildCount();
    		if(children == 1){
    			parse((CommonTree) tree.getChild(0));
    		}else{
    			contentHandler.startElement(XMLConstants.EMPTY_STRING, XMLConstants.EMPTY_STRING, null, attributes.setTree(tree, attributePrefix, namespaces, namespaceSeparator, namespaceAware));
    			for(int x=0, size=tree.getChildCount(); x<size; x++) {
    	           parse((CommonTree) tree.getChild(x));
    	        }
    			contentHandler.endElement(XMLConstants.EMPTY_STRING,XMLConstants.EMPTY_STRING, null);
    		}
    	}
    }
    
    private void parse(Tree tree) throws SAXException {
        switch(tree.getType()) {
        case JSONLexer.PAIR: {
            Tree valueTree = tree.getChild(1);
            if(valueTree.getType() == JSONLexer.ARRAY) {
                parse(valueTree);
            } else {
                Tree stringTree = tree.getChild(0);
                String localName = stringTree.getText().substring(1, stringTree.getText().length() - 1);
                
                if(attributePrefix != null && localName.startsWith(attributePrefix)){
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
            
            for(int x=0, size=tree.getChildCount(); x<size; x++) {
            	CommonTree nextChildTree = (CommonTree) tree.getChild(x);                
            	contentHandler.startElement(uri, parentLocalName, parentLocalName, attributes.setTree(nextChildTree, attributePrefix, namespaces, namespaceSeparator, namespaceAware));
                parse(nextChildTree);
                contentHandler.endElement(uri, parentLocalName, parentLocalName);
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

    private String string(String string) {
        string = string.substring(1, string.length() - 1);
        string = string.replace("\\\"", "\"");
        return string;
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
                 String stringValue = childValueTree.getChild(0).getText();
                 attributes.add(new Attribute(uri, attributeLocalName, attributeLocalName, stringValue.substring(1, stringValue.length() - 1)));
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
	                if(attribute.getQName().equals(testQName)) {
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
        protected List<Attribute> attributes() {
            if(null == attributes) {            	
                
                if(tree.getType() == JSONLexer.NULL){
                	attributes = new ArrayList<Attribute>(1);
                    attributes.add(new Attribute(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_NIL_ATTRIBUTE, XMLConstants.SCHEMA_NIL_ATTRIBUTE, "true"));
                	return attributes;

                }                
                if(tree.getType() == JSONLexer.OBJECT) {
                    attributes = new ArrayList<Attribute>(tree.getChildCount());
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
                            	addSimpleAttribute(attributes, uri, attributeLocalName, nextChildTree);
                        	}
                        }else{
                            addSimpleAttribute(attributes, uri, attributeLocalName, childValueTree);
                        }
                    }
                    
                } else {
                    attributes = Collections.EMPTY_LIST;
                }
            }
            return attributes;
        }

    }

}