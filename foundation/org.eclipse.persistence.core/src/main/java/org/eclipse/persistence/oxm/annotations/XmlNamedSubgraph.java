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
//     Matt MacIvor - 2.5 - initial implementation
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

