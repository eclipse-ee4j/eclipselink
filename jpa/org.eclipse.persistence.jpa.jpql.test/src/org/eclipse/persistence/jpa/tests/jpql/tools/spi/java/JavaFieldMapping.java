/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools.spi.java;

import java.lang.reflect.Field;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;

/**
 * The concrete implementation of {@link org.eclipse.persistence.jpa.jpql.tools.spi.IMapping IMapping}
 * that is wrapping the runtime representation of a persistent attribute.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public class JavaFieldMapping extends AbstractFieldMapping {

    /**
     * Creates a new <code>JavaFieldMapping</code>.
     *
     * @param parent The parent of this mapping
     * @param field The Java field wrapped by this mapping
     */
    public JavaFieldMapping(IManagedType parent, Field field) {
        super(parent, field);
    }
}
