/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.persistence.internal.oxm.XMLObjectBuilder;
import org.eclipse.persistence.internal.oxm.documentpreservation.XMLBinderPolicy;
import org.eclipse.persistence.internal.oxm.record.DOMReader;
import org.eclipse.persistence.internal.oxm.record.SAXUnmarshaller;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.record.DOMRecord;

/**
 * PUBLIC:
 * <p><b>Purpose:</b>Provide a runtime public interface for preserving unmapped content from an 
 * XML Document.
 * <p><b>Responsibilities:</b><ul>
 * <li>Unmarshal XML into JavaObjects and maintain the associations between nodes and objects</li>
 * <li>Update the cached XML based on changes to the object</li>
 * <li>Update the cached objects based on changes to the XML Document</li>
 * <li>Provide API to access the cached Node for a given object</li>
 * <li>Provide API to access the cached Object for a given XML Node</li>
 * </ul>
 * 
 * <p>The XML Binder is a runtime class that allows an association to be maintained between the 
 * original XML Document and the Java Objects built from the Document. It allows unmapped content
 * (such as comments, processing instructions or other unmapped elements and attributes) to be 
 * preserved. The XMLBinder is created through an XMLContext.
 * 
 *  @see org.eclipse.persistence.oxm.XMLContext
 *  @author mmacivor
 */

public class XMLBinder {
    SAXUnmarshaller saxUnmarshaller;
    XMLContext context;
    XMLUnmarshaller unmarshaller;
    DocumentPreservationPolicy documentPreservationPolicy;
    DOMReader reader;
    public XMLBinder(XMLContext context) {
        unmarshaller = context.createUnmarshaller();
        saxUnmarshaller = new SAXUnmarshaller(unmarshaller);        
        this.context = context;
        documentPreservationPolicy = new XMLBinderPolicy();
        reader = new DOMReader();
    }
    /**
     * This method will unmarshal the provided node into mapped java objects. The original node
     * will be cached rather than thrown away.
     * @param node
     * @return The root object unmarshalled from the provided node.
     */
    public Object unmarshal(org.w3c.dom.Node node) {
        reader.setDocPresPolicy(documentPreservationPolicy);
        Object toReturn = saxUnmarshaller.unmarshal(reader, node);
        return toReturn;
    }
    
    public XMLRoot unmarshal(org.w3c.dom.Node node, Class javaClass) {
        XMLRoot root = null;
        reader.setDocPresPolicy(documentPreservationPolicy);
        root = (XMLRoot)saxUnmarshaller.unmarshal(reader, node, javaClass);
        return root;
    }
    /**
     * This method will update the cached XML node for the provided object. If no node exists for this
     * object, then no operation is performed.
     * @param obj
     */
    public void updateXML(Object obj) {
        if(obj instanceof XMLRoot) {
            obj = ((XMLRoot)obj).getObject();
        }        
        Node associatedNode = documentPreservationPolicy.getNodeForObject(obj);
        if(associatedNode == null) {
            return;
        }
        updateXML(obj, associatedNode);
    }
    
    public void updateXML(Object obj, Node associatedNode) {
        if(obj instanceof XMLRoot) {
            obj = ((XMLRoot)obj).getObject();
        }
        DOMRecord root = new DOMRecord((Element)associatedNode);
        root.setDocPresPolicy(this.documentPreservationPolicy);
        AbstractSession session = context.getSession(obj);
        XMLDescriptor rootDescriptor = (XMLDescriptor)context.getSession(obj).getDescriptor(obj);
        ((XMLObjectBuilder)rootDescriptor.getObjectBuilder()).buildIntoNestedRow(root, obj, session);
    }
    /**
     * Gets the XML Node associated with the provided object.
     * @param object
     * @return an XML Node used to construct the given object. Null if no node exists for this object.
     */
    public Node getXMLNode(Object object) {
        return documentPreservationPolicy.getNodeForObject(object);
    }
    
    /**
     * Gets the Java Object associated with the provided XML Node.
     * @param node
     * @return the Java Object associated with this node. If no object is associated then returns null
     */
    public Object getObject(Node node) {
        return documentPreservationPolicy.getObjectForNode(node);
    }
    
    
    /**
     * Updates the object associated with the provided node to reflect any changed made to that node.
     * If this Binder has no object associated with the given node, then no operation is performed.
     * @param node
     */
    public void updateObject(org.w3c.dom.Node node) {
        Object cachedObject = documentPreservationPolicy.getObjectForNode(node);
        if(cachedObject != null) {
            unmarshal(node);
        }
    }
    
    /**
     * Gets this XMLBinder's document preservation policy.
     * @return an instance of DocumentPreservationPolicy
     * @see org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy
     */
    public DocumentPreservationPolicy getDocumentPreservationPolicy() {
        return documentPreservationPolicy;
    }
}