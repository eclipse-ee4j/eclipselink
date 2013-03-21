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
package org.eclipse.persistence.internal.oxm;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.xml.sax.Attributes;

/**
 * INTERNAL:
 * <p><b>Purpose</b>:  A NodeValue is responsible for performing the unmarshal
 * and marshal operation at a mapping or policy level.  The operations are based
 * on a SAX ContextHandler.</p>
 * <p><b>Responsibilities</b>:<ul>
 * <li>Maintain a reference to the owning XPathNode.</li>
 * <li>Given a XPathFragment recognize the node to which the mapping should be 
 * applied.</li>
 * <li>Perform the unmarshal and marshal operation for the given mapping or 
 * policy.</li>
 * </ul> 
 */

public abstract class NodeValue {
    protected XPathNode xPathNode;

    public XPathNode getXPathNode() {
        return xPathNode;
    }

    public void setXPathNode(XPathNode xPathNode) {
        this.xPathNode = xPathNode;
    }

    /**
     * INTERNAL:
     * Return whether we ignore this node value when marshalling its parent
     * @return
     */
    public boolean isMarshalOnlyNodeValue() {
    	return false;
    }
    
    /**
     * INTERNAL:
     * @param xPathFragment
     * @return
     */
    public boolean isOwningNode(XPathFragment xPathFragment) {
        return null == xPathFragment.getNextFragment();
    }

    /**
     * INTERNAL:
     * @param xPathFragment
     * @param marshalRecord
     * @param object
     * @param session
     * @param namespaceResolver
     * @return
     */
    public abstract boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver);

    /**
     * INTERNAL:
     * This method is no longer required as now MarshalRecord maintains a 
     * reference to the XMLMarshaller.
     * @param xPathFragment
     * @param marshalRecord
     * @param object
     * @param session
     * @param namespaceResolver
     * @param marshaller
     * @return
     */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, Marshaller marshaller) {
    	marshalRecord.setMarshaller(marshaller);
        return this.marshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
    }
    
    /**
     * INTERNAL:
     * @param xPathFragment
     * @param marshalRecord
     * @param object
     * @param session
     * @param namespaceResolver
     * @return
     */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext, XPathFragment rootFragment) {
        return this.marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, marshalContext);
    }

    /**
     * INTERNAL:
     * This method provides an optimization in cases where the value has already
     * been calculated.
     * @param xPathFragment
     * @param marshalRecord
     * @param object
     * @param objectValue
     * @param session
     * @param namespaceResolver
     * @param marshalContext
     * @return
     */
    public abstract boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext);

    /**
     * INTERNAL:
     * This method provides an optimization in cases where the value has already
     * been calculated.
     * @param xPathFragment
     * @param marshalRecord
     * @param object
     * @param objectValue
     * @param session
     * @param namespaceResolver
     * @param marshalContext
     * @return
     */
    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext, XPathFragment rootFragment) {
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, marshalContext);
    }

/**
     * INTERNAL:
     * Override this method if the NodeValue is applicable to sequenced objects.
     * @param xPathFragment
     * @param marshalRecord
     * @param object
     * @param session
     * @param namespaceResolver
     * @param xPathNodeWalker
     * @return
     */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        return this.marshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
    }
    
    /**
     * INTERNAL:
     * @param xPathFragment
     * @param unmarshalRecord
     * @param atts
     * @return
     */
    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
    	return true;
    }

    /**
     * INTERNAL:
     * @param unmarshalRecord
     * @param URI
     * @param localName
     * @param value
     */
    public void attribute(UnmarshalRecord unmarshalRecord, String URI, String localName, String value) {
        // No operation for parent
    }

    /**
     * INTERNAL:
     * @param xPathFragment
     * @param unmarshalRecord
     */
    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
    }

    /**
     * INTERNAL:
     * @param unmarshalRecord
     * @param atts
     * @return
     */
    public UnmarshalRecord buildSelfRecord(UnmarshalRecord unmarshalRecord, Attributes atts) {
        return null;
    }
    
    public void endSelfNodeValue(UnmarshalRecord unmarshalRecord, UnmarshalRecord selfRecord, Attributes atts) {        
    }

    /**
     * INTERNAL:
      * @return Returns true if the NodeValue implements ContainerValue.
      * @see org.eclipse.persistence.internal.oxm.ContainerValue
      */
    public boolean isContainerValue() {
        return false;
    }

    /**
     * INTERNAL:
      * @return Returns true if the NodeValue implements NullCapableValue.
      * @see org.eclipse.persistence.internal.oxm.NullCapableValue
      */
    public boolean isNullCapableValue() {
        return false;
    }
    
    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Object collection) {
    }
    

    public boolean isUnmarshalNodeValue() {
        return true;
    }
    
    public boolean isMarshalNodeValue() {
        return true;
    }
    

    /**
     * INTERNAL:
     * Marshal any 'self' mapped attributes.
     * 
     * @param xPathFragment
     * @param marshalRecord
     * @param object
     * @param session
     * @param namespaceResolver
     * @param marshaller
     * @return
     */
    public boolean marshalSelfAttributes(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, Marshaller marshaller) {
        return false;
    }

    public boolean isMappingNodeValue() {
        return false;
    }
    
    public boolean isWhitespaceAware() {
        return false;
    }
    
    public boolean isAnyMappingNodeValue() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this is the node value representing mixed content.
     * @return
     */
    public boolean isMixedContentNodeValue() {
        return false;
    }

    public boolean isWrapperNodeValue() {
        return false;
    }

}
