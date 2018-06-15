/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Member;
import org.eclipse.persistence.jpa.jpql.tools.spi.IEmbeddable;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMappingBuilder;

/**
 * The concrete implementation of {@link IEmbeddable} that is wrapping the design-time
 * representation of a JPA embeddable.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public class JavaEmbeddable extends JavaManagedType
                            implements IEmbeddable {

    /**
     * Creates a new <code>JavaManagedType</code>.
     *
     * @param provider The provider of JPA managed types
     * @param type The {@link org.eclipse.persistence.jpa.jpql.tools.spi.IType IType} wrapping the Java type
     * @param mappingBuilder The builder that is responsible to create the {@link org.eclipse.
     * persistence.jpa.jpql.spi.IMapping IMapping} wrapping a persistent attribute or property
     */
    public JavaEmbeddable(IManagedTypeProvider provider,
                          JavaType type,
                          IMappingBuilder<Member> mappingBuilder) {

        super(provider, type, mappingBuilder);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(IManagedTypeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getType().getName();
    }
}
