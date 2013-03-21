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
* mmacivor - July 18 05/2008 - 1.0 - Initial implementation
******************************************************************************/
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

    public void processComplexElement() throws SAXException {
        getEvents().remove(0);
        workingUnmarshalRecord = new XMLBinaryAttachmentHandler(this.getParent(), nodeValue, mapping, converter, isCollection);
        executeEvents(workingUnmarshalRecord);
    }
    
    public void processSimpleElement() throws SAXException {
        getEvents().remove(0);
        workingUnmarshalRecord = new XMLInlineBinaryHandler(this.getParent(), nodeValue, mapping, converter, isCollection);
        executeEvents(workingUnmarshalRecord);
    }
    
    public void processEmptyElement() throws SAXException {
        processSimpleElement();
    }
    
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