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
 * dmccann - September 14/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.oxm.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * <p><b>XmlJoinNode</b> is used in conjunction with {@code XmlKey} to specify a reference mapping. This is similar
 * to XmlID and XmlIDREF but allows for keys that are of types other than ID. When used with {@code XmlJoinNodes} can
 * be used to allow composite keys. The referencedXmlPath must match the xpath of a field on the target class that has 
 * been annotated with either XmlID or XmlKey.
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
 *     public String department;
 *     
       &#64;XmlJoinNode(xmlPath="manager/id/text()", referencedXmlPath="id/text()")
 *     public Employee manager;
 * }
 * </pre>
 * @see XmlKey
 * @see XmlJoinNodes
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlJoinNode {
    String xmlPath();
    String referencedXmlPath();
}
