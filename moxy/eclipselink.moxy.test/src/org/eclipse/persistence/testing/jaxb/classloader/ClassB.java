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
 package org.eclipse.persistence.testing.jaxb.classloader;

import org.eclipse.persistence.testing.jaxb.employee.Employee;

public class ClassB {
    public String classBVariable;

    public String getClassBVariable() {
        return classBVariable;
    }

    public void setClassBVariable(String classBVariable) {
        this.classBVariable = classBVariable;
    }

    public boolean equals(Object object) {
        ClassB classBObject = ((ClassB)object);

        if(classBObject.getClassBVariable()== null && this.getClassBVariable() ==null){
            return true;
        }

        if(classBObject.getClassBVariable().equals(this.getClassBVariable())){
            return true;
        }
        return false;
    }
}
