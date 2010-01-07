/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.XMLCompositeObjectMappingNodeValue;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;

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
public class CompositeObjectMappingContentHandler extends	DeferredContentHandler {
	/** The AbstractNullPolicy associated with the mapping using this handler */
	private AbstractNullPolicy nullPolicy;
	/** The NullCapableValue that passed control to this handler */
	private XMLCompositeObjectMappingNodeValue nodeValue;
	private XMLCompositeObjectMapping mapping;
	private Attributes attributes;
	private XPathFragment xPathFragment;
	private XMLDescriptor xmlDescriptor;
	
	/**
	 * @param parentRecord
	 * @param aNodeValue
	 * @param aNullPolicy
	 */
    public CompositeObjectMappingContentHandler(UnmarshalRecord parentRecord, //
    		XMLCompositeObjectMappingNodeValue aNodeValue, XMLCompositeObjectMapping aMapping, //
    		Attributes atts, XPathFragment aFragment, XMLDescriptor aDescriptor) {
        super(parentRecord);
        attributes = atts;
        mapping = aMapping;
        nullPolicy = mapping.getNullPolicy();
        nodeValue = aNodeValue;
        xPathFragment = aFragment;
        xmlDescriptor = aDescriptor;
    }

    protected void executeEvents(UnmarshalRecord unmarshalRecord) throws SAXException {    	
    	super.executeEvents(unmarshalRecord);
    }
    
    /**
     * INTERNAL:
     * Create an empty object to be used by empty, complex or simple events.
     * A childRecord is created on the parent UnmarshalRecord.
     */
    private void createEmptyObject() {
    	try {
        	// Instantiate a new object
            XMLField xmlFld = (XMLField)mapping.getField();
            if (xmlFld.hasLastXPathFragment()) {
                getParent().setLeafElementType(xmlFld.getLastXPathFragment().getLeafElementType());
            }
            // Create a childRecord on the parent UnmarshalRecord
            nodeValue.processChild(xPathFragment, getParent(), attributes, xmlDescriptor, mapping);
    	} catch (SAXException e) {
    		throw XMLMarshalException.unmarshalException(e);
    	}
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

	@Override
    protected void processSimpleElement() throws SAXException {
		// Remove original startElement event as it has been precluded by the startElement call below
		getEvents().remove(0);
		// testcase: <team><manager>10</manager></team> where 10=text() or id mapping
        createEmptyObject();
        executeEvents(getParent().getChildRecord());
    }

	@Override
    protected void processComplexElement() throws SAXException {
		// Remove original startElement event as it has been precluded by the startElement call below
		getEvents().remove(0);
        createEmptyObject();
		// execute events on the child
        executeEvents(getParent().getChildRecord());
    }
}
