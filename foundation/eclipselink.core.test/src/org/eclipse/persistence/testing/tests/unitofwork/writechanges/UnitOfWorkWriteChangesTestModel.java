/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
/* $Header: UnitOfWorkWriteChangesTestModel.java 17-nov-2005.13:31:29 gyorke Exp $ */
/* Copyright (c) 2004, 2005, Oracle. All rights reserved.  */
/*
   DESCRIPTION
    <short description of component this file declares/defines>

   PRIVATE CLASSES
    <list of private classes defined - with one-line descriptions>

   NOTES
    <other useful comments, qualifications, etc.>

   MODIFIED    (MM/DD/YY)
    gyorke      11/17/05 -
    jsutherl    11/02/05 -
    gyorke      08/09/05 - gyorke_10-essentials-directory-creation_050808
    smcritch    03/11/04 - smcritch_issue_sql_once_before_completion040305
    smcritch    03/05/04 -
    smcritch    03/04/04 - Creation
 */
package org.eclipse.persistence.testing.tests.unitofwork.writechanges;

import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;


/**
 * Tests the UnitOfWork writeChanges feature, a.k.a issue SQL once before completion.
 * Allows a user to write out their changes before commit.  They can then execute
 * simple queries (non-caching), or stored procedures on their uncommitted data,
 * and either commit() or rollback() at their discretion.
 * <p>
 * Intended for use with external transaction controllers, as any exception can
 * be received directly by the caller, toplink/custom exceptions are often lost
 * in the beforeCompletion/afterCompletion callbacks.
 * @see org.eclipse.persistence.testing.threetier.tests.externaltransaction For more the testing scenarios customers are likely to actually use.
 * @see org.eclipse.persistence.sessions.UnitOfWork#writeChanges
 *  @version $Header: UnitOfWorkWriteChangesTestModel.java 17-nov-2005.13:31:29 gyorke Exp $
 *  @author  smcritch
 *  @since   release specific (what release of product did this appear in)
 */
public class UnitOfWorkWriteChangesTestModel extends EmployeeBasicTestModel {
    public void addTests() {
        addTest(new BeginTransactionEarly_WriteChanges_TestCase());
        addTest(new WriteChanges_Commit_NonTrivial_TestCase());
        addTest(new WriteChanges_Commit_TestCase());
        addTest(new WriteChanges_CommitAndResume_TestCase());
        addTest(new WriteChanges_CommitAndResumeOnFailure_TestCase());
        addTest(new WriteChanges_DeleteAll_TestCase());
        addTest(new WriteChanges_IssueSQL_TestCase());
        addTest(new WriteChanges_NonCachingOLReadQuery_TestCase());
        addTest(new WriteChanges_OLModifyQuery_TestCase());
        addTest(new WriteChanges_OLReadQuery_TestCase());
        addTest(new WriteChanges_Register_TestCase());
        addTest(new WriteChanges_RegisterExisting_TestCase());
        addTest(new WriteChanges_RegisterNew_TestCase());
        addTest(new WriteChanges_Release_TestCase());
        addTest(new WriteChanges_ReportQuery_TestCase());
        addTest(new WriteChanges_RevertAndResume_TestCase());
        addTest(new WriteChangesFailed_TestCase());
        addTest(new WriteChangesFailed_StatementCountTestCase());
    }
}
