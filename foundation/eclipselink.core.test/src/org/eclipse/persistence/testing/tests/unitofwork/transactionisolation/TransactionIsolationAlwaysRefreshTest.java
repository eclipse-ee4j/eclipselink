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
package org.eclipse.persistence.testing.tests.unitofwork.transactionisolation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Tests the Session read refactoring / reading through the write connection
 * properly feature.
 * <p>
 * Tests the interaction of building clones from rows in transaction and
 * the descriptor setting alwaysRefreshIdentityMapResult.
 * <p>
 * Two things could have gone wrong.  a) since this only refreshes the originals
 * we might do nothing and not refresh, since we skip all that original only code.
 * b) we refresh the originals from the write connection.
 * <p>
 * The combination of these two features provide perfect support for transaction
 * isolation.  If in transaction and no original in cache, will not put one in.
 * If always refreshing will never use what is in there, for we would have to
 * refresh it from a dirty row.  However objects are still put in the shared
 * cache after commit, and read object queries can get cache hits without
 * checking if always refresh is true.
 * <p>
 * The only question is whether a cache hit can succeed if set to always refresh
 *  @version $Header: TransactionIsolationAlwaysRefreshTest.java 02-nov-2005.16:17:07 jsutherl Exp $
 *  @author  smcritch
 *  @since   release specific (what release of product did this appear in)
 */
public class TransactionIsolationAlwaysRefreshTest extends TransactionIsolationRefreshTest {
    boolean didRefresh;

    protected void setup() throws Exception {
        super.setup();
        // Must override otherwise test is meaningless.
        refreshQuery.dontRefreshIdentityMapResult();
        // This is to guarantee going to database, in the descriptor refreshing case
        // don't automatically.
        refreshQuery.dontMaintainCache();

        ClassDescriptor descriptor = getSession().getClassDescriptor(Employee.class);
        didRefresh = descriptor.shouldAlwaysRefreshCache();
        descriptor.setShouldAlwaysRefreshCache(true);
    }

    public void reset() throws Exception {
        super.reset();
        ClassDescriptor descriptor = getSession().getClassDescriptor(Employee.class);
        descriptor.setShouldAlwaysRefreshCache(didRefresh);

    }
}
