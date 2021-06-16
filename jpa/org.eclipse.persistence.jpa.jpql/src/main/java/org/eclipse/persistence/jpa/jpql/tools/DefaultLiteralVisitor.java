/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools;

import org.eclipse.persistence.jpa.jpql.LiteralVisitor;

/**
 * This visitor traverses an {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression}
 * and retrieves the "literal" value. The literal to retrieve depends on the {@link
 * org.eclipse.persistence.jpa.jpql.LiteralType type}. The literal is basically a string value like
 * an identification variable name, an input parameter, a path expression, an abstract schema name, etc.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public class DefaultLiteralVisitor extends LiteralVisitor {

    /**
     * Creates a new <code>DefaultLiteralVisitor</code>.
     */
    public DefaultLiteralVisitor() {
        super();
    }
}
