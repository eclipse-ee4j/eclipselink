/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.mappings.converters.*;

/**
 * Bug 5170735 - PROJECTCLASSGENERATOR GENERATES NON-COMPILING CODE FOR TYPECONVERSIONCONVERTER
 * Test setting the DataClass for a TypeConversionConverter produces correct code
 * when the DataClass is of type array.
 * <p>Incorrectly generated code: [B.class
 * <br>Correct code: byte[].class
 */
public class TypeConversionConverterDataClassIsArrayTest extends ProjectClassGeneratorResultFileTest {

    protected ClassDescriptor descriptorToModify;

    public TypeConversionConverterDataClassIsArrayTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject(), 
              "blobDataMappingConverter.setDataClass(byte[].class);");
        setDescription("Test addTypeConversionConverterLines method -> setDataClass() generates legal array code");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        descriptorToModify = (ClassDescriptor)project.getDescriptors().get(
            org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        
        DirectToFieldMapping blobDataMapping = new DirectToFieldMapping();
        blobDataMapping.setAttributeName("blobData");
        blobDataMapping.setFieldName("MR_BLOBBY.BLOB_DATA");
        TypeConversionConverter blobDataMappingConverter = new TypeConversionConverter();
        blobDataMappingConverter.setDataClass(java.sql.Blob.class);
        blobDataMappingConverter.setDataClass(byte[].class);
        blobDataMapping.setConverter(blobDataMappingConverter);
        descriptorToModify.addMapping(blobDataMapping);
    }

}
