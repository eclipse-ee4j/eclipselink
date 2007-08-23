/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.xml.queries;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.queries.MetadataQueryHint;
import org.eclipse.persistence.internal.jpa.metadata.queries.MetadataNamedQuery;

import org.eclipse.persistence.internal.jpa.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.xml.XMLConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Object to hold onto a named query metadata that came from XML.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLNamedQuery extends MetadataNamedQuery {
    /**
     * INTERNAL:
     */
    public XMLNamedQuery(Node node, XMLHelper helper) {
        // Set the location where we found this query.
        setLocation(helper.getDocumentName());
        
        // Process the name.
        setName(helper.getNodeValue(node, XMLConstants.ATT_NAME));
        
        // Process the query string.
        setEJBQLString(helper.getNodeTextValue(node, XMLConstants.QUERY));
            
        // Process the query hints.
        NodeList hints = helper.getNodes(node, XMLConstants.QUERY_HINT);
        if (hints != null) {
            for (int i = 0; i < hints.getLength(); i++) {
                Node hintNode = hints.item(i);
                String name = helper.getNodeValue(hintNode, XMLConstants.ATT_NAME);
                String value = helper.getNodeValue(hintNode, XMLConstants.ATT_VALUE);
                addHint(new MetadataQueryHint(name, value));
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    public String getIgnoreLogMessageContext() {
        return MetadataLogger.IGNORE_NAMED_QUERY_ELEMENT;
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromAnnotations() {
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return true;
    }
}
