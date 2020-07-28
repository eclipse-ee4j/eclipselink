/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 27 January 2012 - 2.3.3 - Initial implementation
package org.eclipse.persistence.internal.oxm;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>: Handles grouping elements for Collections when used with the TreeObjectBuilder.
 * </p>
 */
public class CollectionGroupingElementNodeValue extends NodeValue {

    ContainerValue containerValue;

    public CollectionGroupingElementNodeValue(ContainerValue c) {
        this.containerValue = c;
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return true;
    }

    public boolean isMarshalNodeValue() {
        return false;
    }

    public boolean isWrapperNodeValue() {
        return true;
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        Object collection = unmarshalRecord.getContainerInstance(this.containerValue, false);
        endElement(xPathFragment, unmarshalRecord, collection);
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Object collection) {
        if (collection == null && !unmarshalRecord.isNil()) {
            unmarshalRecord.setContainerInstance(this.containerValue.getIndex(), this.containerValue.getContainerInstance());
        }
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        return false;
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        return false;
    }

}
