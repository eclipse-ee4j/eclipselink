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
//     Denise Smith - April 21/2009 - 2.0 - Initial implementation

package org.eclipse.persistence.internal.oxm.record.deferred;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.xml.sax.SAXException;

/**
 * A DeferredContentHandler that will throw an exception when a descriptor
 * can't be found unless it's a simple element which will be processed by the mapping.
 */
public class DescriptorNotFoundContentHandler extends DeferredContentHandler {
    private Mapping mapping;

    public DescriptorNotFoundContentHandler(UnmarshalRecord parentRecord, Mapping mapping) {
        super(parentRecord);
        this.mapping = mapping;
    }

    @Override
    protected void processComplexElement() throws SAXException {
        throw XMLMarshalException.noDescriptorFound(mapping);
    }

    @Override
    protected void processEmptyElement() throws SAXException {
        processSimpleElement();
    }

    @Override
    protected void processSimpleElement() throws SAXException {
        getEvents().remove(0);
        executeEvents(getParent());
    }
}
