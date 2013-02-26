/*******************************************************************************
* Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     Denise Smith - February 25, 2013
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.interfaces.choice;

import java.util.Properties;


public interface MyInterface {
	
	/* 
	 * no problem with below setters/getters 
	 */
	byte getTest();
    void setTest(byte test); 
    
	Byte getTest2();
	void setTest2(Byte test2);
	
	/*
	 *  but the problem is with below getter/setters:
	 *  just commented the first one to prove.. 
	 *  but you can uncomment to get an additional error
	 *  or comment the last one to 'fix' the issue
	 */
	
	
	Properties getProperties();
	void setProperties(Properties props);
	
	
	Properties getAnothernameproperties();
	void setAnothernameproperties(Properties anothernameproperties);
	
	

}

