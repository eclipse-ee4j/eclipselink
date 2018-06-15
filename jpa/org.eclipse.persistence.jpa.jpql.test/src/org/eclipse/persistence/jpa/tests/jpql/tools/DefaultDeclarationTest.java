/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import org.eclipse.persistence.jpa.jpql.tools.DefaultJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;

/**
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public class DefaultDeclarationTest extends DeclarationTest {

    @Override
    protected JPQLQueryContext buildQueryContext() {
        return new DefaultJPQLQueryContext(jpqlGrammar);
    }
}
