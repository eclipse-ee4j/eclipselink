/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
