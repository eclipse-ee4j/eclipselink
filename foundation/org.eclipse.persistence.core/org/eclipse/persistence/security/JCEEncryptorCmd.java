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
//     Oracle - initial API and implementation
package org.eclipse.persistence.security;

import org.eclipse.persistence.internal.security.JCEEncryptor;

public class JCEEncryptorCmd {

    public static void main(String[] args) throws Exception {
        JCEEncryptorCmd encryptorCmd = new JCEEncryptorCmd();
        encryptorCmd.start(args);
    }

    public void start(String[] args) throws Exception {
        if (args.length < 2 || !args[0].equals("-ip")) {
            System.out.println("Usage is:\t \"java -cp eclipselink.jar org.eclipse.persistence.security.JCEEncryptorCmd -ip <old encrypted password>\"" +
                    "\nThis application internally decrypt old encrypted password used by some previous version EclipseLink and encrypt it by latest algorithm.");
        } else {
            JCEEncryptor jceEncryptor = new JCEEncryptor(false);
            System.out.println("Re-encrypted password is: " + jceEncryptor.encryptPassword(jceEncryptor.decryptPassword(args[1])));
        }
    }
}
