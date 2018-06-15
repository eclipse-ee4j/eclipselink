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
package org.eclipse.persistence.jpa.tests.jpql;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to inject a concrete instance of {@link JPQLQueryTestHelper} into the
 * unit-test before it is run. It allows using multiple instances and the unit-tests are run with
 * each of them separately.
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
public @interface JPQLQueryTestHelperTestHelper {
}
