/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.validation;

//Created by Ian Reid
//Date: Mar 21, 2k3
//used in test for TL Error-168

public class ClassWithProblemConstructor {
    public ClassWithProblemConstructor() throws Exception {
        throw new Exception();
    }

}
