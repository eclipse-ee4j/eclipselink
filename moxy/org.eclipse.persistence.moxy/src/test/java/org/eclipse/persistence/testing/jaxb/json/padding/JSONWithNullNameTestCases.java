/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - November 2012
package org.eclipse.persistence.testing.jaxb.json.padding;

import org.eclipse.persistence.oxm.JSONWithPadding;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;
import org.eclipse.persistence.testing.jaxb.json.numbers.NumberHolder;

public class JSONWithNullNameTestCases extends JAXBWithJSONTestCases{

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/padding/padding_default.json";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/padding/padding.xml";

    public JSONWithNullNameTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Simple.class});
        setControlJSON(JSON_RESOURCE);
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        Simple sample = new Simple();
        sample.id = "1111";
        sample.name = "theName";

        JSONWithPadding test = new JSONWithPadding(sample, null);
        return test;
    }

    public boolean isUnmarshalTest (){
        return false;
    }

    public void testJSONMarshalToBuilderResult() throws Exception{
    }

    public void testJSONMarshalToGeneratorResult() throws Exception{
    }

}
