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

public abstract class AbstractNonELCharacterEscapeHandler implements com.sun.xml.bind.marshaller.CharacterEscapeHandler {

    public void escape(char[] buf, int start, int len, boolean isAttValue, Writer out) throws IOException {
        return;
    }

}
