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
 * A <code>XmlNamedSubgraph</code> is a member element of a
 * <code>XmlNamedObjectGraph</code>.  The <code>XmlNamedSubgraph</code> is
 * only referenced from within an XmlNamedObjectGraph and can not be
 * referenced independently.  It is referenced by its <code>name</code>
 * from an <code>XmlNamedAttributeNode</code> element of the 
 * <code>XmlNamedObjectGraph</code>.
 *
 * @see XmlNamedObjectGraph
 * @see XmlNamedAttributeNode
 *
 * @since EclipseLink 2.5
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface XmlNamedSubgraph {
   /**
    * required: the name of the subgraph
    */
   String name();
 
   /**
    * optional: only required for inheritance or with ChoiceMappings 
    * to specify which of the possible targets this subgraph is to be
    * applied to. */
   Class type() default Object.class;
 
   /**
    * The list of properties to include in this graph
    */
   XmlNamedAttributeNode[]  attributeNodes();
}

