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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.multipletable;

import java.util.*;
import java.math.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.*;

/**  
 * <b>Purpose</b>: Larger scale projects within the Employee Demo
 * <p><b>Description</b>: LargeProject is a concrete subclass of Project. It is instantiated for Projects with type = 'L'. The additional
 * information (budget, & milestoneVersion) are mapped from the LPROJECT table.
 * @see Project
 */
public class LargeBusinessProject extends BusinessProject {
    public Budget budget;
    public Calendar milestoneVersion;

    public LargeBusinessProject() {
        budget = new Budget(0.0);
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(LargeBusinessProject.class);
        descriptor.setTableName("LPROJ");
        descriptor.addPrimaryKeyFieldName("LPROJ.PROJ_ID");
        descriptor.getInheritancePolicy().setParentClass(BusinessProject.class);

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("milestoneVersion");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("LPROJ.MILESTONE");
        descriptor.addMapping(directtofieldmapping1);

        org.eclipse.persistence.mappings.OneToOneMapping onetoonemapping1 = new org.eclipse.persistence.mappings.OneToOneMapping();
        onetoonemapping1.setAttributeName("budget");
        onetoonemapping1.setIsReadOnly(false);
        onetoonemapping1.setUsesIndirection(false);
        onetoonemapping1.setReferenceClass(Budget.class);
        onetoonemapping1.setIsPrivateOwned(true);
        onetoonemapping1.addForeignKeyFieldName("BUDGET_ID", "ID");
        descriptor.addMapping(onetoonemapping1);

        return descriptor;
    }

    public static LargeBusinessProject example1() {
        Calendar milestone = GregorianCalendar.getInstance();
        milestone.clear();
        milestone.set(1999, 05, 06);
        LargeBusinessProject lp = new LargeBusinessProject();
        lp.name = "Java";
        lp.setMilestoneVersion(milestone);
        lp.setBudget(4333.00);
        return lp;
    }

    public static LargeBusinessProject example2() {
        Calendar milestone = GregorianCalendar.getInstance();
        milestone.clear();
        milestone.set(2001, 11, 11);
        LargeBusinessProject lp = new LargeBusinessProject();
        lp.name = "Smalltalk";
        lp.description = "a thing";
        lp.setMilestoneVersion(milestone);
        lp.setBudget(433355.00);
        return lp;
    }

    public Budget getBudget() {
        return budget;
    }

    public Calendar getMilestoneVersion() {
        return milestoneVersion;
    }

    public void setBudget(double amount) {
        setBudget(new Budget(amount));

    }

    public void setBudget(Budget theBudget) {
        budget = theBudget;
    }

    public void setMilestoneVersion(Calendar theMilestoneVersion) {
        milestoneVersion = theMilestoneVersion;
    }

    /**
 *Return a platform independant definition of the LPROJECT database table.
 */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("LPROJ");
        definition.addField("PROJ_ID", BigInteger.class, 15);
        definition.addField("BUDGET_ID", BigInteger.class, 10);
        definition.addField("MILESTONE", java.sql.Timestamp.class);

        return definition;
    }

    /**
 * Print the project's data.
 */
    public String toString() {
        java.io.StringWriter writer = new java.io.StringWriter();

        writer.write("Large Project: ");
        writer.write(getName());
        writer.write(" ");
        writer.write(getDescription());
        writer.write(" " + getBudget());
        writer.write(" ");
        writer.write(String.valueOf(getMilestoneVersion()));
        return writer.toString();
    }
}
