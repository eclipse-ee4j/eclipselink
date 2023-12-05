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
package org.eclipse.persistence.tools.security;

import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.internal.security.JCEEncryptor;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

public final class JCEEncryptorCmd {

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    public static void main(String[] args) throws Exception {
        JCEEncryptorCmd encryptorCmd = new JCEEncryptorCmd();
        encryptorCmd.start(args);
    }

    private void start(String[] args) throws Exception {
        if (args.length < 2 || !args[0].equals("-ip")) {
            System.out.println(LoggingLocalization.buildMessage("encryptor_script_usage", null) +
                    "\n" + LoggingLocalization.buildMessage("encryptor_script_description", null));
        } else {
            JCEEncryptor jceEncryptor = new JCEEncryptor();
            System.out.println(LoggingLocalization.buildMessage("encryptor_script_output", new Object[]{jceEncryptor.encryptPassword(jceEncryptor.decryptPassword(args[1]))}));
        }
    }
}
