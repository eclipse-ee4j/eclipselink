/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.oxm.XMLContext;

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
 * @see org.eclipse.persistence.jaxb.JAXBContext
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 */

public class JAXBIntrospector extends javax.xml.bind.JAXBIntrospector {
    private XMLContext context;
    public JAXBIntrospector(XMLContext context) {
        this.context = context;
    }

    @Override
    public boolean isElement(Object obj) {
        if (obj instanceof JAXBElement) {
            return true;
    }

        try {
            Descriptor descriptor = context.getDescriptorForObject(obj);
            return null != descriptor && null != descriptor.getDefaultRootElementField();
        } catch(XMLMarshalException e) {
            return false;
        }
    }

    @Override
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
