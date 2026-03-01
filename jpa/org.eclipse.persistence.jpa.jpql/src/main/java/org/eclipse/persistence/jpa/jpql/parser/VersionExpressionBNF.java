/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The query BNF for an version expression.
 *
 * <div><b>BNF:</b> <code>VERSION(identification_variable)</code></div>
 *
 * @since 5.0
 * @author Radek Felcman
 */
@SuppressWarnings("nls")
public final class VersionExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "version_function";

    /**
     * Creates a new <code>VersionExpressionBNF</code>.
     */
    public VersionExpressionBNF() {
        super(ID);
    }

    @Override
    protected void initialize() {
        super.initialize();
        registerExpressionFactory(VersionExpressionFactory.ID);
    }
}
