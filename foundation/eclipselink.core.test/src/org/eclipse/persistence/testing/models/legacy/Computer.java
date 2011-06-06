/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.legacy;

import java.sql.Timestamp;
import java.io.*;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.sessions.*;

public class Computer implements Serializable {
    public Timestamp creationTimestamp;
    public int creationTimestampMillis;
    public String description;
    public Employee employee;

    public static Computer example1(Employee employee) {
        Computer example = new Computer();

        example.setDescription("Sun Workstation");
        example.setEmployee(employee);
        return example;
    }

    public static Computer example2(Employee employee) {
        Computer example = new Computer();

        example.setDescription("PC 486");
        example.setEmployee(employee);
        return example;
    }

    public static Computer example3(Employee employee) {
        Computer example = new Computer();

        example.setDescription("IBM Main Frame");
        example.setEmployee(employee);
        return example;
    }

    public static Computer example4(Employee employee) {
        Computer example = new Computer();

        example.setDescription("IBM Main Frame");
        example.setEmployee(employee);
        return example;
    }

    public static Computer example5(Employee employee) {
        Computer example = new Computer();

        example.setDescription("IBM Main Frame");
        example.setEmployee(employee);
        ;
        return example;
    }

    public static Computer example6(Employee employee) {
        Computer example = new Computer();

        example.setDescription("IBM Main Frame");
        example.setEmployee(employee);
        ;
        return example;
    }

    public void prepareForInsert(DescriptorEvent event) {
        Session session = event.getSession();
        long millis = System.currentTimeMillis();
        this.creationTimestamp = new Timestamp(millis);
        if (session.getLogin().getPlatform().isAccess() || session.getLogin().getPlatform().isSQLServer() ||
                session.getLogin().getPlatform().isOracle() || session.getLogin().getPlatform().isSybase() || session.getLogin().getPlatform().isSQLAnywhere() ||
                session.getLogin().getPlatform().isAttunity() || session.getLogin().getPlatform().isMySQL() || 
                session.getLogin().getPlatform().isTimesTen() || session.getLogin().getPlatform().isSymfoware()) {
            // Oracle does not support millis, Sybase stores them only within 1-2 millis...
            //MySQL does not support millis as of 5.0
            this.creationTimestampMillis = this.creationTimestamp.getNanos();
            this.creationTimestamp.setNanos(0);
        }
        try {
            Thread.sleep(50);
            // Symfoware does not support nanos and millis.
            // Need to wait at least a second to prevent unique key constraint
            // violation at next insert
            if (session.getLogin().getPlatform().isSymfoware()) Thread.sleep(1000);
        } catch (InterruptedException exception) {
        }
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public void setEmployee(Employee anEmployee) {
        employee = anEmployee;
    }
}
