/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.internal.security.Securable;


public class CustomEncryption implements Securable {

    public String encryptPassword(String pswd) {
        return "123" + pswd;
    }

    public String decryptPassword(String encryptedPswd) {
        return encryptedPswd.substring(3);
    }
}

