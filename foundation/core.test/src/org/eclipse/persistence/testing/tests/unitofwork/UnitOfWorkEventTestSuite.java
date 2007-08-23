/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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

    }
}
