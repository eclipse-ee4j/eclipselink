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
//  - rbarkhouse - 10 February 2012 - 2.3.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.sun.charescape;

import org.eclipse.persistence.testing.jaxb.xmlmarshaller.CharacterEscapeHandlerTestCases;

public class NonELCharacterEscapeHandlerTestCases extends CharacterEscapeHandlerTestCases {

    public NonELCharacterEscapeHandlerTestCases(String name) throws Exception {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        jaxbMarshaller.setProperty("com.sun.xml.bind.marshaller.CharacterEscapeHandler", new NonELCharacterEscapeHandler());
    }

}
