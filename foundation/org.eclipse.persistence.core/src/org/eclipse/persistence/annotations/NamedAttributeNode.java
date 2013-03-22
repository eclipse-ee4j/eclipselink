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
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A component is a member attribute of an EntityGraph.
 * 
 * @see org.eclipse.persistence.jpa.NamedEntityGraph
 * @see org.eclipse.persistence.jpa.NamedSubgraph
 * @see org.eclipse.persistence.jpa.NamedAttributeNode
 * 
 * @author Gordon Yorke
 * @since EclipseLink 2.4.2
 */
@Target({})
@Retention(RUNTIME)
public @interface NamedAttributeNode {
    /**
     * (Required) the name of the attribute that must be in the sub-graph.
     */
    String value();

    /**
     * (Optional) if this attribute references a managed type that has its own
     * components then this refers to that subgraph definition.
     */
    String subgraphName() default "";
    String keySubgraph() default "";
}