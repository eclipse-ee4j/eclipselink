/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     03/08/2010-2.1 Michael O'Brien
//       - 300051: JPA 2.0 Metamodel processing requires EmbeddedId validation moved higher from
//                      EmbeddedIdAccessor.process() to MetadataDescriptor.addAccessor() so we
//                      can better determine when to add the MAPPED_SUPERCLASS_RESERVED_PK_NAME
//                      temporary PK field used to process MappedSuperclasses for the Metamodel API
//                      during MetadataProject.addMetamodelMappedSuperclass()
package org.eclipse.persistence.testing.models.jpa.metamodel;

import static javax.persistence.CascadeType.ALL;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Use Case: IdClass identifiers declared across multiple mappedSuperclasses in an inheritance hierarchy.
 * Note: The following MappedSuperclass defines 3 of 4 of the Id fields as part of the IdClass MSIdClassPK.
 * The 4th field is declared on the subclass.
 * The IdClass annotation can go on the subclass or the entity but not on this root.
 * As long as resolution of all fields in the IdClass are available - the configuration is good.
 */
@Entity(name="MultiCoreCPUMetamodel")
@Table(name="CMP3_MM_MULTICORECPU")
public class MultiCoreCPU extends CPU {

    private Set<Core> cores;

    @OneToMany(cascade=ALL, mappedBy="cpu", fetch=FetchType.EAGER)
    public Set<Core> getCores() {
        return cores;
    }

    public void setCores(Set<Core> cores) {
        this.cores = cores;
    }

}
