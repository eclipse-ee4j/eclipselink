/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 10 February 2012 - 2.3.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

public class NonELCharacterEscapeHandlerTestCases extends CharacterEscapeHandlerTestCases {

    public NonELCharacterEscapeHandlerTestCases(String name) throws Exception {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        jaxbMarshaller.setProperty("com.sun.xml.bind.marshaller.CharacterEscapeHandler", new NonELCharacterEscapeHandler());
    }

}