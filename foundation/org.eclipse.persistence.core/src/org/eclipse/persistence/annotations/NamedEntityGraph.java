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
 *     Gordon Yorke - Initial development
 ******************************************************************************/

package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A NamedEntityGraph allows for the static definition of an entity graph. Once
 * defined this graph may be used as a template for persistence operations.
 * 
 * @see org.eclipse.persistence.jpa.NamedEntityGraph
 * 
 * @author Gordon Yorke
 * @since EclipseLink 2.4.2
 */

@Target({ TYPE })
@Retention(RUNTIME)
public @interface NamedEntityGraph {
    /**
     * (Optional) The name of the sub-graph. Defaults to the entity name of the
     * root entity.
     */
    String name() default "";

    /**
     * (Required) list of attributes that exist in this sub-graph.
     */
    NamedAttributeNode[] attributes();

    /**
     * (Optional) This is a list of ManagedComponents that exist in the
     * sub-graph. They will be referenced by name from other component
     * definitions.
     */

    NamedSubgraph[] subgraphs() default {};
    
    NamedSubgraph[] subclassSubgraphs() default {};
}