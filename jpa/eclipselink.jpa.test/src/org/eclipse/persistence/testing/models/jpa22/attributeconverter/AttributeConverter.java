/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa22.attributeconverter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.Converter;

import org.eclipse.persistence.testing.models.jpa22.sessionbean.InjectedBean;

@Converter
public class AttributeConverter implements javax.persistence.AttributeConverter<String,String> {

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
