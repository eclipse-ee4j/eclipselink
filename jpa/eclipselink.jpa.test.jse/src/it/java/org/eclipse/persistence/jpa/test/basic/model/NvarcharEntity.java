/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
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
//     02/19/2015 - Rick Curtis
//       - 458877 : Add national character support
package org.eclipse.persistence.jpa.test.basic.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "NvarcharEntity12")
public class NvarcharEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @Version
    int version;

    @Basic
    String dataField, dataField2;

    public NvarcharEntity() {
    }

    public NvarcharEntity(String data) {
        dataField = data;
    }

    public String getDataField() {
        return dataField;
    }

    public void setDataField(String dataField) {
        this.dataField = dataField;
    }

    public int getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

}
