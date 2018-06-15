/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model;

/**
 * The default implementation of a {@link IJPQLQueryFormatter}, which support creating a string
 * representation of a JPQL query written for JPA 1.0 and 2.0.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DefaultJPQLQueryFormatter extends AbstractJPQLQueryFormatter {

    /**
     * Creates a new <code>DefaultJPQLQueryFormatter</code>.
     *
     * @param style Determines how the JPQL identifiers are written out
     * @exception NullPointerException The IdentifierStyle cannot be <code>null</code>
     */
    public DefaultJPQLQueryFormatter(IdentifierStyle style) {
        super(style);
    }
}
