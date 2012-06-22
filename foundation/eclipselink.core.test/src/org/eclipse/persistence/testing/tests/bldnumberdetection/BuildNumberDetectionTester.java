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
package org.eclipse.persistence.testing.tests.bldnumberdetection;

import org.eclipse.persistence.Version;
import java.io.*;

/**
 *  Simple class to verify that the TopLink build matches
 *  the test build, specifically used to ensure automated
 *  MATS runs are using correct binary file pairs.
 */

public class BuildNumberDetectionTester
{

	public static void main(String[] args) throws IOException
	{
		String testBuildNumber;
		String tlOc4jJarBuildNumber;
		FileWriter sucDifWriter;
		String tWork = System.getenv("T_WORK");
		String sucDifFile = tWork+File.separator+"buildnumbermatch";

		System.out.println("T_WORK:  "+tWork);
		System.out.println("filename:  "+sucDifFile);
		System.out.println(Version.getProduct()+" jar build String:  "+Version.getVersionString());
		System.out.println(Version.getProduct()+" jar build date (yymmdd):  "+Version.getBuildDate());

		if (args != null) {
			// Need to read in the toplink-oc4j.jar build number from it's manifest file
			FileInputStream fstream = new FileInputStream(tWork+File.separator+"mats-bld-toplink-oc4j"+File.separator+"META-INF"+File.separator+"MANIFEST.MF");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			tlOc4jJarBuildNumber="undefined";
			while ((strLine = br.readLine()) != null)   {
				if (strLine.startsWith("Implementation-Version"))
					tlOc4jJarBuildNumber=strLine.substring(strLine.lastIndexOf('.')+1);
			}
			in.close();

			System.out.println("toplink-oc4j.jar test jar build:  "+tlOc4jJarBuildNumber);
			testBuildNumber=args[0].substring(args[0].lastIndexOf('.')+1);
			System.out.println("TopLink test jar build:  "+testBuildNumber);

			if (Version.getBuildNumber().equals(tlOc4jJarBuildNumber)) {

				if (Version.getBuildNumber().equals(testBuildNumber)) {
					sucDifWriter = new FileWriter(sucDifFile+".result");
					sucDifWriter.write("The builds match: "+Version.getBuildNumber()+"(toplink.jar) and "+testBuildNumber+"(test jars).");
				} else {
					sucDifWriter = new FileWriter(sucDifFile+".result");
					sucDifWriter.write("The builds DO NOT match: "+Version.getBuildNumber()+"(toplink.jar) and "+testBuildNumber+"(test jars).");
				}
			} else {
				sucDifWriter = new FileWriter(sucDifFile+".result");
				sucDifWriter.write("The TopLink binaries DO NOT match: "+Version.getBuildNumber()+"(toplink.jar) and "+tlOc4jJarBuildNumber+"(toplink-oc4j.jar).");
			}
		} else {
			sucDifWriter = new FileWriter(sucDifFile+".result");
			sucDifWriter.write("No test build number provided: "+Version.getBuildNumber()+"(toplink.jar).");
			
		}
		sucDifWriter.close();
	}
}
