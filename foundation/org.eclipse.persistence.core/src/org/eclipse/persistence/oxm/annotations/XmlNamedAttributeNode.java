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
 *     Matt MacIvor - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A <code>XmlNamedAttributeNode</code> is a member element of a
 * <code>XmlNamedObjectGraph</code>.
 *
 * @see XmlNamedObjectGraph
 * @see XmlNamedSubgraph
 *
 * @since EclipseLink 2.5
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface XmlNamedAttributeNode {
    /**
     * required: the name of the property
     */
    String value();
 
    /** optional: if this property referenced another JAXB Object, 
     *  specify the name of the object graph to use for that nested object. 
     *   By default, the full object will be read.
     */ 
    String subgraph() default "";
}
     