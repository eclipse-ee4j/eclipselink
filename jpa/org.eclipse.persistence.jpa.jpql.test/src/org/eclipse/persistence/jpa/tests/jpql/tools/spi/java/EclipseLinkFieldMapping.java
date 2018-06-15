/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools.spi.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import static org.eclipse.persistence.jpa.jpql.tools.spi.IEclipseLinkMappingType.*;

/**
 * The concrete implementation of {@link org.eclipse.persistence.jpa.jpql.tools.spi.IMapping IMapping}
 * that is wrapping the runtime representation of an EclipseLink mapping that is represented by a
 * persistent attribute.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkFieldMapping extends AbstractFieldMapping {

    /**
     * Creates a new <code>EclipseLinkFieldMapping</code>.
     *
     * @param parent The parent of this mapping
     * @param field The Java field wrapped by this mapping
     */
    public EclipseLinkFieldMapping(IManagedType parent, Field field) {
        super(parent, field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int calculateMappingType(Annotation[] annotations) {

        if (hasAnnotation(annotations, "org.eclipse.persistence.annotations.BasicCollection")) {
            return BASIC_COLLECTION;
        }

        if (hasAnnotation(annotations, "org.eclipse.persistence.annotations.BasicMap")) {
            return BASIC_MAP;
        }

        if (hasAnnotation(annotations, "org.eclipse.persistence.annotations.Transformation")) {
            return TRANSFORMATION;
        }

        if (hasAnnotation(annotations, "org.eclipse.persistence.annotations.VariableOneToOne")) {
            return VARIABLE_ONE_TO_ONE;
        }

        return super.calculateMappingType(annotations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCollection() {
        switch (getMappingType()) {
            case BASIC_COLLECTION:
            case BASIC_MAP: return true;
            default: return super.isCollection();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRelationship() {
        switch (getMappingType()) {
            case VARIABLE_ONE_TO_ONE: return true;
            default: return super.isRelationship();
        }
    }
}
