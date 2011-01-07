/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.optimisticlocking;

import java.sql.Timestamp;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;

public abstract class AbstractVideogameObject {

    protected int id;
    protected int version;
    protected Timestamp updated;
    protected String name;
    protected String description;

    public AbstractVideogameObject() {
        this(null, null);
    }

    public AbstractVideogameObject(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void configureAllFieldsLockingOn(ClassDescriptor descriptor) {
        descriptor.useAllFieldsLocking();
    }

    public void configureChangedFieldsLockingOn(ClassDescriptor descriptor) {
        descriptor.useChangedFieldsLocking();
    }

    public void configureSelectedFieldsLockingOn(ClassDescriptor descriptor) {
        Vector fieldNames = new Vector();
        fieldNames.add(descriptor.getTableName() + ".NAME");
        fieldNames.add(descriptor.getTableName() + ".DESCRIPTION");
        descriptor.useSelectedFieldsLocking(fieldNames);
    }

    public void configureVersionLockingOn(ClassDescriptor descriptor) {
        descriptor.useVersionLocking(descriptor.getTableName() + ".VERSION", false);
    }

    public void configureTimestampLockingOn(ClassDescriptor descriptor) {
        descriptor.useTimestampLocking(descriptor.getTableName() + ".UPDATED", false);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }
    
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
