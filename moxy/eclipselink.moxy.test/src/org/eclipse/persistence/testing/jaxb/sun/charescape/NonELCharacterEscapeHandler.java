/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 08 February 2012 - 2.3.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.sun.charescape;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.persistence.oxm.CharacterEscapeHandler;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.AbstractNonELCharacterEscapeHandler;

public class NonELCharacterEscapeHandler extends AbstractNonELCharacterEscapeHandler {

    // This handler doesn't actually escape any of the important characters.
    // For testing purposes only!
    public void escape(char[] buf, int start, int len, boolean isAttValue, Writer out) throws IOException {
        for (int i = start; i < start + len; i++) {
            char ch = buf[i];

            if (ch == '*') {
                out.write("[]");
                continue;
            }

            out.write(ch);
        }
    }

}
