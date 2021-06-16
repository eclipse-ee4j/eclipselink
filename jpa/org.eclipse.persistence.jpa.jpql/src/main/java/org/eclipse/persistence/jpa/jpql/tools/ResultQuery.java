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

/**
 * This is used to retrieve the new JPQL query when a content assist item needs to be insert at
 * a certain position.
 *
 * @see ContentAssistProposals#buildQuery(String, String, int, boolean)
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public interface ResultQuery {

    /**
     * Returns the position of the cursor within the new query.
     *
     * @return The updated position of the cursor
     */
    int getPosition();

    /**
     * Returns the new JPQL query after insertion of the choice.
     *
     * @return The JPQL with the content assist item inserted into the original query
     */
    String getQuery();
}
