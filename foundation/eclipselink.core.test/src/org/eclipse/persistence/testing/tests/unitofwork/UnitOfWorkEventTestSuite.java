/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork;


public class UnitOfWorkEventTestSuite extends org.eclipse.persistence.testing.framework.TestSuite {

    public UnitOfWorkEventTestSuite() {
        super();
    }

    public void addTests() {
        // PostAcuireUnitOfWork
        addTest(new PostAcquireUnitOfWorkTest());

        // PreCommitUnitOfWork
        addTest(new PreCommitUnitOfWorkTest());

        // PrepareUnitOfWork
        addTest(new PrepareUnitOfWorkTest());

        // PostCommitUnitOfWork
        addTest(new PostCommitUnitOfWorkTest());

        // PreReleaseUnitOfWork
        addTest(new PreReleaseUnitOfWorkTest());

        // PostReleaseUnitOfWork
        addTest(new PostReleaseUnitOfWorkTest());

        // PostResumeUnitOfWork
        addTest(new PostResumeUnitOfWorkTest());

        // commitUnitOfWorkMoreThanOnce
        addTest(new CommitUnitOfWorkAgainTest());

        // Force an SQLException to test for absence of an UnsupportedOperationException
        addTest(new CommitUnitOfWorkForcingSQLExceptionTest());

    }
}
