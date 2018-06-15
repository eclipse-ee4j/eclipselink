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
//     Denise Smith - February 2012
package org.eclipse.persistence.testing.jaxb.innerclasses.notincontext;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

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
