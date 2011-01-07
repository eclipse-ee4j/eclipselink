/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.utils.rename;

import java.io.*;
import java.util.*;

/**
 * Provides a product rename utility specific to moving Oracle TopLink source to EclipseLink.
 * In addition to invoking the package-rename utility it also clears out the target folder
 * except the doNotDeleteFiles. When copying and renaming classes the renamer will NOT replace
 * existing files. This allows us to modify target files and keep the modifications from future
 * executions overwriting them.
 * 
 * @author djclarke
 * 
 */
public class MigrateTopLinkToEclipseLink {
	private static final String PROPERTIES_FILE = "package-rename.properties";

	public static void main(String[] args) {
		if (args.length < 2 || args.length > 3){
			System.out.println("Usage: MigrateTopLinkToEclipseLink sourceDir targetDir (propertiesFile)");
		}
		String source = args[0];
		String target = args[1];
		String propertiesFile = PROPERTIES_FILE;
		if (args.length == 3){
			propertiesFile = args[2];
		}
		new MigrateTopLinkToEclipseLink().migrate(source, target, propertiesFile);
	}

	public void migrate(String source, String target, String propertiesFile) {
		Properties props = readProperties(propertiesFile);

		clearTargetFolder(target);

		PackageRenamer renamer = new PackageRenamer(source, target, props);

		renamer.run();
	}

	private void clearTargetFolder(String targetFolder) {
		File folder = new File(targetFolder);

		if (folder.exists()) {
			removeAllJavaFiles(folder);
		}
	}

	private void removeAllJavaFiles(File folder) {
		File[] contents = folder.listFiles();

		for (int index = 0; contents != null && index < contents.length; index++) {
			File file = contents[index];

			if (file.isDirectory()) {
				removeAllJavaFiles(file);
			} else {
				if (file.getName().endsWith(".java")) {
					file.delete();
				}
			}
		}
	}

	private Properties readProperties(String filename) {
		Properties props = new Properties();
		try {
			InputStream in = new FileInputStream(filename);
			props.load(in);
			in.close();
		} catch (FileNotFoundException fileNotFoundException) {
			throw new RuntimeException("Properties file was not found: "
					+ filename);
		} catch (IOException ioException) {
			throw new RuntimeException(
					"IO error occurred while reading the properties file:'"
							+ filename + "'" + ioException.getMessage());
		}

		return props;
	}
}
