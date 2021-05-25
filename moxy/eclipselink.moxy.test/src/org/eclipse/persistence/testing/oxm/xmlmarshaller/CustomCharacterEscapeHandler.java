/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import org.eclipse.persistence.oxm.CharacterEscapeHandler;

import java.io.IOException;
import java.io.Writer;

public class CustomCharacterEscapeHandler implements CharacterEscapeHandler {

    @Override
    public void escape(char[] buf, int start, int len, boolean isAttValue, Writer out) throws IOException {
        for (int i = start; i < start + len; i++) {
            char ch = buf[i];
            if (ch == '%') {
                out.write("*");
                continue;
            }
            out.write(ch);
        }
        String end = "";
    }
}
