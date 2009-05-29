/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.metamodel;

import java.util.HashSet;
import java.util.Collection;
import javax.persistence.*;

import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;

@Entity(name="LocationMetamodel")
@Table(name="CMP3_MM_LOCATION")
public class Location implements java.io.Serializable{
    @Id
    @GeneratedValue(strategy=TABLE, generator="LOCATION_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="LOCATION_MM_TABLE_GENERATOR", 
        table="CMP3_MM_LOCATION_SEQ", 
        pkColumnName="SEQ_MM_NAME", 
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )
    @Column(name="LOCATION_ID")    
    private Integer id;
    
    @Version
    @Column(name="LOCATION_VERSION")
    private int version;
    
    public Location() {}

    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
}
