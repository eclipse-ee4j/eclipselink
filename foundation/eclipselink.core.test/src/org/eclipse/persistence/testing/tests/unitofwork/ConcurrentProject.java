/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - test for bug 262157
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork;

import java.math.BigDecimal;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class ConcurrentProject {
    protected BigDecimal id;
    protected String name;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(ConcurrentProject.class);
        descriptor.setTableName("CONCURRENT_PROJECT");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("name", "NAME");
        
        // Inheritance Properties.
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("CONCURRENT_PROJECT.DISCRIM");
        descriptor.getInheritancePolicy().addClassIndicator(ConcurrentProject.class, "P");
        descriptor.getInheritancePolicy().addClassIndicator(ConcurrentLargeProject.class, "L");

        return descriptor;
    }
    
    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("CONCURRENT_PROJECT");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", java.lang.String.class, 30);
        definition.addField("DISCRIM", java.lang.String.class, 1);

        return definition;
    }
}
