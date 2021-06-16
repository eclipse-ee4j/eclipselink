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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface INewValueStateObjectBuilder extends IScalarExpressionStateObjectBuilder<INewValueStateObjectBuilder> {

    /**
     * Pushes the changes created by this builder to the state object.
     */
    void commit();

    /**
     * Creates a new expression representing the <code><b>NULL</b></code> keyword.
     *
     * @return This {@link INewValueStateObjectBuilder}
     */
    INewValueStateObjectBuilder NULL();

    /**
     * Creates the expression representing an identification variable.
     *
     * @param variable The identification variable
     * @return This {@link INewValueStateObjectBuilder builder}
     */
    INewValueStateObjectBuilder variable(String variable);
}
