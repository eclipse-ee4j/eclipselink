/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// Denise Smith - September 10 /2009
package org.eclipse.persistence.testing.jaxb.xmladapter.classlevel;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class ClassAtoClassBAdapter extends XmlAdapter<ClassA, ClassB> {

    @Override
    public ClassA marshal(ClassB classBObject) throws Exception {
        String s = classBObject.getSomeValue();
        ClassA classA = new ClassA();
        classA.setTheValue(s);
        return classA;
    }

    @Override
    public ClassB unmarshal(ClassA classAObject) throws Exception {
        String s = classAObject.getTheValue();
        ClassB classB = new ClassB();
        classB.setSomeValue(s);
        return classB;
    }


}
