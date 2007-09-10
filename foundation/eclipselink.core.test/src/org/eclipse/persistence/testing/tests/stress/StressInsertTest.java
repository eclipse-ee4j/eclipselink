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

/**
 * Test insert many times.
 */
 
public class StressInsertTest extends AutoVerifyTestCase {
	public int stressLevel;
public StressInsertTest(int stressLevel)
{
	this.stressLevel = stressLevel;
}
public void test( )
{
	long seq = 900000000;
	seq = seq * 1000;
	for (int i = 0; i < stressLevel; i++) {
		Address address = new Address();
		getDatabaseSession().insertObject(address);
		if (address.getId() < seq) {
			throw new TestErrorException("Sequence id was truncated.");
		}	
	}
}
}
