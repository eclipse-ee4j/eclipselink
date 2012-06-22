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
package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.io.*;
import javax.persistence.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.InheritanceType.*;

@Entity
@Table(name="CMP3_TIRE")
@Inheritance(strategy=SINGLE_TABLE)
@DiscriminatorColumn(name="TIRE_TYPE")
@DiscriminatorValue("Normal")
/**
 * Note Y is currently not used, but was added as a test for bug 336133.  Please do not remove,
 * but feel free to add meaning to Y.
 */
public class TireInfo<Y> extends TireInfoMappedSuperclass implements Serializable {
    protected Integer id;

    public TireInfo() {}

    @Transient
    private Y variable;
    

    @Id
    @GeneratedValue(strategy=TABLE, generator="TIRE_TABLE_GENERATOR")
    @TableGenerator(
        name="TIRE_TABLE_GENERATOR", 
        table="CMP3_INHERITANCE_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="TIRE_SEQ")
    @Column(name="ID")
    public Integer getId() {
        return id;
    }

	public void setId(Integer id) { 
        this.id = id; 
    }
}
