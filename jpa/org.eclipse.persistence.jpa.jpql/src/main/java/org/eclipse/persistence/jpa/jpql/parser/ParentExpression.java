/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation.
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
//     21/07/2024: Ondro Mihalyi - implicit variable in sub-expressions
package org.eclipse.persistence.jpa.jpql.parser;

import java.util.List;

public interface ParentExpression extends Expression {

    /**
     * Whether Hermes parser should automatically add missing "this" prefix into where field variables if it doesn't exist.
     * @return {@code boolean} {@code true} - if this aliases should be generated , {@code false} - if not
     */
    boolean isGenerateImplicitThisAlias();

    void setGenerateImplicitThisAlias(boolean generateImplicitThisAlias);

    /**
     * Get list of {@code IdentificationVariable} where this alias should be added.
     */
    List<IdentificationVariable> getIdentificationVariablesWithoutAlias();
}
