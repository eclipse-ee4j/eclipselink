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

import java.lang.reflect.Method;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;

/**
 * The concrete implementation of {@link org.eclipse.persistence.jpa.jpql.tools.spi.IMapping IMapping}
 * that is wrapping the runtime representation of a property.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class JavaPropertyMapping extends AbstractMethodMapping {

    /**
     * Creates a new <code>JavaPropertyMapping</code>.
     *
     * @param parent The parent of this mapping
     * @param method The Java {@link Method} wrapped by this mapping
     */
    public JavaPropertyMapping(IManagedType parent, Method method) {
        super(parent, method);
    }
}
