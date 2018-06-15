/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.4 - Initial Implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlaccessorfactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.xml.bind.JAXBException;

import com.sun.xml.bind.AccessorFactory;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
public class MyCustomAccessorFactory implements AccessorFactory {

    public Accessor createFieldAccessor(Class beanClass, Field field, boolean isReadOnly) throws JAXBException {
        return new MyCustomAccessorFactory.MyCustomFieldAccessor(beanClass);
    }

    public Accessor createPropertyAccessor(Class beanClass, Method getMethod, Method setMethod) throws JAXBException {
        return new MyCustomAccessorFactory.MyCustomPropertyAccessor(beanClass);
    }


    public static class MyCustomFieldAccessor extends Accessor {

        protected MyCustomFieldAccessor(Class valueType) {
            super(valueType);
        }

        public Object get(Object arg0) throws AccessorException {
            CustomerPackageLevel cust = (CustomerPackageLevel)arg0;
            return "package:field:" + cust.fieldProperty;
        }

        public void set(Object arg0, Object arg1) throws AccessorException {
            String value = (String)arg1;
            value = value.substring(value.lastIndexOf(":") + 1);
            ((CustomerPackageLevel)arg0).fieldProperty = value;
        }
    }

    public class MyCustomPropertyAccessor extends Accessor{

        protected MyCustomPropertyAccessor(Class valueType) {
            super(valueType);
        }

        public Object get(Object arg0) throws AccessorException {
            CustomerPackageLevel cust = (CustomerPackageLevel)arg0;
            return "package:prop:" + cust.getProperty();
        }

        public void set(Object arg0, Object arg1) throws AccessorException {
            String value = (String)arg1;
            value = value.substring(value.lastIndexOf(":") + 1);
            ((CustomerPackageLevel)arg0).setProperty(value);
        }
    }
}
