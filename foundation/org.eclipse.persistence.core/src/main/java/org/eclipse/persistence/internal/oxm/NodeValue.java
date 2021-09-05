/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
     */
    public boolean isMarshalOnlyNodeValue() {
        return false;
    }

    /**
     * INTERNAL:
     */
    public boolean isOwningNode(XPathFragment xPathFragment) {
        return null == xPathFragment.getNextFragment();
    }

    /**
     * INTERNAL:
     */
    public abstract boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver);

    /**
     * INTERNAL:
     * This method is no longer required as now MarshalRecord maintains a
     * reference to the XMLMarshaller.
     */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, Marshaller marshaller) {
        marshalRecord.setMarshaller(marshaller);
        return this.marshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
    }

    /**
     * INTERNAL:
     */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext, XPathFragment rootFragment) {
        return this.marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, marshalContext);
    }

    /**
     * INTERNAL:
     * This method provides an optimization in cases where the value has already
     * been calculated.
     */
    public abstract boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext);

    /**
     * INTERNAL:
     * This method provides an optimization in cases where the value has already
     * been calculated.
     */
    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext, XPathFragment rootFragment) {
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, marshalContext);
    }

/**
     * INTERNAL:
     * Override this method if the NodeValue is applicable to sequenced objects.
 */
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        return this.marshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
    }

    /**
     * INTERNAL:
     */
    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        return true;
    }

    /**
     * INTERNAL:
     */
    public void attribute(UnmarshalRecord unmarshalRecord, String URI, String localName, String value) {
        // No operation for parent
    }

    /**
     * INTERNAL:
     */
    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
    }

    /**
     * INTERNAL:
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
     */
    public boolean isMixedContentNodeValue() {
        return false;
    }

    public boolean isWrapperNodeValue() {
        return false;
    }

}
