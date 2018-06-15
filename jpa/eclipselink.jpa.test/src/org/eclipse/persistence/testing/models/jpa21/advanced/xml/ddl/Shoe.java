/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     12/07/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl;

public class Shoe {
    protected Integer id;
    protected Integer size;
    protected String brand;
    protected String model;
    protected Runner runner;

    public Shoe() {}

    public String getBrand() {
        return brand;
    }

    public Integer getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public Runner getRunner() {
        return runner;
    }

    public Integer getSize() {
        return size;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
