/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     02/19/09 dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.privateowned;

import javax.persistence.*;

@Entity(name="PO_SparkPlug")
@Table(name="CMP3_PO_SPARK_PLUG")
public class SparkPlug {

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CMP3_PO_SPARK_PLUG_TABLE_GENERATOR")
    @TableGenerator(
        name="CMP3_PO_SPARK_PLUG_TABLE_GENERATOR",
        table="CMP3_PRIVATE_OWNED_SEQUENCE",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="SPARK_PLUG_SEQ"
    )
    protected Integer id;

    @ManyToOne
    protected Engine engine; // non-private owned M:1

    protected long serialNumber;

    @Version
    protected int version;

    public SparkPlug() {
        super();
    }

    public SparkPlug(long serialNumber) {
        this();
        this.serialNumber = serialNumber;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
