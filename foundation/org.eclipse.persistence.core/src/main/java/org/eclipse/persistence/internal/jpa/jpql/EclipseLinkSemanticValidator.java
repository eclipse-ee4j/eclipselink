/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.AbstractEclipseLinkSemanticValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkSemanticValidatorExtension;

/**
 * The EclipseLink runtime version of Hermes' semantic validator.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
final class EclipseLinkSemanticValidator extends AbstractEclipseLinkSemanticValidator {

    /**
     * Creates a new <code>EclipseLinkSemanticValidator</code>.
     *
     * @param queryContext The context used to query information about the JPQL query
     */
    EclipseLinkSemanticValidator(JPQLQueryContext queryContext) {

        super(new EclipseLinkSemanticValidatorHelper(queryContext),
              EclipseLinkSemanticValidatorExtension.NULL_EXTENSION);
    }
}
