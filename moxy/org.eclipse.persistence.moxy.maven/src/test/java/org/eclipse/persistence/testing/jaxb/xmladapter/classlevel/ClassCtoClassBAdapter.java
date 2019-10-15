/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ClassCtoClassBAdapter extends XmlAdapter<ClassC, ClassB> {

    public ClassC marshal(ClassB classBObject) throws Exception {
        String s = classBObject.getSomeValue();
        ClassC classC= new ClassC();
        classC.setClassCValue(s);
        return classC;
    }

    public ClassB unmarshal(ClassC classCObject) throws Exception {
        String s = classCObject.getClassCValue();
        ClassB classB = new ClassB();
        classB.setSomeValue(s);
        return classB;
    }


}
