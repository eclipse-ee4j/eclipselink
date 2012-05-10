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
 *     05/1/2009-2.0 Guy Pelletier/David Minsky
 *       - 249033: JPA 2.0 Orphan removal
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.orphanremoval;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import static javax.persistence.GenerationType.TABLE;

@Entity(name="OR_SparkPlug")
@Table(name="JPA_OR_SPARK_PLUG")
public class SparkPlug {
    @Id
    @GeneratedValue(strategy=TABLE, generator="JPA_OR_SPARK_PLUG_TABLE_GENERATOR")
    @TableGenerator(
        name="JPA_OR_SPARK_PLUG_TABLE_GENERATOR", 
        table="JPA_ORPHAN_REMOVAL_SEQUENCE",
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="SPARK_PLUG_SEQ"
    )
    protected int id;
    
    @ManyToOne
    protected Engine engine; // non-orphanRemoval M:1
    
    protected long serialNumber;

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

    public int getId() {
        return id;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String toString() {
        return "SparkPlug ["+ id +"]";
    }
}
