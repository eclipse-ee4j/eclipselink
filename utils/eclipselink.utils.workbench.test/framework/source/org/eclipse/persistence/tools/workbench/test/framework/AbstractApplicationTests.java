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
package org.eclipse.persistence.tools.workbench.test.framework;

import java.net.URISyntaxException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.framework.AbstractApplication;
import org.eclipse.persistence.tools.workbench.framework.Application;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.ManifestInterrogator;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;



public class AbstractApplicationTests extends TestCase {
	private Application application;
	private boolean executingFromClassDir;

	public static Test suite() {
		return new TestSuite(AbstractApplicationTests.class);
	}
	
	public AbstractApplicationTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.application = new TestApplication();
		String classpathEntry = Classpath.locationFor(this.getClass());
		this.executingFromClassDir = ! Classpath.fileNameIsArchive(classpathEntry);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetFullProductName() {
		if (this.executingFromClassDir) {
			assertEquals("EclipseLink Project EclipseLink Workbench", this.application.getFullProductName());
		} else {
			assertEquals("EclipseLink Project EclipseLink Workbench Tests (debug)", this.application.getFullProductName());
		}
	}

	public void testGetShortProductName() {
		if (this.executingFromClassDir) {
			assertEquals("Workbench", this.application.getShortProductName());
		} else {
			assertEquals("Workbench Tests (debug)", this.application.getShortProductName());
		}
	}

	public void testGetVersionNumber() {
		assertEquals("1.1.0", this.application.getVersionNumber());
	}

	public void testGetFullProductNameAndVersionNumber() {
		if (this.executingFromClassDir) {
			assertEquals("EclipseLink Project EclipseLink Workbench 1.1.0", this.application.getFullProductNameAndVersionNumber());
		} else {
			assertEquals("EclipseLink Project EclipseLink Workbench Tests (debug) 1.1.0", this.application.getFullProductNameAndVersionNumber());
		}
	}

	public void testGetBuildNumber() {
		if (this.executingFromClassDir) {
			assertEquals("<dev>", this.application.getBuildNumber());
			Application prodApplication = new ProductionApplication();
			assertEquals("050921", prodApplication.getBuildNumber());
		}
	}

	public void testIsDevelopmentMode() {
		if (this.executingFromClassDir) {
			assertTrue(this.application.isDevelopmentMode());
			Application prodApplication = new ProductionApplication();
			assertFalse(prodApplication.isDevelopmentMode());
		} else {
			assertFalse(this.application.isDevelopmentMode());
		}
	}


private class TestApplication extends AbstractApplication {

	public String defaultSpecificationTitle() {
		return "EclipseLink Workbench";
	}

	public String defaultSpecificationVendor() {
		return "EclipseLink Project";
	}

	public String defaultReleaseDesignation() {
		return "1.1";
	}

	public String defaultLibraryDesignation() {
		return "Workbench";
	}

	public String defaultSpecificationVersion() {
		return "1.1.0";
	}

	public String defaultImplementationVersion() {
		return "1.1.0<dev>";
	}

	public boolean isFirstExecution() {
		return false;
	}

}

private class ProductionApplication extends TestApplication {
	protected ManifestInterrogator buildManifestInterrogator() {
		return new ManifestInterrogator(this.getClass(), this) {
			protected String buildJarFileName(Class resourceClass) {
				try {
					return FileTools.resourceFile("/jars/test.jar").getPath();
				}
				catch (URISyntaxException ex) {
					throw new RuntimeException(ex);
				}
			}
		};

	}
}

}
