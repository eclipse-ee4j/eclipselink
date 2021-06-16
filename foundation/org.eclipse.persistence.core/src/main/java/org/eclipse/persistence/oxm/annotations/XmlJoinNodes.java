/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// dmccann - September 14/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.oxm.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * <p><b>XmlJoinNodes</b> is a holder for multiple {@code XmlJoinNode} annotations. This is used to specify the source to target
 * associations in a reference mapping with multiple keys. XmlJoinNodes will contain one XmlJoinNode for each key. The
 * targets of the individual XmlJoinNode annotations must be annotated with either {@code XmlID} or {@code XmlKey}.
 *
 * <p><b>Example:</b>
 * <pre>
 * &#64;XmlRootElement
 * &#64;XmlAccessorType(XmlAccessType.FIELD)
 * public class Employee {
 *
 *     &#64;XmlKey
 *     public String id;
 *
 *     &#64;XmlKey
 *     public String department;
 *
 *     &#64;XmlJoinNodes({
 *         &#64;XmlJoinNode(xmlPath="manager/id/text()", referencedXmlPath="id/text()"),
 *         &#64;XmlJoinNode(xmlPath="manager/dept/text()", referencedXmlPath="department/text()")
 *     })
 *     public Employee manager;
 * }
 * </pre>
 *
 * @see XmlJoinNode
 * @see XmlKey
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlJoinNodes {
    XmlJoinNode[] value();
}
