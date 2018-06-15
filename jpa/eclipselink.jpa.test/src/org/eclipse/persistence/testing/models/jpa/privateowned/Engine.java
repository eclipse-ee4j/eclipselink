/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/19/09 dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.privateowned;

import java.util.*;
import javax.persistence.*;

import org.eclipse.persistence.annotations.*;

@Entity(name="PO_Engine")
@Table(name="CMP3_PO_ENGINE")
public class Engine {

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CMP3_PO_ENGINE_TABLE_GENERATOR")
    @TableGenerator(
        name="CMP3_PO_ENGINE_TABLE_GENERATOR",
        table="CMP3_PRIVATE_OWNED_SEQUENCE",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ENGINE_SEQ"
    )
    protected int id;

    protected long serialNumber;

    @OneToMany (cascade=CascadeType.ALL, mappedBy = "engine", targetEntity=SparkPlug.class)
    @PrivateOwned
    protected List<SparkPlug> sparkPlugs; // private-owned 1:M

    @Version
    protected int version;

    public Engine() {
        super();
        this.sparkPlugs = new ArrayList<SparkPlug>();
    }

    public Engine(long serialNumber) {
        this();
        this.serialNumber = serialNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public List<SparkPlug> getSparkPlugs() {
        return sparkPlugs;
    }

    public void setSparkPlugs(List<SparkPlug> sparkPlugs) {
        this.sparkPlugs = sparkPlugs;
    }

    public void addSparkPlug(SparkPlug sparkPlug) {
        getSparkPlugs().add(sparkPlug);
        sparkPlug.setEngine(this);
    }

    public void removeSparkPlug(SparkPlug sparkPlug) {
        getSparkPlugs().remove(sparkPlug);
        sparkPlug.setEngine(null);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
