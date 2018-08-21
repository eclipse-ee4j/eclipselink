/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
