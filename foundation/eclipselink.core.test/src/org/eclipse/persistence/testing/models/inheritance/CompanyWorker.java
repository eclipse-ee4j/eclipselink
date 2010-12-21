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
