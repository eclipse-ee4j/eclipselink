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
*     bdoughan - June 25/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.record.namespaces;

import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamReader;

import org.eclipse.persistence.internal.oxm.Constants;

/**
 *  An UnmarshalNamespaceResolver that delegates all work to a NamespaceContext.
 *  This is useful when using XML input from sources such as StAX.
 */
public class UnmarshalNamespaceContext implements UnmarshalNamespaceResolver {

    private XMLStreamReader xmlStreamReader;
    private Set<String> prefixes;

    public UnmarshalNamespaceContext() {        
        this.prefixes = new HashSet(4);
    }
    
    public UnmarshalNamespaceContext(XMLStreamReader anXMLStreamReader) {
        this.xmlStreamReader = anXMLStreamReader;
        this.prefixes = new HashSet(4);
    }

    public String getNamespaceURI(String prefix) {
        if(null == prefix) {
            prefix = Constants.EMPTY_STRING;
        }
        try {
            String namespaceURI = xmlStreamReader.getNamespaceURI(prefix);
            if(null == namespaceURI) {
                return xmlStreamReader.getAttributeValue(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix);
            }
            return namespaceURI;
        } catch(IllegalStateException e) {
            return null;
        }
    }

    public String getPrefix(String namespaceURI) {
        return xmlStreamReader.getNamespaceContext().getPrefix(namespaceURI);
    }

    /**
     * The underlying NamespaceContext is responsible for maintaining the 
     * appropriate prefix/URI associations.
     */
    public void push(String prefix, String namespaceURI) {
        prefixes.add(prefix);
    }

    /**
     * The underlying NamespaceContext is responsible for maintaining the 
     * appropriate prefix/URI associations.
     */
    public void pop(String prefix) {
        if(null!= getNamespaceURI(prefix)) {
            prefixes.remove(prefix);
        }
    }
    
    public Set<String> getPrefixes() {
        return prefixes;
    }

    public XMLStreamReader getXmlStreamReader() {
        return xmlStreamReader;
    }

    public void setXmlStreamReader(XMLStreamReader xmlStreamReader) {
        this.xmlStreamReader = xmlStreamReader;
    }

}
