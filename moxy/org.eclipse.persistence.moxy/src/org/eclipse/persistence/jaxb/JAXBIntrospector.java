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
package org.eclipse.persistence.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sessions.Session;

/**
 * INTERNAL
 * <p><b>Purpose:</b>Provide a TopLink implementation of JAXBIntrospector
 * <p><b>Responsibilities:</b><ul>
 * <li>Determine if a an object has an associated Global Element</li>
 * <li>Get an element QName for an object that has an associated global element</li>
 * </ul>
 * <p>This class is the TopLink implementation of JAXBIntrospector. An Introspector is created
 * by a JAXBContext and allows the user to access certain pieces of meta-data about an instance
 * of a JAXB bound class.
 * 
 * @see javax.xml.bind.JAXBIntrospector
 * @see org.eclipse.persistence.jaxb.JAXB20Context
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 */

public class JAXBIntrospector extends javax.xml.bind.JAXBIntrospector {
    private XMLContext context;
    public JAXBIntrospector(XMLContext context) {
        this.context = context;
    }
    
    public boolean isElement(Object obj) {
    	if (obj instanceof JAXBElement) {
    		return true;
    	}
    	
        try {
            Session session = context.getSession(obj);
            if(session == null) {
                return false;
            }
            Descriptor descriptor = (Descriptor)session.getDescriptor(obj);
            if(descriptor == null) {
                return false;
            }
            return descriptor.getDefaultRootElementField() != null;
        } catch(XMLMarshalException e) {
            return false;
        }
    }
    
    public QName getElementName(Object obj) {
        if(!isElement(obj)) {
            return null;
        }

        if(obj instanceof JAXBElement) {
            return ((JAXBElement) obj).getName();
        }
        try {
            Descriptor descriptor = (Descriptor)context.getSession(obj).getDescriptor(obj);
            XPathFragment rootFragment = descriptor.getDefaultRootElementField().getXPathFragment(); 
            return new QName(rootFragment.getNamespaceURI(), rootFragment.getLocalName()); 
        } catch(XMLMarshalException e) {
            return null;
        }
    }
}
