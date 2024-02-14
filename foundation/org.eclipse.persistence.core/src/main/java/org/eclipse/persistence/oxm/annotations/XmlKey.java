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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * XmlKey is used to mark a property as a key, to be referenced using a key-based mapping via {@linkplain XmlJoinNode}. This is
 * similar to the {@linkplain jakarta.xml.bind.annotation.XmlID} annotation, but doesn't require the property be bound to the schema type ID. XmlKey is
 * typically used in the composite key use case, since only 1 property can be annotated with the {@linkplain jakarta.xml.bind.annotation.XmlID} annotation.
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
 * @see XmlJoinNodes
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlKey {}
