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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="JPA_AC_RABBIT_FOOT")
public class RabbitFoot {
    @Id
    @GeneratedValue
    public int id;
    
    // FK, but user managed.
    @Column(name="RABBIT_ID")
    public int rabbitId;
    
    @Column(name="CAPTION")
    public String caption;

    public int getId() {
        return id;
    }

    public int getRabbitId() {
        return rabbitId;
    }

    public String getCaption() {
        return caption;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRabbitId(int rabbitId) {
        this.rabbitId = rabbitId;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
