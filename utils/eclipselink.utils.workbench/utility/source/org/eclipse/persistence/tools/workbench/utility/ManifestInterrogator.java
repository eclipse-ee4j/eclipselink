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
package org.eclipse.persistence.tools.workbench.utility;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This class parses most of the necessary information from a specified
 * class's manifest.
 * The manifest is built and stored in a file (META_INF/MANIFEST.MF)
 * in the class's JAR during the build process.
 * 
 * User's of this class must provide a Java class from which 
 * the JAR file is determined. They also must provide an implementation of
 * ManifestDefaults for when the JAR and/or the manifest cannot be resolved.
 */
public class ManifestInterrogator {

	/**
	 * Defaults for when the Java class is not found
	 * within a JAR or the JAR has no manifest.
	 */
	private Defaults defaults;

	/**
	 * This is determined from the specified Java class.
	 * It will be null if the class is NOT in a JAR.
	 */
	private String jarFileName;

	/** Hold the manifest so we can get stuff from it as needed. */
	private Manifest manifest;

	/** Names of attributes stored in the JAR file manifest. */
	private static final Attributes.Name RELEASE_DESIGNATION = new Attributes.Name("Release-Designation");
	private static final Attributes.Name LIBRARY_DESIGNATION = new Attributes.Name("Library-Designation");


	// ********** constructor/initialization **********

	/**
	 * Construct a manifest interrogator for the specified class.
	 * If the manifest is unavailable, use the specified defaults.
	 */
	public ManifestInterrogator(Class javaClass, Defaults defaults) {
		super();
		this.defaults = defaults;
		this.initialize(javaClass);
	}

	private void initialize(Class javaClass) {
		this.jarFileName = this.buildJarFileName(javaClass);
		this.manifest = this.buildManifest();
	}

	/**
	 * Return the "full" name of the specified Java class's JAR file,
	 * either fully-qualified or with a path relative to the current
	 * working directory; e.g. "C:/orahome/toplink/jlib/toplinkmw.jar"
	 * or "jlib/toplinkmw.jar".
	 * Return null if the JAR cannot be determined.
	 */
	protected String buildJarFileName(Class javaClass) {
		URL url = Classpath.convertToResource(javaClass);
		if (url.getProtocol().equalsIgnoreCase("file")) {
			return null;	// the class is NOT in a JAR
		}

		// if the class is in a JAR, the URL will look something like this:
		// jar:file:/C:/jdk/1.4.2_04/jre/lib/rt.jar!/java/lang/String.class
		File file;
		try {
			file = FileTools.buildFile(url);
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
		return file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf('!'));
	}

	/**
	 * Build and return the application's manifest.
	 */
	private Manifest buildManifest() {
		JarFile jarFile = this.buildJarFile();
		if (jarFile == null) {
			// if there is no JAR file, use an empty manifest
			return new Manifest();
		}
		try {
			Manifest result = jarFile.getManifest();
			if (result == null) {
				// if there is no manifest in the JAR, use an empty manifest
				return new Manifest();
			}
			return result;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Build and return the application's JAR file. If the JAR file cannot
	 * be determined, return null.
	 */
	private JarFile buildJarFile() {
		if (this.jarFileName == null) {
			return null;
		}
		try {
			return new JarFile(this.jarFileName);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}


	// ********** public API **********

	/**
	 * Return the value of the specified attribute.
	 * Return null if the attribute is not found or empty.
	 */
	public String getMainAttributeValue(Attributes.Name name) {
		return getMainAttributeValue(name, null);
	}

	/**
	 * Return the value of the specified attribute.
	 * Return the specified default value if the attribute
	 * is not found or empty.
	 */
	public String getMainAttributeValue(Attributes.Name name, String defaultValue) {
		String result = this.manifest.getMainAttributes().getValue(name);
		if (StringTools.stringIsEmpty(result)) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Return false if the application is being executed from a JAR file.
	 */	
	public boolean isDevelopmentMode() {
		return this.jarFileName == null;
	}

	/**
	 * Concatenate the Specification Title, and
	 * the Library Designation, as derived from the JAR file manifest.
	 */
	public String getFullProductName() {
		return this.getSpecificationVendor() + " " + this.getProductName();
	}

	/**
	 * Return the Specification Title, as derived from the JAR file manifest.
	 */
	public String getProductName() {
		return this.getMainAttributeValue(Attributes.Name.SPECIFICATION_TITLE, this.defaults.defaultSpecificationTitle());
	}

	/**
	 * Return the Specification Vendor, as derived from the JAR file manifest.
	 */
	public String getSpecificationVendor() {
		return this.getMainAttributeValue(Attributes.Name.SPECIFICATION_VENDOR, this.defaults.defaultSpecificationVendor());
	}

	/**
	 * Return the Library Designation, as derived from the JAR file manifest.
	 */
	public String getShortProductName() {
		return this.getMainAttributeValue(LIBRARY_DESIGNATION, this.defaults.defaultLibraryDesignation());
	}

	/**
	 * Return the Specification Version, as derived from the JAR file manifest.
	 */
	public String getVersionNumber() {
		return this.getMainAttributeValue(Attributes.Name.SPECIFICATION_VERSION, this.defaults.defaultSpecificationVersion());
	}

	/**
	 * Strip the build number off the end of the Implementation Version.
	 */
	public String getBuildNumber() {
		if (this.isDevelopmentMode()) {
			return "<dev>";
		}
		String specVersion = this.getVersionNumber();
		String impVersion = this.getMainAttributeValue(Attributes.Name.IMPLEMENTATION_VERSION, this.defaults.defaultImplementationVersion());
		return impVersion.substring(specVersion.length() + 1);
	}

	/**
	 * Return the Release Designation, as derived from the JAR file manifest.
	 */
	public String getReleaseDesignation() {
		return this.getMainAttributeValue(RELEASE_DESIGNATION, this.defaults.defaultReleaseDesignation());
	}
	
	// ********** nested interface **********

	/**
	 * Users of ManifestInterrogator must provide an implementation of this
	 * interface.
	 */
	public interface Defaults {

		/**
		 * Return the default to be used when a manifest is unavailable.
		 */
		String defaultSpecificationTitle();

		/**
		 * Return the default to be used when a manifest is unavailable.
		 */
		String defaultSpecificationVendor();

		/**
		 * Return the default to be used when a manifest is unavailable.
		 */
		String defaultReleaseDesignation();

		/**
		 * Return the default to be used when a manifest is unavailable.
		 */
		String defaultLibraryDesignation();

		/**
		 * Return the default to be used when a manifest is unavailable.
		 */
		String defaultSpecificationVersion();

		/**
		 * Return the default to be used when a manifest is unavailable.
		 */
		String defaultImplementationVersion();

	}

}
