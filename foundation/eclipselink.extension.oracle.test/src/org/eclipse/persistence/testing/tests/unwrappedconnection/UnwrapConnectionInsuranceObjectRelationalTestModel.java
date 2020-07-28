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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unwrappedconnection;

public class UnwrapConnectionInsuranceObjectRelationalTestModel extends UnwrapConnectionBaseTestModel{

    public UnwrapConnectionInsuranceObjectRelationalTestModel() {
        setDescription("This model tests oracle.sql.ARRAY  and oracle.sql.ARRAY using unwrapped connection.");
    }

    public void addTests(){
        //Add objectRelationTestModel to test Array, Struct type
        addTest(new org.eclipse.persistence.testing.tests.insurance.InsuranceObjectRelationalTestModel());
    }

}
