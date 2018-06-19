/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - November 29/2010 - 2.3 - Initial implementation
package org.eclipse.persistence.oxm.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * XmlElementsJoinNodes is used in conjunction with {@code XmlElements} in order to specify the key references for the
 * targets of the XmlElements annotation. There must be one XmlJoinNodes for each XmlElement This is similar to using {@code XmlIDREF}
 * with {@code XmlElements} but allows customisation of the xpath for the source keys and allows for composite key
 * relationships.
 *
 * <p><b>Example:</b></p>
 * <pre>
 * &#64;XmlRootElement
 * public class Client {
 *
 *   ...
 *
 *   &#64;XmlElements({
 *       &#64;XmlElement(name="mail", type=Address.class),
 *       &#64;XmlElement(name="phone", type=PhoneNumber.class)
 *   })
 *   &#64;XmlElementsJoinNodes({
 *       &#64;XmlJoinNodes({
 *           &#64;XmlJoinNode(xmlPath="mail/@id", referencedXmlPath="@aid"),
 *           &#64;XmlJoinNode(xmlPath="mail/type/text()", referencedXmlPath="@type")
 *       }),
 *       &#64;XmlJoinNodes({
 *           &#64;XmlJoinNode(xmlPath="phone/@id", referencedXmlPath="@pid"),
 *           &#64;XmlJoinNode(xmlPath="phone/type/text()", referencedXmlPath="@type")
 *       })
 *   })
 *   public Object preferredContactMethod;
 * </pre>
 *
 *   @see XmlJoinNodes
 *   @see XmlJoinNode
 *   @see javax.xml.bind.annotation.XmlElements XmlElements
 *   @see XmlKey
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlElementsJoinNodes {
    XmlJoinNodes[] value();
}
