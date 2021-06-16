/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Matt MacIvor - 2.4 - Initial Implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlaccessorfactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jakarta.xml.bind.JAXBException;

import org.glassfish.jaxb.runtime.AccessorFactory;
import org.glassfish.jaxb.runtime.api.AccessorException;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;

public class ClassLevelAccessorFactory implements AccessorFactory {

    public Accessor createFieldAccessor(Class beanClass, Field field, boolean isReadOnly) throws JAXBException {
        return new ClassLevelAccessorFactory.MyCustomFieldAccessor(beanClass);
    }

    public Accessor createPropertyAccessor(Class beanClass, Method getMethod, Method setMethod) throws JAXBException {
        return new ClassLevelAccessorFactory.MyCustomPropertyAccessor(beanClass);
    }


    public static class MyCustomFieldAccessor extends Accessor {

        protected MyCustomFieldAccessor(Class valueType) {
            super(valueType);
        }

        public Object get(Object arg0) throws AccessorException {
            CustomerClassOverride cust = (CustomerClassOverride)arg0;
            return "class:field:" + cust.fieldProperty;
        }

        public void set(Object arg0, Object arg1) throws AccessorException {
            String value = (String)arg1;
            value = value.substring(value.lastIndexOf(":") + 1);
            ((CustomerClassOverride)arg0).fieldProperty = value;
        }
    }

    public class MyCustomPropertyAccessor extends Accessor{

        protected MyCustomPropertyAccessor(Class valueType) {
            super(valueType);
        }

        public Object get(Object arg0) throws AccessorException {
            CustomerClassOverride cust = (CustomerClassOverride)arg0;
            return "class:prop:" + cust.getProperty();
        }

        public void set(Object arg0, Object arg1) throws AccessorException {
            String value = (String)arg1;
            value = value.substring(value.lastIndexOf(":") + 1);
            ((CustomerClassOverride)arg0).setProperty(value);
        }
    }
}
