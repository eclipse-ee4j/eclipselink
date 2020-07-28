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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.internal.oxm.record.deferred;

import org.eclipse.persistence.internal.oxm.XMLCompositeCollectionMappingNodeValue;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.CompositeCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CompositeCollectionMappingContentHandler extends CompositeMappingContentHandler {

    /** The NullCapableValue that passed control to this handler */
    private XMLCompositeCollectionMappingNodeValue nodeValue;

    /**
     * @param parentRecord
     * @param aNodeValue
     * @param aNullPolicy
     */
    public CompositeCollectionMappingContentHandler(UnmarshalRecord parentRecord, //
            XMLCompositeCollectionMappingNodeValue aNodeValue, CompositeCollectionMapping aMapping, //
            Attributes atts, XPathFragment aFragment, Descriptor aDescriptor) {
        super(parentRecord, aMapping, atts, aMapping.getNullPolicy(), aFragment, aDescriptor);
        nodeValue = aNodeValue;
    }

    @Override
    protected XMLCompositeCollectionMappingNodeValue getNodeValue() {
        return nodeValue;
    }

    @Override
    protected void processEmptyElement() throws SAXException {
        // Remove original startElement event as it has been precluded by the nodeValue call below
        getEvents().remove(0);
        executeEvents(getParent());
    }

}
