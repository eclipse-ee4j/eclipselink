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
package org.eclipse.persistence.testing.models.inheritance;

import org.eclipse.persistence.indirection.*;

public class CompanyWorker extends Worker {
    public ValueHolderInterface company;

    public CompanyWorker() {
        super();

        company = new ValueHolder();
    }

    public static CompanyWorker example1(Company company) {
        CompanyWorker worker = new CompanyWorker();

        worker.setCompany(company);
        worker.setName("Worker 1");

        return worker;
    }

    public static CompanyWorker example2(Company company) {
        CompanyWorker worker = new CompanyWorker();

        worker.setCompany(company);
        worker.setName("Worker 2");

        return worker;
    }

    public static CompanyWorker example3(Company company) {
        CompanyWorker worker = new CompanyWorker();

        worker.setCompany(company);
        worker.setName("Worker 3");

        return worker;
    }

    public static CompanyWorker example4(Company company) {
        CompanyWorker worker = new CompanyWorker();

        worker.setCompany(company);
        worker.setName("Worker 4");

        return worker;
    }

    public static CompanyWorker example5(Company company) {
        CompanyWorker worker = new CompanyWorker();

        worker.setCompany(company);
        worker.setName("Worker 5");

        return worker;
    }

    public ValueHolderInterface getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company.setValue(company);
    }
}
