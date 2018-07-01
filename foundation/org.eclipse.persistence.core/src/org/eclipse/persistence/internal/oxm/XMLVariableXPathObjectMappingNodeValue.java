/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.5.1 - Initial Implementation
package org.eclipse.persistence.internal.oxm;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.VariableXPathObjectMapping;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

public class XMLVariableXPathObjectMappingNodeValue extends XMLVariableXPathMappingNodeValue {

    VariableXPathObjectMapping mapping;

    public XMLVariableXPathObjectMappingNodeValue(VariableXPathObjectMapping mapping) {
        this.mapping = mapping;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (mapping.isReadOnly()) {
            return false;
        }
        Object objectValue = marshalContext.getAttributeValue(object, mapping);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, marshalContext);
    }

    @Override
    public VariableXPathObjectMapping getMapping() {
        return mapping;
    }

    protected void setOrAddAttributeValue(UnmarshalRecord unmarshalRecord, Object value, XPathFragment xPathFragment, Object collection){
         unmarshalRecord.setAttributeValue(value, mapping);
    }

}
