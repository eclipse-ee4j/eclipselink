/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     03/08/2010-2.1 Michael O'Brien
//       - 300051: JPA 2.0 Metamodel processing requires EmbeddedId validation moved higher from
//                      EmbeddedIdAccessor.process() to MetadataDescriptor.addAccessor() so we
//                      can better determine when to add the MAPPED_SUPERCLASS_RESERVED_PK_NAME
//                      temporary PK field used to process MappedSuperclasses for the Metamodel API
//                      during MetadataProject.addMetamodelMappedSuperclass()
package org.eclipse.persistence.testing.models.jpa.metamodel;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Version;

/**
 * Use Case: IdClass identifiers declared across multiple mappedSuperclasses in an inheritance hierarchy.
 * Note: The following MappedSuperclass defines 3 of 4 of the Id fields as part of the IdClass MSIdClassPK.
 * The 4th field is declared on the subclass.
 * The IdClass annotation can go on the subclass or the entity but not on this root.
 * As long as resolution of all fields in the IdClass are available - the configuration is good.
 */
@Entity(name="CoreMetamodel")
@Table(name="CMP3_MM_CORE")
public class Core implements java.io.Serializable {
    private static final long serialVersionUID = 1168268798087713519L;

    private Integer id;

    private MultiCoreCPU cpu;

    private int version;

    @Id
    @GeneratedValue(strategy=TABLE, generator="CORE_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="CORE_MM_TABLE_GENERATOR",
        table="CMP3_MM_CORE_SEQ",
        pkColumnName="SEQ_MM_NAME",
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )
    // MethodLevelAttributeAccessor testing
    @Column(name="CORE_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch=EAGER)
    public MultiCoreCPU getCpu() {
        return cpu;
    }

    public void setCpu(MultiCoreCPU cpu) {
        this.cpu = cpu;
    }

    @Version
    @Column(name="CORE_VERSION")
    public int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }
}
