/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.mappings.converters.*;

/**
 * Bug 5170735 - PROJECTCLASSGENERATOR GENERATES NON-COMPILING CODE FOR TYPECONVERSIONCONVERTER
 * Test setting the ObjectClass for a TypeConversionConverter produces correct code
 * when the ObjectClass is of type array.
 * <p>Incorrectly generated code: [B.class
 * <br>Correct code: byte[].class
 */
public class TypeConversionConverterObjectClassIsArrayTest extends ProjectClassGeneratorResultFileTest {

	protected ClassDescriptor descriptor;
	protected DirectToFieldMapping mapping;
	protected Class classType;

	public TypeConversionConverterObjectClassIsArrayTest(Class classType) {
		super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject());
		setDescription("Test addTypeConversionConverterLines method -> setObjectClassName() generates legal array code");
		setName(getName() + "[" + classType.getName() + "]");
		// should validate to prevent improper test usage
		if (!classType.isArray()) {
			throwError("ClassType must be an array type: " + classType);
		}
		this.classType = classType;
		String expectedName = classType.getComponentType().getName();
		this.testString = "someDataMappingConverter.setObjectClass(" + expectedName + "[].class);";
	}

	public void setup() {
		getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
		descriptor = project.getDescriptors().get(org.eclipse.persistence.testing.models.employee.domain.Employee.class);

		mapping = new DirectToFieldMapping();
		mapping.setAttributeName("someData");
		mapping.setFieldName("SOME_TABLE.SOME_DATA");

		TypeConversionConverter someConverter = new TypeConversionConverter();
		// MW only sets dataClassName, this is translated to dataClass when written to the project java file
		someConverter.setObjectClassName(classType.getName());
		mapping.setConverter(someConverter);
		descriptor.addMapping(mapping);
	}

	public void reset() {
		if (descriptor != null) {
			descriptor.getMappings().remove(mapping);
			mapping.setDescriptor(null);
		}
		super.reset();
	}
}
