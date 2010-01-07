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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.multipletable;

import java.math.*;
import java.io.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.*;

/**
 * <b>Purpose</b>: Abstract superclass for Large & Small projects in Employee Demo
 * <p><b>Description</b>: Project is an example of an abstract superclass. It demonstrates how class inheritance can be mapped to database tables.
 *                                It's subclasses are concrete and may or may not add columns through additional tables. The PROJ_TYPE field in the
 *                                database table indicates which subclass to instantiate. Projects are involved in a M:M relationship with employees. 
 *                                The Employee classs maintains the definition of the relation table
 * @see LargeProject
 * @see SmallProject
 */
public abstract class BusinessProject implements Serializable {
    public String name;
    public String description;
    public BigDecimal id;

    public BusinessProject() {
        name = "";
        description = "";
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        descriptor.setJavaClass(BusinessProject.class);
        descriptor.setTableName("PROJ");
        descriptor.addPrimaryKeyFieldName("PROJ.PROJ_ID");

        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setSequenceNumberName("PROJ_SEQ");
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("PROJ_TYPE");
        descriptor.setSequenceNumberFieldName("PROJ_ID");
        descriptor.useVersionLocking("VERSION");
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);
        descriptor.getInheritancePolicy().addClassIndicator(LargeBusinessProject.class, "L");

        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("description");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("PROJ.DESCRIP");
        descriptor.addMapping(directtofieldmapping);

        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("id");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("PROJ.PROJ_ID");
        descriptor.addMapping(directtofieldmapping1);

        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping2 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("name");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setFieldName("PROJ.PROJ_NAME");
        descriptor.addMapping(directtofieldmapping2);

        return descriptor;
    }

    public String getDescription() {
        return description;
    }

    /**
 * Return the persistent identifier of the receiver.
 */
    public BigDecimal getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String theDescriptor) {
        description = theDescriptor;
    }

    /**
 * Set the persistent identifier of the receiver.
 */
    public void setId(BigDecimal theId) {
        id = theId;
    }

    public void setName(String theName) {
        name = theName;
    }

    /**
 *Return a platform independant definition of the PROJECT database table.
 */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("PROJ");

        definition.addIdentityField("PROJ_ID", BigInteger.class, 15);
        definition.addField("PROJ_TYPE", String.class, 1);
        definition.addField("PROJ_NAME", String.class, 30);
        definition.addField("DESCRIP", String.class, 200);
        definition.addField("LEADER_ID", BigInteger.class, 15);
        definition.addField("VERSION", BigInteger.class, 15);
        return definition;
    }
}
