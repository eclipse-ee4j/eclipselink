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
// dmccann - November 29/2010 - 2.3 - Initial implementation
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * XmlElementsJoinNodes is used in conjunction with {@linkplain jakarta.xml.bind.annotation.XmlElements}
 * in order to specify the key references for the targets of the {@linkplain jakarta.xml.bind.annotation.XmlElements}
 * annotation. There must be one {@linkplain XmlJoinNodes} for each {@linkplain jakarta.xml.bind.annotation.XmlElement}.
 * This is similar to using {@linkplain jakarta.xml.bind.annotation.XmlIDREF} with {@linkplain jakarta.xml.bind.annotation.XmlElements}
 * but allows customisation of the xpath for the source keys and allows for composite key relationships.
 *
 * <p><b>Example:</b>
 * {@snippet :
 *  @XmlRootElement
 *  public class Client {
 *
 *      ...
 *
 *    @XmlElements({
 *        @XmlElement(name="mail", type=Address.class),
 *        @XmlElement(name="phone", type=PhoneNumber.class)
 *    })
 *    @XmlElementsJoinNodes({
 *        @XmlJoinNodes({
 *            @XmlJoinNode(xmlPath="mail/@id", referencedXmlPath="@aid"),
 *            @XmlJoinNode(xmlPath="mail/type/text()", referencedXmlPath="@type")
 *        }),
 *        @XmlJoinNodes({
 *            @XmlJoinNode(xmlPath="phone/@id", referencedXmlPath="@pid"),
 *            @XmlJoinNode(xmlPath="phone/type/text()", referencedXmlPath="@type")
 *        })
 *    })
 *    public Object preferredContactMethod;
 *  }
 * }
 * @see XmlJoinNodes
 * @see XmlJoinNode
 * @see jakarta.xml.bind.annotation.XmlElement
 * @see jakarta.xml.bind.annotation.XmlElements
 * @see XmlKey
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlElementsJoinNodes {
    /**
     * An array of named XmlJoinNodes annotations.
     */
    XmlJoinNodes[] value();
}
