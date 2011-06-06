/*******************************************************************************
* Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;

/**
 * <p><b>Purpose</b>: Implementation of DeferredContentHandler for Binary Mappings.
 * <p><b>Responsibilities</b>:<ul>
 * <li> If the element is empty then execute stored events and return control to the original parentRecord
 * <li> If the element has simple content execute stored events on an inline binary handler
 * <li> If the element has complex content execute stored events on a binary attachment handler
 * </ul>
 */
public class BinaryMappingContentHandler extends DeferredContentHandler {
    private DatabaseMapping mapping;
    private NodeValue nodeValue;
    private Converter converter;
    private boolean isCollection;
    private UnmarshalRecord workingUnmarshalRecord;
    private boolean finished;          
     
    public BinaryMappingContentHandler(UnmarshalRecord parentRecord, NodeValue nodeValue, XMLBinaryDataMapping mapping) {
        super(parentRecord);
        this.mapping = mapping;
        this.converter = mapping.getConverter();
        this.nodeValue = nodeValue;
        this.isCollection = false;
        this.finished = false;
    }
    
    public BinaryMappingContentHandler(UnmarshalRecord parentRecord, NodeValue nodeValue, XMLBinaryDataCollectionMapping mapping) {
        super(parentRecord);
        this.mapping = mapping;
        this.converter = mapping.getValueConverter();
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