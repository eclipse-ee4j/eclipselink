/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - February 25, 2013
package org.eclipse.persistence.testing.jaxb.interfaces.choice;

import java.util.Properties;

import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;


@XmlAccessorType(XmlAccessType.FIELD)
public class MyObject implements MyInterface{

     /*
    * this part of the code is irrelevant, but shows that the issue
     * does not reproduce with simple types..
     */
    private byte test = 0x01;
    private Byte test2 = 0x01;

    private Properties props = new Properties();
    private Properties anothernameproperties = new Properties();

    public Properties getProperties() {
        return props;
    }

     public void setProperties(Properties properties) {
         this.props = properties;
    }

    @Override
    public byte getTest() {
        return test;
    }

    @Override
    public void setTest(byte test) {
        this.test = test;
    }

    public Byte getTest2() {
        return test2;
    }

    public void setTest2(Byte test2) {
        this.test2 = test2;
    }

    public Properties getAnothernameproperties() {
        return anothernameproperties;
    }

    public void setAnothernameproperties(Properties anothernameproperties) {
        this.anothernameproperties = anothernameproperties;
    }

    public boolean equals(Object obj){
        if(obj instanceof MyObject){
            MyObject compare = (MyObject)obj;
            if(test != compare.test || !test2.equals(compare.test2)){
                return false;
            }
            return this.props.equals(compare.props) && anothernameproperties.equals(compare.anothernameproperties);
        }
        return false;
    }

}
