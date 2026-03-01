/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor = 2.1 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * XPath based mapping is what allows an existing object model to be mapped
 * to an existing XML schema. The {@literal @XmlPath} annotation is the means by
 * which XPath based mapping is achieved.
 *
 * <p><b>Example 1 - Using {@literal @XmlPath} to Add a Grouping Element</b>
 * <p>Sometimes grouping elements are added to your document to organise data. JAXB has this concept
 * for collection properties in the form of {@linkplain jakarta.xml.bind.annotation.XmlElementWrapper}.
 * Here we'll use {@literal @XmlPath} for non-collection properties.
 * In this case we'll nest the billing/shipping address data within the "contact-info" element.</p>
 * {@snippet :
 *  import jakarta.xml.bind.annotation.*;
 *  import org.eclipse.persistence.oxm.annotations.XmlPath;
 *
 *  @XmlRootElement
 *  @XmlAccessorType(XmlAccessType.FIELD)
 *  public class Customer {
 *      @XmlPath("contact-info/billing-address")
 *      private Address billingAddress;
 *
 *      @XmlPath("contact-info/shipping-address")
 *      private Address shippingAddress;
 *  }
 * }
 * This will produce XML like:
 * {@snippet lang="XML":
 *  <xmp>
 *    <customer>
 *      <contact-info>
 *        <billing-address>
 *          <street>1 Billing Street</street>
 *        </billing-address>
 *        <shipping-address>
 *          <street>2 Shipping Road</street>
 *        </shipping-address>
 *      </contact-info>
 *    </customer>
 *  </xmp>
 * }
 *
 * <p><b>Example 2 - Using {@literal @XmlPath} to Map by Position</b>
 * <p>Normally in JAXB elements with the same name must be mapped to a collection property.
 * Using the {@literal @XmlPath} extension you map non-collection properties to a repeated element by index.</p>
 * {@snippet :
 *  import jakarta.xml.bind.annotation.*;
 *  import org.eclipse.persistence.oxm.annotations.XmlPath;
 *
 *  @XmlRootElement
 *  @XmlAccessorType(XmlAccessType.FIELD)
 *  public class Customer {
 *      @XmlPath("address[1]")
 *      private Address billingAddress;
 *
 *      @XmlPath("address[2]")
 *      private Address shippingAddress;
 *  }
 * }
 * This will produce XML like:
 * {@snippet lang="XML":
 *  <xmp>
 *    <customer>
 *      <address>
 *        <street>1 Billing Street</street>
 *      </address>
 *      <address>
 *        <street>2 Shipping Road</street>
 *      </address>
 *    </customer>
 *  </xmp>
 * }
 *
 * <p><b>Example 3 - Using {@literal @XmlPath} to Map Two Objects to the Same Node</b>
 * <p>We have seen how {@literal @XmlPath} can be used to expand the structure by adding a grouping element.
 * {@literal @XmlPath} can also be used to collapse the structure by mapping two objects to the same node.</p>
 * {@snippet :
 *  import jakarta.xml.bind.annotation.*;
 *  import org.eclipse.persistence.oxm.annotations.XmlPath;
 *
 *  @XmlRootElement
 *  @XmlAccessorType(XmlAccessType.FIELD)
 *  public class Customer {
 *      @XmlPath(".")
 *      private Address billingAddress;
 *
 *      private Address shippingAddress;
 *  }
 * }
 * This will produce XML like:
 * {@snippet lang="XML":
 *  <xmp>
 *    <customer>
 *      <street>1 Billing Street</street>
 *      <shippingAddress>
 *        <street>2 Shipping Road</street>
 *      </shippingAddress>
 *    </customer>
 *  </xmp>
 * }
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(XmlPaths.class)
public @interface XmlPath {

    /**
     * The XPath for this property. A subset of the XPath specification may be
     * used to specify mappings. The following concepts are supported:
     * <ul>
     * <li>Attribute - "@id"</li>
     * <li>Element - "address"</li>
     * <li>Element by Position - "address[1]"</li>
     * <li>Element by Predicate - "address[@type='mailing']"</li>
     * <li>Element Text - "name/text()"</li>
     * <li>Text - "text()"</li>
     * <li>Self - "."</li>
     * <li>Combination - "personal-info/name[2]/text()"</li>
     * </ul>
     * <p>For namespace qualified nodes, the prefixes defined in the {@linkplain jakarta.xml.bind.annotation.XmlNs}
     * annotations can be used to qualify the XPath fragments. Unqualified
     * fragments will assumed to be in the namespace specified using
     * {@linkplain jakarta.xml.bind.annotation.XmlSchema}.</p>
     * <p><b>Example:</b>
     * <p>Assuming the following namespace information has been set up using the
     * {@linkplain jakarta.xml.bind.annotation.XmlSchema} annotation:</p>
     * {@snippet :
     *  @XmlSchema(namespace = "https://www.example.org/FOO",
     *             xmlns = {@XmlNs(prefix="ns", namespaceURI="https://www.example.com/BAR")},
     *             elementFormDefault = XmlNsForm.QUALIFIED)
     *  package org.example;
     *
     *  import jakarta.xml.bind.annotation.*;
     * }
     * <p>Then the following XPath:</p>
     * {@snippet :
     *  @XmlPath("contact-info/ns:address/@id")
     * }
     * <p>Will be qualified as:</p>
     * <ul>
     * <li>contact-info - in "https://www.example.org/FOO" namespace.</li>
     * <li>address - in "https://www.example.com/BAR" namespace.</li>
     * <li>@id - in no namespace.</li>
     * </ul>
     * @see jakarta.xml.bind.annotation.XmlSchema
     * @see jakarta.xml.bind.annotation.XmlNs
     */
    String value();

}
