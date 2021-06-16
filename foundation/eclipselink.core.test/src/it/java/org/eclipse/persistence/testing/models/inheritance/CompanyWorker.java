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
