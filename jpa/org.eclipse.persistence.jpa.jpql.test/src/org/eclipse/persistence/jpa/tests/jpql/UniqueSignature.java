/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.tests.jpql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be added to a unit-test class when it is executed more than once and the
 * signature of its methods remain identical. This will allow Eclipse to differentiate between them
 * and to properly update the test's status in the JUnit view.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueSignature {
}