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
// Denise Smith - September 10 /2009
package org.eclipse.persistence.testing.jaxb.xmladapter.packagelevel.adapters;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class ClassAtoClassBAdapter extends XmlAdapter<ClassA, ClassB> {

    public ClassA marshal(ClassB classBObject) throws Exception {
        String s = classBObject.getSomeValue();
        ClassA classA = new ClassA();
        classA.setTheValue(s);
        return classA;
    }

    public ClassB unmarshal(ClassA classAObject) throws Exception {
        String s = classAObject.getTheValue();
        ClassB classB = new ClassB();
        classB.setSomeValue(s);
        return classB;
    }


}
