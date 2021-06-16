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
//     Denise Smith - February 2012
package org.eclipse.persistence.testing.jaxb.innerclasses.notincontext;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

public class TestObject {

    public String testString;

    public TestObject(){

    }

    public boolean equals (Object compareObject){
        if(compareObject instanceof TestObject){
            if(testString == null){
                return ((TestObject)compareObject).testString == null;
            }else{
                return testString.equals(((TestObject)compareObject).testString);
            }
        }
        return false;
    }

    public static class InnerClass{
        public InnerClass(){}


          @XmlElements({
                @XmlElement(name = "aaa", type = String.class)
            })
            protected List<Object> statements;

            public List<Object> getStatements() {
                if (statements == null) {
                    statements = new ArrayList<Object>();
                }
                return this.statements;
            }
    }
}
