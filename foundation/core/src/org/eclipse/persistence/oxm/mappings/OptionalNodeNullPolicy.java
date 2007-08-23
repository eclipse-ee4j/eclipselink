/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.mappings;

import org.eclipse.persistence.internal.oxm.NullCapableValue;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.sessions.Session;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class OptionalNodeNullPolicy implements NodeNullPolicy {

	private final static OptionalNodeNullPolicy INSTANCE = new OptionalNodeNullPolicy();
	
	public static OptionalNodeNullPolicy getInstance() {
		return INSTANCE;
	}
	
	protected OptionalNodeNullPolicy() {
		super();
	}
	
    public boolean directMarshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Session session, NamespaceResolver namespaceResolver) {
        return false;
    }

    public boolean compositeObjectMarshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Session session, NamespaceResolver namespaceResolver) {
        return false;
    }
    
    /**
     * When using the DOM Platform, this method is responsible for marshalling
     * null values for the XML Composite Object Mapping.
     * @param record
     * @param object
     * @param field
     * @return true if this method caused any objects to be marshalled, else 
     * false.
     */
    public boolean compositeObjectMarshal(XMLRecord record, Object object, XMLField field) {
        return false;
    }

    public boolean valueIsNull(Attributes attributes) {
        return false;
    }

    public boolean valueIsNull(Element element) {
        return null == element;
    }

    public boolean isNullCapabableValue() {
        return true;
    }

    public void xPathNode(XPathNode xPathNode, NullCapableValue nullCapableValue) {
    }

}