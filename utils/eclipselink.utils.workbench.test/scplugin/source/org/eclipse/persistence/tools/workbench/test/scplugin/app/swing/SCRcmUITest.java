/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RemoteCommandManagerAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;

/**
 * Based class for testing a RemoteCommandManager property.
 * Sets the subject to the first session RemoteCommandManager in the config file.
 */
public abstract class SCRcmUITest extends SCAbstractUITest {

	private RemoteCommandManagerAdapter subject;
	private PropertyValueModel subjectHolder;
	
	protected SCRcmUITest() {
		super();
	}
	
	protected void setUp() {
		
		super.setUp();

		DatabaseSessionAdapter session = ( DatabaseSessionAdapter)getTopLinkSessions().sessionNamed( "SC-EmployeeTest");
		
		this.subject = session.getRemoteCommandManager();
		
		this.subjectHolder = new SimplePropertyValueModel( this.subject);
	}
	
	protected RemoteCommandManagerAdapter subject() {
		return this.subject;
	}	
	
	protected PropertyValueModel subjectHolder() {
		return this.subjectHolder;
	}

	protected void clearModel() {
		this.subjectHolder.setValue( null);
	}

	protected void restoreModel() {
		this.subjectHolder.setValue( subject());
	}

	protected void printModel() {
		System.out.println( subject());
	}

}
