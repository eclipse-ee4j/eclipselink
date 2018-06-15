/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2012, 2015 Oracle, Daryl Davis. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     09/10/2008-2.4.1 Daryl Davis
//       - 386939: @ManyToMany Map<Entity,Entity> unidirectional reverses Key and Value fields on Update
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="JPAMASTER")
public class Master implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    public String id;
    public String name;

    //Associations
    @ManyToMany
    public Map<DetailEntity, ValueEntity> details;

    public Master () {
    }

    public Master (String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Map<DetailEntity, ValueEntity> getDetails() {
        return details;
    }

    public void setDetails(Map<DetailEntity, ValueEntity> details) {
        this.details = details;
    }
}
