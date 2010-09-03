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

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;

public class ConcurrentLargeProject extends ConcurrentProject {

    protected ConcurrentPerson supervisor;

    public static boolean isForBackup = false;
    
    public ConcurrentPerson getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(ConcurrentPerson supervisor) {
        this.supervisor = supervisor;
    }
    
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(ConcurrentLargeProject.class);
        descriptor.setTableName("CONCURRENT_PROJECT");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setParentClass(ConcurrentProject.class);

        OneToOneMapping supervisorMapping = new OneToOneMapping();
        supervisorMapping.setAttributeName("supervisor");
        supervisorMapping.setReferenceClass(ConcurrentPerson.class);
        supervisorMapping.setGetMethodName("getSupervisor");
        supervisorMapping.setSetMethodName("setSupervisor");
        supervisorMapping.dontUseIndirection();
        supervisorMapping.addTargetForeignKeyFieldName("CONCURRENT_EMP.PROJ_ID", "CONCURRENT_PROJECT.ID");
        descriptor.addMapping(supervisorMapping);

        return descriptor;
    }
}
