/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

//  Contributors:
//      gonural - initial
package org.eclipse.persistence.jpa.rs.util;

/**
 * Factory class to create concrete subclasses of abstract classes.
 *
 * This class is used in JPA-RS JAXB mappings to create InstantiationPolicy for abstract classes
 * @see PreLoginMappingAdapter
 *
 * @author gonural
 *
 */
public class ConcreteSubclassFactory {

    @SuppressWarnings("rawtypes")
    private Class clazz = null;

    /**
     * Instantiates a new concrete subclass factory.
     *
     * @param clazz the clazz
     */
    @SuppressWarnings("rawtypes")
    public ConcreteSubclassFactory(Class clazz) {
        super();
        this.clazz = clazz;
    }

    /**
     * Creates a new ConcreteSubclass object.
     *
     * @return the object
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     */
    public Object createConcreteSubclass() throws InstantiationException, IllegalAccessException {
        return clazz.newInstance();
    }
}
