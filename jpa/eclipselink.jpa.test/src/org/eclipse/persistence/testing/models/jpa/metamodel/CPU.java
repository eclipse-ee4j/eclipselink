/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Use Case: IdClass identifiers declared across multiple mappedSuperclasses in an inheritance hierarchy.
 * Note: The following MappedSuperclass defines 3 of 4 of the Id fields as part of the IdClass MSIdClassPK.
 * The 4th field is declared on the subclass.
 * The IdClass annotation can go on the subclass or the entity but not on this root.
 * As long as resolution of all fields in the IdClass are available - the configuration is good. 
 */
@MappedSuperclass
public abstract class CPU implements java.io.Serializable {
    
    private static final long serialVersionUID = 6887677320771553598L;
    
    protected CPUEmbeddedId id;
    
    // Any reference to this embedded key requires a bidirectional relationship (not unidirectional)
    @EmbeddedId
    @Column(name="CPU_ID")    
    public CPUEmbeddedId getId() {
        return id;
    }

    public void setId(CPUEmbeddedId id) {
        this.id = id;
    }

    private int version;
    
    @Version
    @Column(name="CPU_VERSION")
    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
        this.version = version;
    }    

}
