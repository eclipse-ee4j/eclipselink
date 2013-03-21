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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <b>Purpose:</b> Used to define the boundaries for a marshal or unmarhsal
 * operation. 
 * 
 * @author mmacivor
 * @since EclipseLink 2.5
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface XmlNamedObjectGraph {
    /**
     * The name of this object graph. Defaults to the name of the class
     */
    String name();
 
    /**
     * The list of properties to be marshalled/unmarshalled for this graph.
     */
    XmlNamedAttributeNode[] attributeNodes();
 
    /**
     * Optional: a list of named subgraphs that are referenced 
     * from the property entries.
     */
    XmlNamedSubgraph[] subgraphs() default {};
 
    /**
     * Optional: a list of named subgraphs for any subclasses
     * of this class.
     */
    XmlNamedSubgraph[] subclassSubgraphs() default {};
}