/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.jpaadvancedproperties;

import org.eclipse.persistence.security.Securable;

public class CustomizedEncryptor implements Securable {

    public static int encryptPasswordCounter = 0;
    public static int decryptPasswordCounter = 0;

    @Override
    public String encryptPassword(String pswd) {
        encryptPasswordCounter++;
        return pswd;
    }

    @Override
    public String decryptPassword(String encryptedPswd) {
        decryptPasswordCounter++;
        return encryptedPswd;
    }
}