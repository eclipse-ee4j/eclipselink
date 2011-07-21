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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.persistence.internal.libraries.antlr.runtime.ANTLRInputStream;
import org.eclipse.persistence.internal.libraries.antlr.runtime.ANTLRReaderStream;
import org.eclipse.persistence.internal.libraries.antlr.runtime.CharStream;
import org.eclipse.persistence.internal.libraries.antlr.runtime.RecognitionException;
import org.eclipse.persistence.internal.libraries.antlr.runtime.TokenRewriteStream;
import org.eclipse.persistence.internal.libraries.antlr.runtime.tree.CommonTree;
import org.eclipse.persistence.internal.libraries.antlr.runtime.tree.Tree;
import org.eclipse.persistence.internal.oxm.record.XMLReaderAdapter;
import org.eclipse.persistence.oxm.XMLConstants;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class JSONReader extends XMLReaderAdapter {

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    private JSONAttributes attributes = new JSONAttributes(); 

    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        try {
            CharStream charStream;
            if(null != input.getByteStream()) {
                charStream = new ANTLRInputStream(input.getByteStream());
            } else {
                charStream = new ANTLRReaderStream(input.getCharacterStream());
            }
            JSONLexer lexer = new JSONLexer(charStream);
            TokenRewriteStream tokens = new TokenRewriteStream(lexer);
            JSONParser parser = new JSONParser(tokens);
            CommonTree commonTree = (CommonTree) parser.object().getTree();
            contentHandler.startDocument();
            parse(commonTree);
            contentHandler.endDocument();
        } catch(RecognitionException e) {
            throw new SAXParseException(e.getLocalizedMessage(), input.getPublicId(), input.getSystemId(), e.line, e.index, e);
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
                contentHandler.startElement("", localName, localName, attributes.setTree(valueTree));
                parse(valueTree);
                contentHandler.endElement("", localName, localName);                
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
            for(int x=0, size=tree.getChildCount(); x<size; x++) {
            	CommonTree nextChildTree = (CommonTree) tree.getChild(x);                
            	contentHandler.startElement("", parentLocalName, parentLocalName, attributes.setTree(nextChildTree));
                parse(nextChildTree);
                contentHandler.endElement("", parentLocalName, parentLocalName);
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

        public JSONAttributes setTree(Tree tree) {
            reset();
            this.tree = tree;              
            return this;
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
                        Tree childValueTree = childTree.getChild(1);
                        switch(childValueTree.getType()) {
                        case JSONLexer.STRING: {
                            String stringValue = childValueTree.getChild(0).getText();
                            attributes.add(new Attribute("", attributeLocalName, attributeLocalName, stringValue.substring(1, stringValue.length() - 1)));
                            break;
                        }
                        case JSONLexer.NUMBER: {
                            attributes.add(new Attribute("", attributeLocalName, attributeLocalName, childValueTree.getChild(0).getText()));
                            break;
                        }
                        case JSONLexer.TRUE: {
                            attributes.add(new Attribute("", attributeLocalName, attributeLocalName, TRUE));
                            break;
                        }
                        case JSONLexer.FALSE: {
                            attributes.add(new Attribute("", attributeLocalName, attributeLocalName, FALSE));
                            break;
                        }
                        case JSONLexer.NULL: {
                            attributes.add(new Attribute("", attributeLocalName, attributeLocalName, ""));
                            break;
                        }
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