/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.indirection.IndirectContainer;

//used for Method.class

//needed for IndirectContainer.class

//Created by Ian Reid
//Date: Mar 25, 2k3
//class used in TL-ERROR 167 test -- need no valid public constructor

public class ClassWithoutConstructor implements IndirectContainer {
    //used for TL-172 testing (work in progress)

    private ClassWithoutConstructor() throws java.lang.NoSuchMethodException {
        Class[] parmClasses = { Integer.class };
        ClassWithoutConstructor.class.getDeclaredMethod("invalidMethod", parmClasses);
    }

    public void invalidMethod(String row) {

    }

    public void setValueHolder(org.eclipse.persistence.indirection.ValueHolderInterface input) {

    }

    public org.eclipse.persistence.indirection.ValueHolderInterface getValueHolder() {
        return null;
    }

    public boolean isInstantiated() {
        return false;
    }

}
