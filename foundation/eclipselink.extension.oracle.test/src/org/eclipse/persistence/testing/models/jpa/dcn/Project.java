/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.dcn;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name="DCN_PROJECT")
@Inheritance(strategy=InheritanceType.JOINED)
public class Project implements Serializable {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Integer id;

    @Column(name="NAME")
    private String name;
    
    @Version
    @Column(name="VERSION")
    private Integer version;
     
    public Project() {}

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version; 
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public boolean equals(Object object) {
        if (object instanceof Project) {
            if (((Project)object).getId() != null && ((Project)object).getId().equals(this.id)) {
                return true;
            }
        }
        return super.equals(object);
    }
    
}

