/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.io.StringWriter;

import java.sql.Time;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;


public class PersonWithValueHolder {
    public long p_id;
    private String p_name;
    public ValueHolderInterface address;
    public ArrayList phoneNumbers;
    public Vector projects;
    public Time[] normalHours;

    public PersonWithValueHolder() {
        this.p_name = "";
        this.address = new ValueHolder();
        this.phoneNumbers = new ArrayList();
    }

    public long getId() {
        return p_id;
    }

    public void setId(long id) {
        this.p_id = id;
    }

    public String getName() {
        return p_name;
    }

    public void setName(String name) {
        this.p_name = name;
    }

    public ValueHolderInterface getAddressHolder() {
        return address;
    }

    public void setAddressHolder(ValueHolderInterface addressNew) {
        this.address = addressNew;
    }

    public Address getAddress() {
        return (Address)address.getValue();
    }

    public void setAddress(Address addressNew) {
        this.address.setValue(addressNew);
    }

    public void setProjects(Vector newProjects) {
        this.projects = newProjects;
    }

    public Vector getProjects() {
        return projects;
    }

    public void setPhones(ArrayList newPhones) {
        this.phoneNumbers = newPhones;
    }

    public ArrayList getPhones() {
        return phoneNumbers;
    }

    public Time[] buildNormalHours(Record row) {
        Time[] hours = new Time[2];
        // hours[0] = (Time) session.getPlatform().convertObject(row.get("START_TIME"), java.sql.Time.class);
        //hours[1] = (Time) session.getPlatform().convertObject(row.get("END_TIME"), java.sql.Time.class);	
        return hours;
    }

    public Time[] buildNormalHoursAgain(Record row, Session session) throws DescriptorException {
        Time[] hours = new Time[2];
        // hours[0] = (Time) session.getPlatform().convertObject(row.get("START_TIME"), java.sql.Time.class);
        // hours[1] = (Time) session.getPlatform().convertObject(row.get("END_TIME"), java.sql.Time.class);	
        return hours;
    }

    public Time[] getNormalHours() {
        return normalHours;
    }

    public Time getEndTime() {
        return getNormalHours()[1];
    }

    public Time getStartTime() {
        return getNormalHours()[0];
    }


    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write("Person: ");
        writer.write(getName());
        writer.write(" ");
        return writer.toString();
    }


}
