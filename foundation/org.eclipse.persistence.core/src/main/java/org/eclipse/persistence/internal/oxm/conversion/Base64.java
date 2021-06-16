/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm.conversion;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Convert to/from XML base64Binary.</p>
 */

public class Base64 {

    /**
     * Base64 constructor comment.
     */
    private Base64() {
    }

    /**
     * base64Binary data is likely to be long, and decoding requires
     * each character to be accessed twice (once for counting length, another
     * for decoding.)
     *
     * This method decodes the given byte[] using the java.util.Base64
     *
     * @param  data the base64-encoded data.
     * @return the decoded <var>data</var>.
     */
    public static byte[] base64Decode(byte[] data) {
        return java.util.Base64.getMimeDecoder().decode(data);
    }

    /**
     * This method encodes the given byte[] using java.util.Base64
     *
     * @param  data the data
     * @return the base64-encoded <var>data</var>
     */
    public static byte[] base64Encode(byte[] data) {
        return java.util.Base64.getEncoder().encode(data);
    }
}
