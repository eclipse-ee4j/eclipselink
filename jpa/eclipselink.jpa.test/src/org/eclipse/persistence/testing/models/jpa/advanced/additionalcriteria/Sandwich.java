/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     15/08/2011-2.3.1 Guy Pelletier 
 *       - 298494: JPQL exists subquery generates unnecessary table join
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria;

import static org.eclipse.persistence.config.CacheIsolationType.PROTECTED;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.AdditionalCriteria;
import org.eclipse.persistence.annotations.Cache;

@Entity
@Table(name="JPA_AC_SANDWICH")
@DiscriminatorValue("S")
@AdditionalCriteria("this.description like :SANDWICH_DESCRIPTION")
@PrimaryKeyJoinColumn(name="S_ID", referencedColumnName="F_ID")
public class Sandwich extends Food {
    @Column(name="S_NAME")
    public String name;
    
    @Column(name="S_DESCRIPTION")
    public String description;

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }
}
