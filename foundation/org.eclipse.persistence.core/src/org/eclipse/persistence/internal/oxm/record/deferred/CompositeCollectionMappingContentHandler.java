/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record.deferred;

import org.eclipse.persistence.internal.oxm.XMLCompositeCollectionMappingNodeValue;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
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
            XMLCompositeCollectionMappingNodeValue aNodeValue, XMLCompositeCollectionMapping aMapping, //
            Attributes atts, XPathFragment aFragment, XMLDescriptor aDescriptor) {
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
        // Prerequisite: We know that (nullPolicy.isNullRepresentedByEmptyNode() || nullPolicy.isNullRepresentedByXsiNil()) is true
        // Null: Set the object to null on the node value if we are empty with inrben=true
        // nodeValue.setNullValue(getParent().getCurrentObject(), getParent().getSession());
        nodeValue.getContainerPolicy().addInto(null, getParent().getContainerInstance(nodeValue), getParent().getSession());
        executeEvents(getParent());
    }

}