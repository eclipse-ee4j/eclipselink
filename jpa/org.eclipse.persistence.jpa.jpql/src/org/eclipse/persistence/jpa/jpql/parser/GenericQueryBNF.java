/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * A generic {@link JPQLQueryBNF} can be used to manually create a new BNF without having to create
 * a concrete instance.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class GenericQueryBNF extends JPQLQueryBNF {

    /**
     * Creates a new <code>GenericQueryBNF</code>.
     *
     * @param id The unique identifier of this BNF
     */
    public GenericQueryBNF(String id) {
        super(id);
    }
}
