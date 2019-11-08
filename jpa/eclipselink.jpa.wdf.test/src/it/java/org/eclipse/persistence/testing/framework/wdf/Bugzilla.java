/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.framework.wdf;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.eclipse.persistence.platform.database.DatabasePlatform;

/**
 * Issue, which has been investigated and filed in Bugzilla. This test will be skipped
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Bugzilla {

    /**
     * The Bugzilla id of this issue.
     */
    long bugid();

    /**
     * The database platform classes on which this test should be skipped. Default: skip on all databases
     */
    Class<? extends DatabasePlatform>[] databases() default {};

    /**
     * The fully qualified names of the database platforms on which this test should be skipped. Default: skip on all databases.
     */
    String[] databaseNames() default {};
}
