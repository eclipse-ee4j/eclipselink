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
 * <p>XmlKey is used to mark a property as a key, to be referenced using a key-based mapping via {@code XmlJoinNode}. This is
 * similar to the {@code XmlID} annotation, but doesn't require the property be bound to the schema type ID. XmlKey is 
 * typically used in the composite key use case, since only 1 property can be annotated with the XmlID annotation.
 * </p> 
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
 * @see XmlJoinNodes
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface XmlKey {}