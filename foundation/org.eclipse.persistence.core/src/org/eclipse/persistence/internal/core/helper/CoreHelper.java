/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5 - initial implementation
//     08/29/2016 Jody Grassel
//       - 500441: Eclipselink core has System.getProperty() calls that are not potentially executed under doPriv()
package org.eclipse.persistence.internal.core.helper;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

public class CoreHelper {

    /** Store CR string, for some reason \n is not platform independent. */
    protected static String CR = null;

    /**
     * Return a string containing the platform-appropriate
     * characters for carriage return.
     */
    public static String cr() {
        // bug 2756643
        if (CR == null) {
            CR = PrivilegedAccessHelper.getSystemProperty("line.separator");
        }
        return CR;
    }

}
