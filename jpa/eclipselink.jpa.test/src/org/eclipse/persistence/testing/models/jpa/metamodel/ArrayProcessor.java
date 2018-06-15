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
//     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
//       of the Metamodel implementation for EclipseLink 2.0 release involving
//       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
package org.eclipse.persistence.testing.models.jpa.metamodel;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="ArrayProcessorMetamodel")
@Table(name="CMP3_MM_PROC")
public class ArrayProcessor extends Processor {

    private static final long serialVersionUID = -5324917445417350353L;

    public ArrayProcessor() {}

    /**
     * Even though the object model has this field declared here,
     * the relational model has it declared on the superclass
     * because we are using single table inheritance
     */
    private int speed;

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
