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
package org.eclipse.persistence.tools.workbench.test.scplugin.model.adapter;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.tools.workbench.scplugin.model.EisPlatformManager;
import org.eclipse.persistence.tools.workbench.scplugin.model.SCPlatformManager;
import org.eclipse.persistence.tools.workbench.scplugin.model.ServerPlatformManager;


public class SCPlatformManagerTest extends AbstractAdapterTest {

    	public SCPlatformManagerTest( String name)
    	{
    		super( name);
    	}

    	public static Test suite()
    	{
    		return new TestSuite( SCPlatformManagerTest.class, "SCPlatformManager Test");
    	}

    	public void testVerifyServerPlatformManager() throws Exception
    	{
    	    String platformName = "CustomServerPlatform";
    	    SCPlatformManager manager = ServerPlatformManager.instance();
    		String className = manager.getRuntimePlatformClassNameFor( platformName);

    		assertTrue( "ServerPlatformManager - getRuntimePlatformClassNameFor()",
    		        className.endsWith( platformName));
    	}

    	public void testVerifyEisPlatformManager() throws Exception
    	{
    	    String platformName = "Oracle AQ";
    	    SCPlatformManager manager = EisPlatformManager.instance();
    		String className = manager.getRuntimePlatformClassNameFor( platformName);

    		assertTrue( "EisPlatformManager - getRuntimePlatformClassNameFor()",
    		        className.endsWith( "AQPlatform"));
    	}
}
