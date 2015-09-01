/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

/**
 * Helper class. Checks that javax.validation API is present.
 *
 * @author Dmitry Kornilov
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
