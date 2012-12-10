/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;

public abstract class Context<
    ABSTRACT_SESSION extends CoreAbstractSession,
    DESCRIPTOR extends Descriptor,
    SESSION extends CoreSession> {

    /**
     * INTERNAL: 
     * Return the Descriptor with the default root mapping matching the QName 
     * parameter.
     */
    public abstract Descriptor getDescriptor(QName qName);

    /**
     * INTERNAL: 
     * Return the Descriptor with the default root mapping matching the
     * XPathQName parameter.
     */
    public abstract DESCRIPTOR getDescriptor(XPathQName xpathQName);

    /**
     * INTERNAL:
     * Return the Descriptor mapped to the global type matching the
     * XPathFragment parameter.
     */
    public abstract DESCRIPTOR getDescriptorByGlobalType(XPathFragment frag);

    /**
     * INTERNAL:
     * Return the DocumentPreservationPolicy associated with this session
     * @param session
     * @return
     */
    public abstract DocumentPreservationPolicy getDocumentPreservationPolicy(ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Return the session corresponding to this class. Since the class
     * may be mapped by more that one of the projects used to create the 
     * Context, this method will return the first match.
     */
    public abstract ABSTRACT_SESSION getReadSession(Class referenceClass);

    /**
     * INTERNAL:
     * Return the session corresponding to this descriptor. Since the class
     * may be mapped by more that one of the projects used to create the 
     * Context, this method will return the first match.
     */
    public abstract ABSTRACT_SESSION getReadSession(DESCRIPTOR descriptor);

    /**
     * INTERNAL: 
     * <code>
     * XMLContext xmlContext = new XMLContext("path0:path1");<br>
     * DatabaseSession session = xmlContext.getSession(0);  // returns session for path0<br>
     * </code>
     */
    public abstract SESSION getSession(int index);

    /**
     * INTERNAL: 
     * Return the session corresponding to this class. Since the class
     * may be mapped by more that one of the projects used to create the XML
     * Context, this method will return the first match.
     */
    public abstract ABSTRACT_SESSION getSession(Class clazz);

    /**
     * INTERNAL: 
     * Return the session corresponding to this object. Since the
     * object may be mapped by more that one of the projects used to create the
     * XML Context, this method will return the first match.
     */
    public abstract ABSTRACT_SESSION getSession(Object value);

    /**
     * <p>Query the object model based on the corresponding XML document.  The following pairings are equivalent:</p> 
     * 
     * <i>Return the Customer's ID</i>
     * <pre> Integer id = xmlContext.getValueByXPath(customer, "@id", null, Integer.class);
     * Integer id = customer.getId();</pre>
     * 
     * <i>Return the Customer's Name</i>
     * <pre> String name = xmlContext.getValueByXPath(customer, "ns:personal-info/ns:name/text()", null, String.class);
     * String name = customer.getName();</pre>
     * 
     * <i>Return the Customer's Address</i>
     * <pre> Address address = xmlContext.getValueByXPath(customer, "ns:contact-info/ns:address", aNamespaceResolver, Address.class);
     * Address address = customer.getAddress();</pre>
     * 
     * <i>Return all the Customer's PhoneNumbers</i> 
     * <pre> List phoneNumbers = xmlContext.getValueByXPath(customer, "ns:contact-info/ns:phone-number", aNamespaceResolver, List.class);
     * List phoneNumbers = customer.getPhoneNumbers();</pre>
     * 
     * <i>Return the Customer's second PhoneNumber</i>
     * <pre> PhoneNumber phoneNumber = xmlContext.getValueByXPath(customer, "ns:contact-info/ns:phone-number[2]", aNamespaceResolver, PhoneNumber.class);
     * PhoneNumber phoneNumber = customer.getPhoneNumbers().get(1);</pre>
     * 
     * <i>Return the base object</i>
     * <pre> Customer customer = xmlContext.getValueByXPath(customer, ".", aNamespaceResolver, Customer.class);
     * Customer customer = customer;
     * </pre>
     * 
     * @param <T> The return type of this method corresponds to the returnType parameter.
     * @param object  The XPath will be executed relative to this object.
     * @param xPath The XPath statement
     * @param namespaceResolver A NamespaceResolver containing the prefix/URI pairings from the XPath statement.
     * @param returnType The return type.
     * @return The object corresponding to the XPath or null if no result was found.
     */
    public abstract <T> T getValueByXPath(Object object, String xPath, NamespaceResolver namespaceResolver, Class<T> returnType);

    /**
     * INTERNAL:
     * Return true if any session held onto by this context has a document preservation
     * policy that requires unmarshalling from a Node.
     */
    public abstract boolean hasDocumentPreservation();

}