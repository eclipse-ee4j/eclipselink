/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/19/2015 - Rick Curtis
 *       - 458877 : Add national character support
 *****************************************************************************/
package org.eclipse.persistence.jpa.test.basic.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

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
