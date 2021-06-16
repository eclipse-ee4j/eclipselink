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
//     Denise Smith - September 2012
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable;

/**
 * Same as super class but the XML document being read has some additional
 * text after xsi:nil that should be ignored
 *
 */
public class CompositeObjectNillableTextTestCases extends CompositeObjectNillableNodeNullPolicyTestCases{

    protected final static String XML_READ_RESOURCE = //
        "org/eclipse/persistence/testing/oxm/mappings/compositeobject/nillable/CompositeObjectNillableText.xml";

    public CompositeObjectNillableTextTestCases(String name) throws Exception{
        super(name);
        setControlDocument(XML_READ_RESOURCE);
        setWriteControlDocument(XML_RESOURCE);
    }

}
