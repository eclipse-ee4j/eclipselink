/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
            Class.forName("jakarta.validation.Validation").newInstance();
        } catch (ReflectiveOperationException e) {
            return false;
        }
        return true;
    }
}
