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
 *     Matt MacIvor = 2.1 - Initial contribution
 ******************************************************************************/
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>XPath based mapping is what allows an existing object model to be mapped 
 * to an existing XML schema.  The {@code @XmlPath} annotation is the means by 
 * which XPath based mapping is achieved.</p>
 * <b>Example 1 - Using {@code @XmlPath} to Add a Grouping Element</b>
 * <p>Sometimes grouping elements are added to your document to organise data.
 * JAXB has this concept for collection properties in the form of 
 * {@code @XmlElementWrapper}.  Here we'll use {@code @XmlPath} for 
 * non-collection properties. In this case we'll nest the billing/shipping 
 * address data within the "contact-info" element.</p>
 * <pre>
 * import javax.xml.bind.annotation.*;
 * import org.eclipse.persistence.oxm.annotations.XmlPath;
 * 
 * &#64;XmlRootElement 
 * &#64;XmlAccessorType(XmlAccessType.FIELD)
 * public class Customer {
 *     &#64;XmlPath("contact-info/billing-address")
 *     private Address billingAddress;
 *
 *     &#64;XmlPath("contact-info/shipping-address")
 *     private Address shippingAddress;
 * }
 * </pre>
 * This will produce XML like:
 * <xmp>
 * <customer>
 *     <contact-info>
 *         <billing-address>
 *             <street>1 Billing Street</street>
 *         </billing-address>
 *         <shipping-address>
 *             <street>2 Shipping Road</street>
 *         </shipping-address>
 *     </contact-info>
 * </customer>
 * </xmp>
 * <b>Example 2 - Using {@code @XmlPath} to Map by Position</b>
 * <p>Normally in JAXB elements with the same name must be mapped to a 
 * collection property.  Using the &#64;XmlPath extension you map non-collection
 * properties to a repeated element by index.</p>
 * <pre>
 * import javax.xml.bind.annotation.*;
 * import org.eclipse.persistence.oxm.annotations.XmlPath;
 * 
 * &#64;XmlRootElement 
 * &#64;XmlAccessorType(XmlAccessType.FIELD)
 * public class Customer {
 *     &#64;XmlPath("address[1]")
 *     private Address billingAddress;
 *
 *     &#64;XmlPath("address[2]")
 *     private Address shippingAddress;
 * }
 * </pre>
 * This will produce XML like:
 * <xmp>
 * <customer>
 *     <address>
 *         <street>1 Billing Street</street>
 *     </address>
 *     <address>
 *         <street>2 Shipping Road</street>
 *     </address> 
 * </customer>
 * </xmp>
 * <b>Example 3 - Using {@code @XmlPath} to Map Two Objects to the Same Node</b>
 * <p>We have seen how {@code @XmlPath} can be used to expand the structure by 
 * adding a grouping element. {@code @XmlPath} can also be used to collapse the 
 * structure by mapping two objects to the same node.</p>
 * <pre>
 * import javax.xml.bind.annotation.*;
 * import org.eclipse.persistence.oxm.annotations.XmlPath;
 * 
 * &#64;XmlRootElement @XmlAccessorType(XmlAccessType.FIELD)
 * public class Customer {
 *     &#64;XmlPath(".")
 *     private Address billingAddress;
 *
 *     private Address shippingAddress;
 * } 
 * </pre>
 * This will produce XML like:
 * <xmp>
 * <customer>
 *     <street>1 Billing Street</street>
 *     <shippingAddress>
 *         <street>2 Shipping Road</street>
 *     </shippingAddress>
 * </customer>
 * </xmp>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlPath {

    /**
     * <p>The XPath for this property.  A subset of the XPath specification may be 
     * used to specify mappings.  The following concepts are supported:</p>
     * <ul>
     * <li>Attribute - "&#64;id"</li>
     * <li>Element - "address"</li>
     * <li>Element by Position - "address[1]"</li>
     * <li>Element by Predicate - "address[@type='mailing']"</li>
     * <li>Element Text - "name/text()"</li>
     * <li>Text - "text()"</li>
     * <li>Self - "."</li>
     * <li>Combination - "personal-info/name[2]/text()"</li>
     * </ul>
     * <p>For namespace qualified nodes, the prefixes defined in the XmlNs
     * annotations can be used to qualify the XPath fragments.  Unqualified 
     * fragments will assumed to be in the namespace specified using
     * &#64;XmlSchema.</p>
     * <b>Example</b>
     * <p>Assuming the following namespace information has been set up using the
     * &#64;XmlSchema annotation:</p>
     * <pre>
     * &#64;XmlSchema(namespace = "http://www.example.org/FOO", 
     *            xmlns = {&#64;XmlNs(prefix="ns", namespaceURI="http://www.example.com/BAR")},
     *            elementFormDefault = XmlNsForm.QUALIFIED) 
     * package org.example;
     * 
     * import javax.xml.bind.annotation.*;
     * </pre>
     * <p>Then the following XPath:</p>
     * <pre>
     * &#64;XmlPath("contact-info/ns:address/&#64;id")
     * </pre>
     * <p>Will be qualified as:</p>
     * <ul>
     * <li>contact-info - in "http://www.example.org/FOO" namespace.</li>
     * <li>address - in "http://www.example.com/BAR" namespace.</li>
     * <li>&#64id - in no namespace.</li>
     * </ul>
     * @see javax.xml.bind.annotation.XmlSchema
     * @see javax.xml.bind.annotation.XmlNs
     */
    String value();

}