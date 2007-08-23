/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.mappings;

import org.eclipse.persistence.internal.oxm.NullCapableValue;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.sessions.Session;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * This abstraction allows for the handling various representations of null in 
 * XML documents.
 */
public interface NodeNullPolicy {

	/**
	 * When using the SAX Platform, this method is responsible for marshalling
	 * null values for the XML Direct Mapping.
	 * @return true if this method caused any nodes to be marshalled, else 
	 * false.
	 */
    public boolean directMarshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Session session, NamespaceResolver namespaceResolver);
    
	/**
	 * When using the SAX Platform, this method is responsible for marshalling
	 * null values for the XML Composite Object Mapping.
	 * @return true if this method caused any nodes to be marshalled, else 
	 * false.
	 */
    public boolean compositeObjectMarshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Session session, NamespaceResolver namespaceResolver);
    
    /**
     * When using the DOM Platform, this method is responsible for marshalling
     * null values for the XML Composite Object Mapping.
     * @param record
     * @param object
     * @param field
     * @return true if this method caused any objects to be marshalled, else 
     * false.
     */
    public boolean compositeObjectMarshal(XMLRecord record, Object object, XMLField field);

    /**
     * When using the DOM Platform during unmarshal operations.  Use the 
     * attributes to determine if the element represents a null value.
     * @return true if based on the attributes the corresponding element
     * represents a null value, else false.
     */
    public boolean valueIsNull(Attributes attributes);
    
    /**
     * When using the DOM Platform during unmarshal operations.  Use the 
     * element to determine if the element represents a null value.
     * @return true if based on the element it represents a null value, else 
     * false.
     */
    public boolean valueIsNull(Element element);
    
    /**
     * This method indicates how to handle missing nodes.  By returning true an
     * explicit set operation will be performed setting the value to null or its 
     * primitive type equivalent.
     * @return true if an explicit set operation should be performed for missing 
     * nodes, else false.
     */
    public boolean isNullCapabableValue();
    
    /**
     * When using the SAX Platform this allows a NodeValue to be registered to 
     * receive events from the TreeObjectBuilder.
     */
    public void xPathNode(XPathNode xPathNode, NullCapableValue nullCapableValue);
    
}