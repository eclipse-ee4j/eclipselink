/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     04/07/2012-2.5 Guy Pelletier
//       - 384275: Customizer from a mapped superclass is not overridden by an entity customizer
package org.eclipse.persistence.testing.models.jpa21.advanced.inheritance;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;

import java.io.Serializable;

@Entity
@Table(name="JPA21_COMPUTER")
@Inheritance(strategy=InheritanceType.JOINED)
@Customizer(Computer.class)
public class Computer extends ComputerSuperclass implements Serializable, DescriptorCustomizer {
    private ComputerPK computerPK;

    public Computer() {
    }

    public void customize(ClassDescriptor descriptor) {
        descriptor.getInheritancePolicy().setShouldOuterJoinSubclasses(true);
    }

    public Computer(ComputerPK computerPK) {
        this.computerPK = computerPK;
    }

    @EmbeddedId
    public ComputerPK getComputerPK() {
        return computerPK;
    }

    public void setComputerPK(ComputerPK computerPK) {
        this.computerPK = computerPK;
    }
}
