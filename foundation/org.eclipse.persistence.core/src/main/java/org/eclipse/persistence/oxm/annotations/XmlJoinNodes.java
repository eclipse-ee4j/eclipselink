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
// dmccann - September 14/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * XmlJoinNodes is a holder for multiple {@linkplain XmlJoinNode} annotations. This is used to specify the source to target
 * associations in a reference mapping with multiple keys. XmlJoinNodes will contain one {@linkplain XmlJoinNode} for each key. The
 * targets of the individual {@linkplain XmlJoinNode} annotations must be annotated with either
 * {@linkplain jakarta.xml.bind.annotation.XmlID} or {@linkplain XmlKey}.
 *
 * <p><b>Example:</b>
 * {@snippet :
 *  @XmlRootElement
 *  @XmlAccessorType(XmlAccessType.FIELD)
 *  public class Employee {
 *
 *      @XmlKey
 *      public String id;
 *
 *      @XmlKey
 *      public String department;
 *
 *      @XmlJoinNodes({
 *          @XmlJoinNode(xmlPath="manager/id/text()", referencedXmlPath="id/text()"),
 *          @XmlJoinNode(xmlPath="manager/dept/text()", referencedXmlPath="department/text()")
 *      })
 *      public Employee manager;
 *  }
 * }
 *
 * @see jakarta.xml.bind.annotation.XmlID
 * @see XmlJoinNode
 * @see XmlKey
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Repeatable(XmlElementsJoinNodes.class)
public @interface XmlJoinNodes {
    /**
     * An array of XmlJoinNode annotations.
     */
    XmlJoinNode[] value();
}
