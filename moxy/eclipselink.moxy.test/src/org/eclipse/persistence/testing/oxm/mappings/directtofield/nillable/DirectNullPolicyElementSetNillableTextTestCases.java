/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - Sepetember 2012
package org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable;

/**
 * Same as super class except the XML document being read has some text
 * after the xsi:nil that should be ignored
 */
public class DirectNullPolicyElementSetNillableTextTestCases extends
        DirectNullPolicyElementSetNillableTestCases {

    protected final static String XML_READ_RESOURCE = //
        "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectNullPolicyElementSetNillableText.xml";

    public DirectNullPolicyElementSetNillableTextTestCases(String name)
            throws Exception {
        super(name);
        setControlDocument(XML_READ_RESOURCE);
        setWriteControlDocument(XML_RESOURCE);
    }

}
