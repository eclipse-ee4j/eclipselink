/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2023 IBM Corporation. All rights reserved.
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
//     11/28/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.internal.jpa;

import java.util.List;
import java.util.function.Consumer;

import org.eclipse.persistence.tools.schemaframework.TableValidationException;

/**
 * Schema validation failure.
 * {@link Consumer} of {@link TableValidationException} list to be called from schema validation
 * to pass all validation failures.
 */
final class ValidationFailure implements Consumer<List<TableValidationException>> {

    private static List<TableValidationException> validationResult;

    /**
     * Creates an instance of schema validation failure {@link Consumer}.
     */
    ValidationFailure() {
        validationResult = null;
    }

    @Override
    public void accept(List<TableValidationException> failures) {
        validationResult = failures;
    }

    /**
     * Return {@link List} of all {@link TableValidationException}s found by the schema validation process.
     *
     * @return schema validation failures
     */
    List<TableValidationException> result() {
        return validationResult;
    }

}
