/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * Helper class. Checks that javax.validation API is present.
 *
 * @author Dmitry Kornilov
 * @since 2.7.0
 */
public class BeanValidationChecker {

    /**
     * Returns true if javax.validation.api bundle is on the class path.
     */
    static boolean isBeanValidationPresent() {
        try {
            Class.forName("javax.validation.Validation").newInstance();
        } catch (ReflectiveOperationException e) {
            return false;
        }
        return true;
    }
}
