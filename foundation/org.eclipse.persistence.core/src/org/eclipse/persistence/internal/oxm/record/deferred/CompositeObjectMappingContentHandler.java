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
package org.eclipse.persistence.internal.oxm.record.deferred;

import org.eclipse.persistence.internal.oxm.XMLCompositeObjectMappingNodeValue;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.CompositeObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * INTERNAL:<br>
 * <p>
 * <b>Purpose</b>: An implementation of DeferredContentHandler used to queue
 * events to enable state-specific behavior for simple, complex or empty
 * elements.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * Null Composite Objects are marshalled in 2 ways when the input XML node is
 * empty. (1) as null - isNullRepresentedByEmptyNode = true (2) as empty object -
 * isNullRepresentedByEmptyNode = false A deferred contentHandler is used to
 * queue events until we are able to determine whether we are in one of
 * empty/simple/complex state. Control is returned to the UnmarshalHandler after
 * creation of (1) or (2) above is started.
 * </ul>
 */
public class CompositeObjectMappingContentHandler extends CompositeMappingContentHandler {

    /** The NullCapableValue that passed control to this handler */
    private XMLCompositeObjectMappingNodeValue nodeValue;

    public CompositeObjectMappingContentHandler(UnmarshalRecord parentRecord, //
            XMLCompositeObjectMappingNodeValue aNodeValue, CompositeObjectMapping aMapping, //
            Attributes atts, XPathFragment aFragment, Descriptor aDescriptor) {
        super(parentRecord, aMapping, atts, aMapping.getNullPolicy(), aFragment, aDescriptor);
        nodeValue = aNodeValue;
    }

    @Override
    protected XMLCompositeObjectMappingNodeValue getNodeValue() {
        return nodeValue;
    }


    @Override
    protected void processEmptyElement() throws SAXException {
        // Remove original startElement event as it has been precluded by the nodeValue call below
        getEvents().remove(0);
        // Prerequisite: We know that (nullPolicy.isNullRepresentedByEmptyNode() || nullPolicy.isNullRepresentedByXsiNil()) is true
        // Null: Set the object to null on the node value if we are empty with inrben=true
        nodeValue.setNullValue(getParent().getCurrentObject(), getParent().getSession());
        executeEvents(getParent());
    }

}