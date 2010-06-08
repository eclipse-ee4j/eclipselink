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
package org.eclipse.persistence.testing.tests.stress;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;

/**
 * Test login many times.
 */
 
public class StressLoginTest extends AutoVerifyTestCase {
	public int stressLevel;
public StressLoginTest(int stressLevel)
{
	this.stressLevel = stressLevel;
}
public void reset( )
{
	((DatabaseSession) getSession()).logout();
	((DatabaseSession) getSession()).login();
}
public void test( )
{
	Vector sessions =  new Vector();
	
	try {
		for (int i = 0; i < stressLevel; i++) {
			Session session = new Project((Login) getSession().getDatasourceLogin().clone()).createDatabaseSession();
			((DatabaseSession) session).login();
			sessions.addElement(session);
		}
		getSession().readObject(Address.class);
	} finally {
		for (Enumeration sessionEnum = sessions.elements(); sessionEnum.hasMoreElements(); ) {
			((DatabaseSession) sessionEnum.nextElement()).logout();
		}
	}	
	getSession().readObject(Address.class);
}
}
