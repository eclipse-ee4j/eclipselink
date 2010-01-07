/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.oxm.documentpreservation;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.documentpreservation.AppendNewElementsOrderingPolicy;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.WeakObjectWrapper;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide an implementation of DocumentPreservation Policy that accesses the
 * session cache to store Objects and their associated nodes. 
 * <p><b>Responsibilities:</b><ul>
 * <li>Add new objects and their associated nodes into the Session cache, wrapping them in a weak 
 * reference.
 * <li>Lookup the node for a given Object
 *  
 * @author mmacivor
 * @since TopLink 11g
 */

public class DescriptorLevelDocumentPreservationPolicy extends DocumentPreservationPolicy {
 
    private XMLContext context;
    
    public DescriptorLevelDocumentPreservationPolicy() {
        super();
        this.setNodeOrderingPolicy(new AppendNewElementsOrderingPolicy());        
    }
    
    public void initialize(XMLContext ctx) {
        context = ctx;
    }
    
    public void addObjectToCache(Object obj, Node node) {
        this.addObjectToCache(obj, node, null);
    }
    
    public void addObjectToCache(Object obj, Node node, XMLMapping selfRecordMapping) {
    	// Can't process non-element nodes
    	if (node.getNodeType() != Node.ELEMENT_NODE) {
    		return;
    	}
        AbstractSession session = context.getSession(obj);
        XMLDescriptor xmlDescriptor = (XMLDescriptor)session.getDescriptor(obj);
        DOMRecord row = new DOMRecord((Element)node);
        row.setSession(session);
        Vector pk = xmlDescriptor.getObjectBuilder().extractPrimaryKeyFromRow(row, session);
        if (xmlDescriptor.shouldPreserveDocument() || xmlDescriptor.getPrimaryKeyFieldNames().size() > 0) {
            if ((pk == null) || (pk.size() == 0)) {
                pk = new Vector();
                pk.addElement(new WeakObjectWrapper(obj));
            }
            CacheKey key = session.getIdentityMapAccessorInstance().acquireDeferredLock(pk, xmlDescriptor.getJavaClass(), xmlDescriptor);
            if ((xmlDescriptor).shouldPreserveDocument()) {
                key.setRecord(row);
            }
            key.setObject(obj);
            key.releaseDeferredLock();
        }
    }
    
    public Node getNodeForObject(Object obj) {
        AbstractSession session = context.getSession(obj);
        XMLDescriptor xmlDescriptor = (XMLDescriptor)session.getDescriptor(obj);
        if(xmlDescriptor.shouldPreserveDocument()) {
            Vector pk = xmlDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(obj, session);
            if ((pk == null) || (pk.size() == 0)) {
                pk = new Vector();
                pk.addElement(new WeakObjectWrapper(obj));
            }
            CacheKey cacheKey = session.getIdentityMapAccessorInstance().getCacheKeyForObject(pk, xmlDescriptor.getJavaClass(), xmlDescriptor);
            if(cacheKey != null && cacheKey.getRecord() != null) {
                return ((DOMRecord)cacheKey.getRecord()).getDOM();
            }
        }
        return null;
    }
    
    public Object getObjectForNode(Node node) {
        return getObjectForNode(node, null);
    }  
    public Object getObjectForNode(Node node, XMLMapping selfRecordMapping) {
        return null;
    }
    
    public boolean shouldPreserveDocument() {
        return true;
    }
    
}
