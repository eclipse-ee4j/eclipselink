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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.structures.ArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Vesna
//Feb 2k3
//uses class org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder


public class StructureNameNotSetInMappingTest extends ExceptionTestSaveDescriptor {
    ClassDescriptor orgDescriptor;
    IntegrityChecker orgIntegrityChecker;

    public StructureNameNotSetInMappingTest() {
        super();
        setDescription("This tests Structure Name Not Set In Mapping (TL-ERROR 156)");
    }

    protected void setup() {
        expectedException = DescriptorException.structureNameNotSetInMapping(new ArrayMapping());
        super.setup();
    }


    public void test() {
        try {
            ((DatabaseSession)getSession()).addDescriptor(descriptor());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public ClassDescriptor descriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.PersonWithValueHolder.class);
        descriptor.addTableName("EMPLOYEE");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Descriptor properties.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("p_id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);

        ArrayMapping projectsMapping = new ArrayMapping();
        projectsMapping.setAttributeName("projects");
        //The structure is size_t defined on the database (in SCOTT schema), and is not set on the mapping
        //projectsMapping.setStructureName("SCOTT.size_t");
        projectsMapping.setFieldName("EMPLOYEE.F_NAME");
        descriptor.addMapping(projectsMapping);

        return descriptor;
    }
}

