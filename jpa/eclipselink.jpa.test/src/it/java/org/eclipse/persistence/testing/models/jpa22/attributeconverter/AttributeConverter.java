/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.jpa22.attributeconverter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.persistence.Converter;

import org.eclipse.persistence.testing.models.jpa22.sessionbean.InjectedBean;

@Converter
public class AttributeConverter implements jakarta.persistence.AttributeConverter<String,String> {

    public static boolean INJECTED_RETURN_VALUE = false;
    public static int CONVERT_TO_DB_CALLS = 0;
    public static int CONVERT_TO_ENTITY_CALLS = 0;
    public static int POST_CONSTRUCT_CALLS = 0;
    public static int PRE_DESTROY_CALLS = 0;

    @Inject
    protected InjectedBean injected;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        CONVERT_TO_DB_CALLS++;
        INJECTED_RETURN_VALUE = injected.isCalled();
        return "DB name";
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        CONVERT_TO_ENTITY_CALLS++;
        INJECTED_RETURN_VALUE = injected.isCalled();
        return "Entity name";
    }

    @PostConstruct
    public void postConstruct(){
        POST_CONSTRUCT_CALLS++;
    }

    @PreDestroy
    public void preDestroy(){
        PRE_DESTROY_CALLS++;
    }
}
