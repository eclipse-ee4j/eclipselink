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

import java.util.ArrayList;
import java.util.List;

public class ClassC {
    public String classCValue;

    public ClassC(){
    }

    public String getClassCValue() {
        return classCValue;
    }

    public void setClassCValue(String classCValue) {
        this.classCValue = classCValue;
    }

    public boolean equals(Object obj){
        if(!(obj instanceof ClassC)){
            return false;
        }
        ClassC classCObj = (ClassC)obj;

        if(classCValue == null){
            if(classCObj.getClassCValue() != null){
                return false;
            }
        }else{
            if(classCObj.getClassCValue() == null){
                return false;
            }
            if(!getClassCValue().equals(classCObj.getClassCValue())){
                return false;
            }
        }

        return true;
    }
}
