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
package org.eclipse.persistence.tools.workbench.test.scplugin.app.swing;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;

/**
 * Based class for testing a DatabaseLogin property.
 * Sets the subject to the first session login in the config file.
 */
public abstract class SCDatabaseLoginUITest extends SCAbstractUITest {

	private DatabaseLoginAdapter subject;
	private PropertyValueModel subjectHolder;
	
	protected SCDatabaseLoginUITest() {
		super();
	}
	
	protected void setUp() {
		
		super.setUp();

		DatabaseSessionAdapter session = ( DatabaseSessionAdapter)getTopLinkSessions().sessionNamed( "SC-EmployeeTest");
		
		subject = ( DatabaseLoginAdapter)session.getLogin();
		
		subjectHolder = new SimplePropertyValueModel( subject);
	}
	
	protected DatabaseLoginAdapter subject() {
		return subject;
	}	
	
	protected PropertyValueModel subjectHolder() {
		return subjectHolder;
	}

	protected void clearModel() {
		subjectHolder.setValue( null);
	}

	protected void restoreModel() {
		subjectHolder.setValue( subject());
	}

	protected void printModel() {
		System.out.println( subject());
	}

}
