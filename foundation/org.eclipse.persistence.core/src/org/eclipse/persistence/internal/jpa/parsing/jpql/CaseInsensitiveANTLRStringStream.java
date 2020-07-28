/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.parsing.jpql;

import org.eclipse.persistence.internal.libraries.antlr.runtime.ANTLRStringStream;

/**
 * This Stream is used when tokenizing JPQL queries
 * It overrides the look ahead operator to return the lower case version of the string
 * This is required because starting in ANTLR v3, case insensitivity is not provided
 * as an option in ANTLR and JPQL requires case insensitivity
 * @author tware
 *
 */
public class CaseInsensitiveANTLRStringStream extends ANTLRStringStream {

    /** Copy data in string to a local char array */
    public CaseInsensitiveANTLRStringStream(String input) {
        super(input);
    }

    /** This is the preferred constructor as no data is copied */
    public CaseInsensitiveANTLRStringStream(char[] data, int numberOfActualCharsInArray) {
        super(data, numberOfActualCharsInArray);
    }

    public int LA(int i) {
        int la = super.LA(i);
        return Character.toLowerCase(la);
    }

}
