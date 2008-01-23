/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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

        // If on Access or DB2, remove the byte array mapping
        if (platform.isAccess()) {
            ClassDescriptor objDescriptor = ((ClassDescriptor)project.getDescriptors().get(ConversionDataObject.class));
            objDescriptor.getMappings().removeElement(objDescriptor.getMappingForAttributeName("aPByteArray"));
        }

        if (!(platform.isSybase() || platform.isSQLServer())) {
            // Must switch boolean to type converion as these do not support booleans.
            ClassDescriptor objDescriptor = ((ClassDescriptor)project.getDescriptors().get(ConversionDataObject.class));
            objDescriptor.getMappings().removeElement(objDescriptor.getMappingForAttributeName("aBoolean"));
            objDescriptor.getMappings().removeElement(objDescriptor.getMappingForAttributeName("aPBoolean"));

            // SECTION: TYPECONVERSIONMAPPING
            org.eclipse.persistence.mappings.DirectToFieldMapping typeconversionmapping12 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
            org.eclipse.persistence.mappings.converters.TypeConversionConverter typeconversionconverter12 = new org.eclipse.persistence.mappings.converters.TypeConversionConverter();
            typeconversionmapping12.setConverter(typeconversionconverter12);
            typeconversionmapping12.setAttributeName("aBoolean");
            typeconversionmapping12.setFieldName("CM_OBJ.A_BOOLEAN");
            typeconversionconverter12.setObjectClass(java.lang.Boolean.class);
            typeconversionconverter12.setDataClass(java.lang.Integer.class);
            objDescriptor.addMapping(typeconversionmapping12);

            // SECTION: TYPECONVERSIONMAPPING
            typeconversionmapping12 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
            typeconversionconverter12 = new org.eclipse.persistence.mappings.converters.TypeConversionConverter();
            typeconversionmapping12.setConverter(typeconversionconverter12);
            typeconversionmapping12.setAttributeName("aPBoolean");
            typeconversionmapping12.setFieldName("CM_OBJ.A_PBOOLEAN");
            typeconversionconverter12.setObjectClass(boolean.class);
            typeconversionconverter12.setDataClass(int.class);
            objDescriptor.addMapping(typeconversionmapping12);
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

        if (platform.isOracle() || platform.isDB2()) {
            // Must switch short/byte to type converion as Oracle does not support those tow types.
            ClassDescriptor objDescriptor = ((ClassDescriptor)project.getDescriptors().get(ConversionDataObject.class));
            objDescriptor.getMappings().removeElement(objDescriptor.getMappingForAttributeName("aByte"));
            objDescriptor.getMappings().removeElement(objDescriptor.getMappingForAttributeName("aPByte"));
            objDescriptor.getMappings().removeElement(objDescriptor.getMappingForAttributeName("aPByteArray"));
            objDescriptor.getMappings().removeElement(objDescriptor.getMappingForAttributeName("aPShort"));
            objDescriptor.getMappings().removeElement(objDescriptor.getMappingForAttributeName("aShort"));
            objDescriptor.getMappings().removeElement(objDescriptor.getMappingForAttributeName("floatToByte"));
            objDescriptor.getMappings().removeElement(objDescriptor.getMappingForAttributeName("floatToShort"));
            objDescriptor.getMappings().removeElement(objDescriptor.getMappingForAttributeName("intToByte"));
            objDescriptor.getMappings().removeElement(objDescriptor.getMappingForAttributeName("intToShort"));
        }

        (session).addDescriptors(project);
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