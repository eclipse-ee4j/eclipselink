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
package org.eclipse.persistence.jpa.tests.jpql.tools.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to inject a concrete instance of {@link org.eclipse.persistence.jpa.jpql.
 * model.IJPQLQueryFormatter IJPQLQueryFormatter} into the unit-test before it is run. It allows
 * using multiple instances and the unit-tests are run with each of them separately.
 * <p>
 * <ul>
 * <li>Method in a test suite: Request the concrete instances that will be injected into the
 * unit-tests before they are run.</li>
 * <li>Field in a unit-test: Defines the variable for which the value will be injected before the
 * test is run.</li>
 * </ul>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface IJPQLQueryFormatterTestHelper {
}
