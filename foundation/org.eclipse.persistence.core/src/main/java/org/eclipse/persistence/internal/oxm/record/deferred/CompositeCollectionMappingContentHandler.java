/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
