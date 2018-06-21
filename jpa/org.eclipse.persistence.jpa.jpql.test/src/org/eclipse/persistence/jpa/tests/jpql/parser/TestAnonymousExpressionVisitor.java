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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.parser.AnonymousExpressionVisitor;

/**
 * This class is used to make sure the parent class {@link AnonymousExpressionVisitor} implements
 * all the methods defined on the interface {@link ExpressionVisitor}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
final class TestAnonymousExpressionVisitor extends AnonymousExpressionVisitor {

    private TestAnonymousExpressionVisitor() {
        super();
    }

    /**
     * IMPORTANT: NO METHODS TO IMPLEMENT. SHOULD ALL BE IMPLEMENTED BY AnonymousExpressionVisitor.
     */
}
