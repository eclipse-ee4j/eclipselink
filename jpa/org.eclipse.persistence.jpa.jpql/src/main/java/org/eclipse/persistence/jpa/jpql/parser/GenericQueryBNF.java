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
