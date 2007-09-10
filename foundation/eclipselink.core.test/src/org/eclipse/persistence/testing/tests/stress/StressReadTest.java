/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.stress;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;

/**
 * Test insert many times.
 */
 
public class StressReadTest extends AutoVerifyTestCase {
	public int stressLevel;
public StressReadTest(int stressLevel)
{
	this.stressLevel = stressLevel;
}
public void test( )
{
	Address address = (Address)getSession().readObject(Address.class);
	for (int i = 0; i < stressLevel; i++) {
		getSession().readAllObjects(Address.class);
		getSession().readObject(Address.class, new ExpressionBuilder().get("id").equal(address.getId()));
	}
}
}
