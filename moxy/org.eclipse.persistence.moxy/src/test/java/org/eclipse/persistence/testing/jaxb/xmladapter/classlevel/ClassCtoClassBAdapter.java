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
package org.eclipse.persistence.testing.jaxb.xmladapter.classlevel;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

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
