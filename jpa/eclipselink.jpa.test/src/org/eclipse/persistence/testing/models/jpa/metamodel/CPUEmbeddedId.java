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
 *     03/08/2010-2.1 Michael O'Brien 
 *       - 300051: JPA 2.0 Metamodel processing requires EmbeddedId validation moved higher from
 *                      EmbeddedIdAccessor.process() to MetadataDescriptor.addAccessor() so we
 *                      can better determine when to add the MAPPED_SUPERCLASS_RESERVED_PK_NAME
 *                      temporary PK field used to process MappedSuperclasses for the Metamodel API
 *                      during MetadataProject.addMetamodelMappedSuperclass()
 *     16/06/2010-2.2  mobrien - 316991: Attribute.getJavaMember() requires reflective getMethod call
 *       when only getMethodName is available on accessor for attributes of Embeddable types.
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_95:_20091017:_Attribute.getJavaMember.28.29_returns_null_for_a_BasicType_on_a_MappedSuperclass_because_of_an_uninitialized_accessor
 *                      
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import static javax.persistence.GenerationType.TABLE;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.TableGenerator;

// This class uses MethodAttributeAccessors during reflective calls
@Embeddable
public class CPUEmbeddedId implements Serializable {
    private static final long serialVersionUID = 2162087921393149126L;

    private int pk_part1;
//    private int pk_part2;

    // This class is embedded inside a CPU MappedSuperclass (MultiCoreCPU Entity)
    @GeneratedValue(strategy=TABLE, generator="CPUEMBEDID_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="CPUEMBEDID_MM_TABLE_GENERATOR", 
        table="CMP3_MM_CPUEMBEDDID_SEQ", 
        pkColumnName="SEQ_MM_NAME", 
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )
    @Column(name = "CPU_ID", nullable = false)
    public int getPk_part1() {
        return pk_part1;
    }

    public void setPk_part1(int pkPart1) {
        pk_part1 = pkPart1;
    }

    /*
    // This class is embedded inside a CPU MappedSuperclass (MultiCoreCPU Entity)
    @GeneratedValue(strategy=TABLE, generator="CPUEMBEDID_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="CPUEMBEDID_MM_TABLE_GENERATOR2", 
        table="CMP3_MM_CPUEMBEDDID_SEQ", 
        pkColumnName="SEQ_MM_NAME2", 
        valueColumnName="SEQ_MM_COUNT2",
        pkColumnValue="CUST_MM_SEQ2"
    )
    @Column(name = "CPU_ID", nullable = false)
    public int getPk_part2() {
        return pk_part2;
    }

    public void setPk_part2(int pkPart2) {
        pk_part2 = pkPart2;
    }
    */
    public CPUEmbeddedId() {
    }
    
    @Override
    public boolean equals(Object anEmbeddedId) {
        if (anEmbeddedId.getClass() != CPUEmbeddedId.class) {
            return false;
        }        
        CPUEmbeddedId embeddedId = (CPUEmbeddedId) anEmbeddedId;
        return ((CPUEmbeddedId)anEmbeddedId).pk_part1 == this.pk_part1;// &&                
        //((CPUEmbeddedId)anEmbeddedId).pk_part2 == this.pk_part2;
    }

    @Override
    public int hashCode() {
        return 9232 * pk_part1;// + pk_part2;
    }
        
}

