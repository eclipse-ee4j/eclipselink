/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
// mmacivor - July 18 05/2008 - 1.0 - Initial implementation
package org.eclipse.persistence.internal.oxm.record.deferred;

import org.xml.sax.SAXException;
import org.eclipse.persistence.internal.oxm.NodeValue;
import org.eclipse.persistence.internal.oxm.XMLBinaryAttachmentHandler;
import org.eclipse.persistence.internal.oxm.XMLInlineBinaryHandler;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataMapping;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.XMLConverterMapping;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

/**
 * <p><b>Purpose</b>: Implementation of DeferredContentHandler for Binary Mappings.
 * <p><b>Responsibilities</b>:<ul>
 * <li> If the element is empty then execute stored events and return control to the original parentRecord
 * <li> If the element has simple content execute stored events on an inline binary handler
 * <li> If the element has complex content execute stored events on a binary attachment handler
 * </ul>
 */
public class BinaryMappingContentHandler extends DeferredContentHandler {
    private Mapping mapping;
    private NodeValue nodeValue;
    private XMLConverterMapping converter;
    private boolean isCollection;
    private UnmarshalRecord workingUnmarshalRecord;
    private boolean finished;

    public BinaryMappingContentHandler(UnmarshalRecord parentRecord, NodeValue nodeValue, BinaryDataMapping mapping) {
        super(parentRecord);
        this.mapping = mapping;
        this.converter = mapping;
        this.nodeValue = nodeValue;
        this.isCollection = false;
        this.finished = false;
    }

    public BinaryMappingContentHandler(UnmarshalRecord parentRecord, NodeValue nodeValue, BinaryDataCollectionMapping mapping) {
        super(parentRecord);
        this.mapping = mapping;
        this.converter = mapping;
        this.nodeValue = nodeValue;
        this.isCollection = true;
    }

    @Override
    public void processComplexElement() throws SAXException {
        getEvents().remove(0);
        workingUnmarshalRecord = new XMLBinaryAttachmentHandler(this.getParent(), nodeValue, mapping, converter, isCollection);
        executeEvents(workingUnmarshalRecord);
    }

    @Override
    public void processSimpleElement() throws SAXException {
        getEvents().remove(0);
        workingUnmarshalRecord = new XMLInlineBinaryHandler(this.getParent(), nodeValue, mapping, converter, isCollection);
        executeEvents(workingUnmarshalRecord);
    }

    @Override
    public void processEmptyElement() throws SAXException {
        processSimpleElement();
    }

    @Override
    protected void executeEvents(UnmarshalRecord unmarshalRecord) throws SAXException {
        super.executeEvents(unmarshalRecord);
        finished = true;
    }

    public UnmarshalRecord getWorkingUnmarshalRecord() {
        return workingUnmarshalRecord;
    }

    public boolean isFinished() {
        return finished;
    }



}
