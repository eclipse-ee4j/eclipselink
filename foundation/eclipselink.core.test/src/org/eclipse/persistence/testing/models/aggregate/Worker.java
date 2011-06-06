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
package org.eclipse.persistence.testing.models.aggregate;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * Insert the type's description here.
 * Creation date: (6/7/00 9:53:49 AM)
 * @author: Administrator
 */
public class Worker {
    public int id;
    public String fName;
    public String lName;
    public Job job;

    /**
     * Worker constructor comment.
     */
    public Worker() {
        super();
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 11:33:02 AM)
     * @return org.eclipse.persistence.testing.models.aggregate.Worker
     */
    public static Worker example1() {
        Worker worker1 = new Worker();
        worker1.setFName("Bruce");
        worker1.setLName("McGoose");
        worker1.setJob(Job.example1());
        return worker1;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:12:16 AM)
     */
    public String getFName() {
        return this.fName;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:12:16 AM)
     */
    public int getId() {
        return this.id;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:12:16 AM)
     */
    public Job getJob() {
        return this.job;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:12:16 AM)
     */
    public String getLName() {
        return this.lName;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:12:16 AM)
     */
    public void setFName(String theFName) {
        this.fName = theFName;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:12:16 AM)
     */
    public void setId(int theId) {
        this.id = theId;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:12:16 AM)
     */
    public void setJob(Job theJob) {
        this.job = theJob;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:12:16 AM)
     */
    public void setLName(String theLName) {
        this.lName = theLName;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/7/00 10:43:55 AM)
     * @return org.eclipse.persistence.tools.schemaframework.TableDefinition
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("WORKER");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("FNAME", String.class, 20);
        definition.addField("LNAME", String.class, 20);
        definition.addField("TITLE", String.class, 100);
        definition.addField("START_TIME", java.sql.Time.class);
        definition.addField("END_TIME", java.sql.Time.class);

        return definition;
    }
}
