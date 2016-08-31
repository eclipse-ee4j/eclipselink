/*******************************************************************************
 * Copyright (c) 2012, 2016 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 *     08/29/2016 Jody Grassel
 *       - 500441: Eclipselink core has System.getProperty() calls that are not potentially executed under doPriv()
 ******************************************************************************/
package org.eclipse.persistence.internal.core.helper;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

public class CoreHelper {

    /** Store CR string, for some reason \n is not platform independent. */
    protected final static String CR;

    static {
        // bug 2756643
        CR = PrivilegedAccessHelper.shouldUsePrivilegedAccess() ?
                AccessController.doPrivileged(new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        return System.getProperty("line.separator");
                    }
                })
                : System.getProperty("line.separator");
    }

    /**
     * Return a string containing the platform-appropriate
     * characters for carriage return.
     */
    public static String cr() {
        // bug 2756643
        return CR;
    }

}
