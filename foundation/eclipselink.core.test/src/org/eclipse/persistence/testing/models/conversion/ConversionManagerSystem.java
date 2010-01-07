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
package org.eclipse.persistence.testing.models.conversion;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;

public class ConversionManagerSystem extends TestSystem {
    public ConversionManagerSystem() {
        project = new ConversionManagerProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new ConversionManagerProject();
        }
        DatabasePlatform platform = session.getLogin().getPlatform();

        // If on Access, Oracle or DB2, remove the byte array mapping
        if (platform.isAccess() || platform.isOracle()) {
            ClassDescriptor objDescriptor = ((ClassDescriptor)project.getDescriptors().get(ConversionDataObject.class));
            objDescriptor.getMappings().removeElement(objDescriptor.getMappingForAttributeName("aPByteArray"));
        }

        if (platform.isDB2()) {
            // Bug 2719624 - This mapping is added so we can use DB2 to test our handling of Nanoseconds
            // DB2 handles nanoseconds at a level of precision and consistency that allows us to easily test.
            ClassDescriptor objDescriptor = ((ClassDescriptor)project.getDescriptors().get(ConversionDataObject.class));

            // SECTION: DIRECTTOFIELDMAPPING
            org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping28 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
            directtofieldmapping28.setAttributeName("dateToTimestamp");
            directtofieldmapping28.setIsReadOnly(false);
            directtofieldmapping28.setFieldName("CM_OBJ.DATE2TIMESTAMP");
            objDescriptor.addMapping(directtofieldmapping28);
        }

        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);
        TableDefinition definition = ConversionDataObject.tableDefinition();

        DatabasePlatform platform = session.getLogin().getPlatform();

        // Only add this field if NOT on Access or DB2
        if (!(platform.isAccess() || platform.isOracle())) {
            definition.addField("A_PBYTE_A", Byte[].class);
        }

        schemaManager.replaceObject(definition);
        schemaManager.createSequences();
    }

    public void populate(DatabaseSession session) {
        Object instance;
        PopulationManager manager = PopulationManager.getDefaultManager();

        instance = ConversionDataObject.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "example1");

        instance = ConversionDataObject.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "example2");

        instance = ConversionDataObject.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "example3");
    }
}
