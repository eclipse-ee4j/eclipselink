/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.XMLRelationshipMappingNodeValue;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class CompositeMappingContentHandler extends DeferredContentHandler {

    /** The AbstractNullPolicy associated with the mapping using this handler */
    protected AbstractNullPolicy nullPolicy;
    protected Attributes attributes;
    protected Mapping mapping;
    protected XPathFragment xPathFragment;
    protected Descriptor xmlDescriptor;

    public CompositeMappingContentHandler(UnmarshalRecord parentRecord, Mapping aMapping, Attributes atts, AbstractNullPolicy aNullPolicy, XPathFragment aFragment, Descriptor aDescriptor) {
        super(parentRecord);
        mapping = aMapping;
        nullPolicy = aNullPolicy;
        xPathFragment = aFragment;
        xmlDescriptor = aDescriptor;
        try {
            attributes = buildAttributeList(atts);
        } catch(SAXException e) {
        }
    }

    protected abstract XMLRelationshipMappingNodeValue getNodeValue();

    /**
     * INTERNAL:
     * Create an empty object to be used by empty, complex or simple events.
     * A childRecord is created on the parent UnmarshalRecord.
     */
    protected void createEmptyObject() {
        try {
            // Instantiate a new object
        	Field xmlFld = (Field)mapping.getField();
            if (xmlFld.hasLastXPathFragment()) {
                getParent().setLeafElementType(xmlFld.getLastXPathFragment().getLeafElementType());
            }
            // Create a childRecord on the parent UnmarshalRecord
            getNodeValue().processChild(xPathFragment, getParent(), attributes, xmlDescriptor, mapping);
        } catch (SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    @Override
    protected void processEmptyElementWithAttributes() throws SAXException {
        processComplexElement();
    }

    @Override
    protected void processComplexElement() throws SAXException {
        // Remove original startElement event as it has been precluded by the startElement call below
        getEvents().remove(0);
        createEmptyObject();
        // execute events on the child
        executeEvents(getParent().getChildRecord());
    }

    @Override
    protected void processSimpleElement() throws SAXException {
        // Remove original startElement event as it has been precluded by the startElement call below
        getEvents().remove(0);
        // testcase: <team><manager>10</manager></team> where 10=text() or id mapping
        createEmptyObject();
        executeEvents(getParent().getChildRecord());
    }

}