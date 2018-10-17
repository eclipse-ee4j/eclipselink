/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 */
// Contributors:Suresh Balakrishnan
package org.eclipse.persistence.testing.models.employee.domain;

public class Contact {
    private long id;

    public void setId(long id) {
        this.id = id;
    }

    public Customer customer;

    public TkeEmployee employee;

    public int daysVisted;

    @Override
    public String toString() {
        return customer + " <-> " + employee + ", daysVisted=" + daysVisted;
    }
}
