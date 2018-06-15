/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;


/**
 * Code coverage test.  Accesses unused object-type converter methods.
 */
public class NoFieldConversionValueInObjectTypeMapping extends ExceptionTest {
    public Map fieldToAttributeValues;
    public Map attributeToFieldValues;
    public ObjectTypeConverter objectType;

    public void reset() {
        super.reset();
        this.objectType.setFieldToAttributeValues(this.fieldToAttributeValues);
        this.objectType.setAttributeToFieldValues(this.attributeToFieldValues);
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void test() {
        ClassDescriptor employeeDescriptor = getSession().getDescriptor(new org.eclipse.persistence.testing.models.employee.domain.Employee());

        this.objectType = (ObjectTypeConverter)((DirectToFieldMapping)employeeDescriptor.getObjectBuilder().getMappingForAttributeName("gender")).getConverter();
        this.fieldToAttributeValues = new HashMap(objectType.getFieldToAttributeValues());
        this.attributeToFieldValues = new HashMap(objectType.getAttributeToFieldValues());
        objectType.setFieldToAttributeValues(new Hashtable());
        objectType.setAttributeToFieldValues(new Hashtable());
        expectedException = DescriptorException.noFieldValueConversionToAttributeValueProvided(null, new DatabaseField("field"), null);

        try {
            Object employee = getSession().readObject(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        } catch (EclipseLinkException exception) {
            this.caughtException = exception;
        }
    }
}
