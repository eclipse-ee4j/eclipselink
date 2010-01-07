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
 *     etang - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.sessionbean;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.rmi.PortableRemoteObject;

import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.sessionbean.EmployeeService;

/**
 * EJB 3 SessionBean tests using EclipseLink JPA
 * These tests can only be run in Oracle High Availability environment -  
 * a WebLogic server configured with Multi Data Sources to connect to Oracle RAC database
 * They read/write entity bean repeatly for 300 times, in the meantime, RAC database failures can be simulated,
 * to ensure no exceptions would be caused the RAC database failures.
 */
public class FindAllThread implements Runnable 
{
    public int threadNumber;
    public boolean succeeded = true;
    public boolean finished = false;
    public String relatedException = "";

    protected EmployeeService service;

    public FindAllThread()
    {
        super();
    }

    public FindAllThread(int threadNumber)
    {
        super();
        this.threadNumber = threadNumber;
    }

    public EmployeeService getEmployeeService() throws Exception {
        if (service == null) {
            Properties properties = new Properties();
            String url = System.getProperty("server.url");
            if (url != null) {
                properties.put("java.naming.provider.url", url);
            }
            Context context = new InitialContext(properties);

            try {
                service = (EmployeeService) PortableRemoteObject.narrow(context.lookup("EmployeeService#org.eclipse.persistence.testing.models.jpa.sessionbean.EmployeeService"), EmployeeService.class);
            } catch (NameNotFoundException notFoundException) {
                throw new Error("Lookup failed.", notFoundException);
            }
        }
        return service;
    }
        
    public void run()
    {
        int repeatTimes = 300;

        for (int i = 1; i <= repeatTimes; i++) {
            String prefix = "===Thread " + threadNumber + "===FindAll iteration " + i;
            try {
                System.out.println(prefix);
                List result = getEmployeeService().findAll();
                int employCount = 0;
                for (Iterator iterator = result.iterator(); iterator.hasNext(); ) {
                    Employee employee = (Employee)iterator.next();
                    employCount++;
                }
                if (employCount != 2) {
                    relatedException=prefix + "===The count is " + employCount +", Failed to find all employees";
                    succeeded=false;
                    break;
                }
                Thread.currentThread().sleep(3000);
            } catch (Exception e) {
                relatedException=prefix + e.toString();
                succeeded=false;
                break;
            }
        }
        System.out.println(relatedException);
        finished = true;
    }
}
