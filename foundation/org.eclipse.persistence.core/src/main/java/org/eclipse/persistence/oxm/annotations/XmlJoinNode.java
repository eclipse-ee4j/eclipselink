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
 * XmlJoinNode is used in conjunction with {@linkplain XmlKey} to specify a reference mapping. This is similar
 * to XmlID and XmlIDREF but allows for keys that are of types other than ID. When used with {@linkplain XmlJoinNodes} can
 * be used to allow composite keys. The "<em>referencedXmlPath</em>" must match the xpath of a field on the target class that has
 * been annotated with either XmlID or {@linkplain XmlKey}.
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
 *      public String department;
 *
        @XmlJoinNode(xmlPath="manager/id/text()", referencedXmlPath="id/text()")
 *      public Employee manager;
 *  }
 * }
 * @see jakarta.xml.bind.annotation.XmlID
 * @see XmlKey
 * @see XmlJoinNodes
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlJoinNode {
    String xmlPath();
    String referencedXmlPath();
}
