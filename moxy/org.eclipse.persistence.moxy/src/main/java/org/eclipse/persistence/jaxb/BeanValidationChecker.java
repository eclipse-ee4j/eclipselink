/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Dmitry Kornilov - initial implementation
package org.eclipse.persistence.jaxb;

/**
 * Helper class. Checks that jakarta.validation API is present.
 *
 * @author Dmitry Kornilov
 * @since 2.7.0
 */
public class BeanValidationChecker {

    /**
     * Returns true if jakarta.validation.api bundle is on the class path.
     */
    static boolean isBeanValidationPresent() {
        try {
            // Only the presence of the type matters. Do not instantiate: as of Jakarta
            // Validation 4.0 the class is final with no public constructor, so reflective
            // instantiation fails and would report Bean Validation as absent.
            Class.forName("jakarta.validation.Validation");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }
}
