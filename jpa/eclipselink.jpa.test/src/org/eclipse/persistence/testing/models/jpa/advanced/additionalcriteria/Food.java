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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;

import static javax.persistence.InheritanceType.JOINED;
import static org.eclipse.persistence.config.CacheIsolationType.PROTECTED;

@Entity
@Table(name="JPA_AC_FOOD")
@Inheritance(strategy=JOINED)
@DiscriminatorValue("F")
@Cache(isolation=PROTECTED)
public class Food {
    @Id
    @GeneratedValue
    @Column(name="F_ID")
    public Integer id;
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
}
