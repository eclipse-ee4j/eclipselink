/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.resolver;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;

/**
 * This {@link Declaration} uses a database table as the "root" object.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public class TableDeclaration extends Declaration {

    /**
     * Creates a new <code>TableDeclaration</code>.
     */
    public TableDeclaration() {
        super();
    }

    /**
     * Returns the unquoted table name.
     *
     * @return The name of the table specified in the <code><b>TABLE</b></code> expression
     */
    public String getTableName() {
        return ExpressionTools.unquote(rootPath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getType() {
        return Type.TABLE;
    }
}
