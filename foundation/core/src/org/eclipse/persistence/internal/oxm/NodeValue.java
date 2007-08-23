/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
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
    protected static final char COLON = ':';
    protected static final String EMPTY_STRING = "";
    private XPathNode xPathNode;

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
     * 
     * @param xPathFragment
     * @return
     */
    public boolean isOwningNode(XPathFragment xPathFragment) {
        return null == xPathFragment.getNextFragment();
    }

    public abstract boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver);

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver, org.eclipse.persistence.oxm.XMLMarshaller marshaller) {
        return this.marshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
    	return true;
    }

    public void attribute(UnmarshalRecord unmarshalRecord, String URI, String localName, String value) {
        //no op for parent
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
    }

    public UnmarshalRecord buildSelfRecord(UnmarshalRecord unmarshalRecord, Attributes atts) {
        return null;
    }

    /**
      * @return Returns true if the NodeValue implements ContainerValue.
      * @see org.eclipse.persistence.internal.oxm.ContainerValue
      */
    public boolean isContainerValue() {
        return false;
    }

    /**
      * @return Returns true if the NodeValue implements NullCapableValue.
      * @see org.eclipse.persistence.internal.oxm.NullCapableValue
      */
    public boolean isNullCapableValue() {
        return false;
    }
}