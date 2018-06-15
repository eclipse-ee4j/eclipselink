/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.NodeValue;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;

/**
 * An implementation of MarshalContext for handling plain old java objects that
 * are mapped to XML.
 */
public class ObjectMarshalContext implements MarshalContext {

    private static final ObjectMarshalContext INSTANCE = new ObjectMarshalContext();

    public static ObjectMarshalContext getInstance() {
        return INSTANCE;
    }

    private ObjectMarshalContext() {
        super();
    }

    @Override
    public MarshalContext getMarshalContext(int index) {
        return this;
    }

    @Override
    public int getNonAttributeChildrenSize(XPathNode xPathNode) {
        return xPathNode.getNonAttributeChildren().size();
    }

    @Override
    public Object getNonAttributeChild(int index, XPathNode xPathNode) {
        return xPathNode.getNonAttributeChildren().get(index);
    }

    @Override
    public Object getAttributeValue(Object object, Mapping mapping) {
        return mapping.getAttributeValueFromObject(object);
    }

    @Override
    public boolean marshal(NodeValue nodeValue, XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        return nodeValue.marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, this);
    }

    @Override
    public boolean marshal(NodeValue nodeValue, XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, XPathFragment rootFragment) {
        return nodeValue.marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, this, rootFragment);
    }

}
