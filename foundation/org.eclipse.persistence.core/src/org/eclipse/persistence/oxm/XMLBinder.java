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
package org.eclipse.persistence.oxm;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.XMLObjectBuilder;
import org.eclipse.persistence.internal.oxm.documentpreservation.XMLBinderPolicy;
import org.eclipse.persistence.internal.oxm.record.DOMReader;
import org.eclipse.persistence.internal.oxm.record.SAXUnmarshaller;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.platform.xml.XMLTransformer;

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
    XMLMarshaller marshaller;
    XMLUnmarshaller unmarshaller;
    DocumentPreservationPolicy documentPreservationPolicy;
    DOMReader reader;

    public XMLBinder(XMLContext context) {
        this.context = new XMLContext(context.getXMLContextState());
        marshaller = this.context.createMarshaller();
        unmarshaller = this.context.createUnmarshaller();
        saxUnmarshaller = new SAXUnmarshaller(unmarshaller, null);
        documentPreservationPolicy = new XMLBinderPolicy();
        reader = new DOMReader(unmarshaller);
    }

    /**
     * This method will unmarshal the provided node into mapped java objects. The original node
     * will be cached rather than thrown away.
     * @param node
     * @return The root object unmarshalled from the provided node.
     */
    public Object unmarshal(org.w3c.dom.Node node) {
        validateNode(node);
        
        reader.setDocPresPolicy(documentPreservationPolicy);
        Object toReturn = saxUnmarshaller.unmarshal(reader, node);
        return toReturn;
    }

    private void validateNode(org.w3c.dom.Node node) {
        if (getSchema() != null) {
            Validator validator = getSchema().newValidator();
            validator.setErrorHandler(getErrorHandler());
            try {
                validator.validate(new DOMSource(node));
            } catch (Exception e) {
                throw XMLMarshalException.validateException(e);
            }
        }
    }
    
    public XMLRoot unmarshal(org.w3c.dom.Node node, Class javaClass) {
        validateNode(node);
        reader.setDocPresPolicy(documentPreservationPolicy);
        return buildXMLRootFromObject(saxUnmarshaller.unmarshal(reader, node, javaClass));
    }

    /**
     * This method will update the cached XML node for the provided object. If no node exists for this
     * object, then no operation is performed.
     * @param obj
     */
    public void updateXML(Object obj) {
        if(obj instanceof Root) {
            obj = ((Root)obj).getObject();
        }
        Node associatedNode = documentPreservationPolicy.getNodeForObject(obj);
        if(associatedNode == null) {
            return;
        }
        updateXML(obj, associatedNode);
    }
    
    public void marshal(Object obj, Node node) {
        XMLDescriptor desc = null;
        boolean isXMLRoot = obj instanceof Root;
        if (isXMLRoot) {
            Object o = ((Root) obj).getObject();
            desc = (XMLDescriptor) context.getSession(o).getDescriptor(o);
        } else {
            desc = (XMLDescriptor) context.getSession(obj).getDescriptor(obj);
        }
        
        DOMRecord domRecord = null;
        if (!isXMLRoot) {
            domRecord = new DOMRecord(desc.getDefaultRootElement(), desc.getNamespaceResolver());
            domRecord.setDocPresPolicy(getDocumentPreservationPolicy());
        }
        Node n = this.marshaller.objectToXML(obj, node, desc, domRecord, isXMLRoot, this.getDocumentPreservationPolicy());

        validateNode(n);
        
        DOMResult result = new DOMResult(node);
        XMLTransformer transformer = marshaller.getTransformer();
        if (isXMLRoot) {
            String oldEncoding = transformer.getEncoding();
            String oldVersion = transformer.getVersion();
            if (((Root) obj).getEncoding() != null) {
                transformer.setEncoding(((Root) obj).getEncoding());
            }
            if (((Root) obj).getXMLVersion() != null) {
                transformer.setVersion(((Root) obj).getXMLVersion());
            }
            transformer.transform(n, result);
            if(oldEncoding != null){
                transformer.setEncoding(oldEncoding);
            }
            if(oldVersion != null){
                transformer.setVersion(oldVersion);
            }
        } else {
            transformer.transform(n, result);
           
        }
    }

    public void updateXML(Object obj, Node associatedNode) {
        if (obj instanceof Root) {
            obj = ((Root)obj).getObject();
        }
        
        Node objNode = this.getXMLNode(obj); 

        AbstractSession session = context.getSession(obj);
        if (objNode == associatedNode) {
            DOMRecord root = new DOMRecord((Element)associatedNode);
            root.setMarshaller(marshaller);            
            root.setDocPresPolicy(this.documentPreservationPolicy);
            XMLDescriptor rootDescriptor = (XMLDescriptor) session.getDescriptor(obj);
            ((XMLObjectBuilder)rootDescriptor.getObjectBuilder()).buildIntoNestedRow(root, obj, session);
        }
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
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            node = ((Document) node).getDocumentElement();
        }
        
        Object cachedObject = documentPreservationPolicy.getObjectForNode(node);
        if (cachedObject != null) {
            unmarshal(node);
        } else {
            throw XMLMarshalException.objectNotFoundInCache(node.getNodeName());
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

    public XMLMarshaller getMarshaller() {
        return marshaller;
    }

    public void setMarshaller(XMLMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setSchema(Schema aSchema) {
        this.unmarshaller.setSchema(aSchema);
        this.saxUnmarshaller.setSchema(aSchema);
    }

    public Schema getSchema() {
        return this.unmarshaller.getSchema();
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.unmarshaller.setErrorHandler(errorHandler);
        this.saxUnmarshaller.setErrorHandler(errorHandler);
    }

    public ErrorHandler getErrorHandler() {
        return this.unmarshaller.getErrorHandler();
    }
    
    /**
     * Create an XMLRoot instance.  If the object is an instance of XMLRoot
     * it will simply be returned.  Otherwise, we will create a new XMLRoot
     * using the object's descriptor default root element - any prefixes
     * will be resolved - and the given object
     *  
     * @param obj
     * @return an XMLRoot instance encapsulating the given object
     */
    private XMLRoot buildXMLRootFromObject(Object obj) {    
        if (obj instanceof XMLRoot) {           
            return (XMLRoot) obj;            
        }
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setObject(obj);
        
        // at this point, the default root element of the object being
        // marshalled to == the root element  - here we need to create 
        // an XMLRoot instance using information from the returned 
        // object
        org.eclipse.persistence.sessions.Session sess = this.unmarshaller.getXMLContext().getSession(obj);
        XMLDescriptor desc = (XMLDescriptor) sess.getClassDescriptor(obj);
        
        // here we are assuming that if we've gotten this far, there
        // must be a default root element set on the descriptor.  if
        // this is incorrect, we need to check for null and throw an
        // exception
        String rootName = desc.getDefaultRootElement();
        if (rootName == null) {
            return xmlRoot;
        }
        String rootNamespaceUri = null;
        int idx = rootName.indexOf(":");
        if (idx != -1) {
            rootNamespaceUri = desc.getNamespaceResolver().resolveNamespacePrefix(rootName.substring(0, idx)); 
            rootName = rootName.substring(idx + 1);
        }
        xmlRoot.setLocalName(rootName);
        xmlRoot.setNamespaceURI(rootNamespaceUri);
        return xmlRoot;
    }
}
