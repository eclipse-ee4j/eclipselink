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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkTraverseParentVisitor;

/**
 * This class is used to make sure the parent class {@link AbstractEclipseLinkTraverseParentVisitor}
 * implements all the methods defined on the interface {@link EclipseLinkTraverseParentVisitor}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
final class TestAbstractEclipseLinkTraverseParentVisitor extends AbstractEclipseLinkTraverseParentVisitor {

    private TestAbstractEclipseLinkTraverseParentVisitor() {
        super();
    }

    /**
     * IMPORTANT: NO METHODS TO IMPLEMENT. SHOULD ALL BE IMPLEMENTED BY AbstractEclipseLinkTraverseParentVisitor.
     */
}
