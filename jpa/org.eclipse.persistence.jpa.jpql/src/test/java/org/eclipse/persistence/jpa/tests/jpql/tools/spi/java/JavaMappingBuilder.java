/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools.spi.java;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMappingBuilder;

/**
 * A {@link IMappingBuilder} that creates the right instance of {@link IMappingBuilder} for a class'
 * {@link Member members}, which are either a persistent attribute or a property.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class JavaMappingBuilder implements IMappingBuilder<Member> {

    /**
     * Creates a new <code>JavaMappingBuilder</code>.
     */
    public JavaMappingBuilder() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public IMapping buildMapping(IManagedType parent, Member value) {

        if (value instanceof Field) {
            return new JavaFieldMapping(parent, (Field) value);
        }

        return new JavaPropertyMapping(parent, (Method) value);
    }
}
