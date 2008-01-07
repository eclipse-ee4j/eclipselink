/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jdkversiondetection;

import org.eclipse.persistence.*;

/**
 *  Simple class for manual test of JDK version detection.
 *  This class implements a main() method which makes use of our org.eclipse.persistence.Version
 *  class to detect JDK versions.  It then prints the results.
 */
public class JDKVersionDetectionTester 
{

	public static void main(String[] args)
	{
		System.out.println(Version.getProduct() + " " + Version.getVersion());
		System.out.println("Build " + Version.getBuildNumber());
		System.out.println("isJDK15() - " + Version.isJDK15());
	}
}