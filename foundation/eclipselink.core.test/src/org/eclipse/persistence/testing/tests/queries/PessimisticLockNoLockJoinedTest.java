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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * For bug 3431017 tests when a NO_LOCK query joins an attribute which should be locked.
 * <p>
 * The correct behavior is that the object should be read again.
 */

public class PessimisticLockNoLockJoinedTest extends PessimisticLockJoinedAttributeTest {

public PessimisticLockNoLockJoinedTest() {
	this.lockMode = ObjectLevelReadQuery.LOCK_NOWAIT;
	setDescription("For bug 3431017 tests when a NO_LOCK query joins an attribute which should be locked.");
}
public void test () throws Exception
{
    if (!(getSession().getPlatform().isOracle() || getSession().getPlatform().isSQLServer())) {
		throw new TestWarningException("This test only runs on Oracle where writes do not block reads.");
	}
		
	uow = getSession().acquireUnitOfWork();

	ReadObjectQuery query = new ReadObjectQuery(LargeProject.class);
	// Only Charles Chanley and John Way are team leaders of projects they are also working on.
	Expression expression = query.getExpressionBuilder().get("teamLeader").get("firstName").equal("Charles");
	query.setSelectionCriteria(expression);
	query.addJoinedAttribute(query.getExpressionBuilder().get("teamLeader"));
	query.addJoinedAttribute(query.getExpressionBuilder().get("teamLeader").get("address"));

	// This is what differentiates from the super class test.
	query.setLockMode(ObjectLevelReadQuery.NO_LOCK);

	LargeProject uow1Project = (LargeProject)uow.executeQuery(query);

	uow1Project.getTeamLeader().getAddress();

	// Test the lock.
	// Because this is on a ServerSession the second UOW will have its own
	// ClientSession/exclusive connection.
	UnitOfWork uow2 = getSession().acquireUnitOfWork();
	try {
		boolean isLocked = false;
		LargeProject uow2Project = (LargeProject)uow2.executeQuery(query);

		try {
			uow2Project.getTeamLeader();
		} catch (EclipseLinkException exception) {
			isLocked = true;
		}
		if (!isLocked) {
			throw new TestWarningException("A joined attribute in an explicit NO_LOCK query is not being locked.  This breaks bug 3431017.");
		}
	} catch (RuntimeException e) {
		throw e;
	} finally {
		if (uow2 != null) {
			uow2.release();
		}
	}
}
}
