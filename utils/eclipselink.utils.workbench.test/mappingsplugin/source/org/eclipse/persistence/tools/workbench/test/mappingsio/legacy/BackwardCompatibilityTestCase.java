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
package org.eclipse.persistence.tools.workbench.test.mappingsio.legacy;

import java.io.File;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.PackageRenamer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools.ClassRepositoryTypesFieldDifferentiator;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.diff.CompositeDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffEngine;
import org.eclipse.persistence.tools.workbench.utility.diff.EqualityDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.ReferenceDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * This abstract class provides the common behavior for testing
 * backward-compatibility with MW project files:
 * - it defines the projects that should be tested
 * - it adds a few more fields to ignore
 * - it provides a simple API for renaming project files
 * 
 * Subclasses need to:
 * - extend setUp() to add any more fields that need to be ignored during comparison
 * - implement readOldProjectFor(MWRProject) to read the appropriate old project
 *    (and rename it if necessary)
 */
public abstract class BackwardCompatibilityTestCase
	extends TestCase
{
	private DiffEngine diffEngine;


	protected BackwardCompatibilityTestCase(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		super.setUp();

		TestTools.setUpOracleProxy();

		this.diffEngine = this.buildDiffEngine();
	}

	protected DiffEngine buildDiffEngine() {
		DiffEngine de = MappingsModelTestTools.buildDiffEngine();

		ReflectiveDifferentiator rd;

		rd = (ReflectiveDifferentiator) de.getUserDifferentiator(MWClassRepository.class);
			// the classpath has changed over the years
			rd.ignoreFieldsNamed("classpathEntries");
			// the old projects have some extraneous classes saved to XML
			ReferenceDifferentiator utDifferentiator = (ReferenceDifferentiator) rd.getFieldDifferentiator("userTypes");
			rd.replaceFieldDifferentiator("userTypes", new ClassRepositoryTypesFieldDifferentiator(utDifferentiator));

		rd = (ReflectiveDifferentiator) de.getUserDifferentiator(MWClass.class);
			// the modifier and interfaces are treated differently now in "stub" MWClasses
			rd.ignoreFieldsNamed("modifier", "interfaceHandles");

		rd = (ReflectiveDifferentiator) de.getUserDifferentiator(MWLoginSpec.class);
			// the drivers, servers, and logins have changed over the years
			rd.ignoreFieldsNamed("url", "userName", "driverClasspathEntries");

		rd = (ReflectiveDifferentiator) de.getUserDifferentiator(MWMethod.class);
			// MWMethods did not have exception types in 4.0
			rd.ignoreFieldsNamed("exceptionTypeHandles");

		rd = (ReflectiveDifferentiator) de.getUserDifferentiator(MWRelationalProject.class);
			// this will be different for legacy projects 5.0 and before
			rd.ignoreFieldsNamed("generateDeprecatedDirectMappings");
			
		rd = de.addReflectiveDifferentiator(MWAbstractRelationalReadQuery.class);
	
		setUpDescriptorCachingPolicyDifferentiator(de);

		rd = (ReflectiveDifferentiator) de.getUserDifferentiator(MWDescriptorCachingPolicy.class);
			// Need to perform a custom test to compare default value to project default value
			rd.ignoreFieldNamed("cacheTypeHolder");
			rd.ignoreFieldNamed("existenceChecking");

		rd = (ReflectiveDifferentiator) de.getUserDifferentiator(MWTable.class);
			//this is only used on reading of a legacy project and gets to a default null value of True
			rd.ignoreFieldNamed("legacyIsFullyQualified");
			
		return de;
	}

	private void setUpDescriptorCachingPolicyDifferentiator(DiffEngine de) {

		de.setUserDifferentiator(MWDescriptorCachingPolicy.class,
			new ReflectiveDifferentiator(MWDescriptorCachingPolicy.class, de.getRecordingDifferentiator()) {
				public Diff diff(Object object1, Object object2) {
					return new CompositeDiff(
						object1, 
						object2, 
						new Diff[] {
							super.diff(object1, object2), 
							cacheTypeDiff((MWCachingPolicy) object1, (MWCachingPolicy) object2)
						}, 
						this
					);
				}


				private Diff cacheTypeDiff(MWCachingPolicy cachingPolicy1, MWCachingPolicy cachingPolicy2) {
					if (cachingPolicy1.getCacheType() != cachingPolicy2.getCacheType() &&
						 cachingPolicy1.getCacheType().getMWModelOption() == MWCachingPolicy.CACHE_TYPE_PROJECT_DEFAULT)
					{
						String cacheType1 = cachingPolicy1.getProject().getDefaultsPolicy().getCachingPolicy().getCacheType().getMWModelOption();
						String cacheType2 = cachingPolicy2.getCacheType().getMWModelOption();
						return new CompositeDiff(
							cachingPolicy1,
							cachingPolicy2,
							new Diff[] {
								EqualityDifferentiator.instance().diff(cacheType1, cacheType2),
								cacheExistenceCheckingDiff(cachingPolicy1, cachingPolicy2)
							},
							this
						);
					}
					return cacheExistenceCheckingDiff(cachingPolicy1, cachingPolicy2);
				}

				private Diff cacheExistenceCheckingDiff(MWCachingPolicy cachingPolicy1, MWCachingPolicy cachingPolicy2) {
					if (cachingPolicy1.getExistenceChecking() != cachingPolicy2.getExistenceChecking() &&
						cachingPolicy1.getExistenceChecking().getMWModelOption() == MWCachingPolicy.EXISTENCE_CHECKING_PROJECT_DEFAULT)
					{
						String existenceChecking1 = cachingPolicy1.getProject().getDefaultsPolicy().getCachingPolicy().getExistenceChecking().getMWModelOption();
						String existenceChecking2 = cachingPolicy2.getExistenceChecking().getMWModelOption();
						return EqualityDifferentiator.instance().diff(existenceChecking1, existenceChecking2);
					}
					return EqualityDifferentiator.instance().diff(cachingPolicy1.getExistenceChecking(), cachingPolicy2.getExistenceChecking());
				}
			}
		);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	/**
	 * rename all the files in the specified directory tree;
	 * use the specified properties file;
	 * put the files in a temporary directory with the specified name;
	 * return the new directory that contains the renamed files
	 */
	private File renameDirectoryTree(String propertiesFileName, File sourceDirectory, String destinationDirectoryName) throws URISyntaxException {
		propertiesFileName = FileTools.resourceFile("/backwards-compatibility/rename/" + propertiesFileName).getAbsolutePath();
	
		String sourceDirectoryName = sourceDirectory.getAbsolutePath();
	
		File workDirectory = FileTools.temporaryDirectory("MW-backward-compatibility");
		File destinationDirectory = new File(workDirectory, destinationDirectoryName);
		destinationDirectory.mkdirs();
		FileTools.deleteDirectoryContents(destinationDirectory);
		destinationDirectoryName = destinationDirectory.getAbsolutePath();
	
		File logFile = new File(workDirectory, destinationDirectory.getName() + ".log");
		if (logFile.exists()) {
			if ( ! logFile.delete()) {
				throw new RuntimeException("unable to delete package renamer log file: " + logFile.getAbsolutePath());
			}
		}
		String logFileName = logFile.getAbsolutePath();
	
		PackageRenamer renamer = new PackageRenamer(new String[] {
					propertiesFileName,
					sourceDirectoryName,
					destinationDirectoryName,
					logFileName});
		renamer.run();
		// I don't understand: #run() is public, but #cleanup() is protected;
		// and you can't call #run() without calling #cleanup() or the log doesn't get closed...
		
		ClassTools.invokeMethod(renamer, "cleanup");
	
		return destinationDirectory;
	}
	
	/**
	 * rename all the files in the specified directory tree;
	 * return the new directory that contains the renamed files
	 */
	protected File renameDirectoryTree(File sourceDirectory, String version) throws URISyntaxException {
		// first rename the tree with the standard properties file...
		return this.renameDirectoryTree("eclipselinkPackageRename.properties", sourceDirectory, sourceDirectory.getName() + " " + version + " (temp renamed)");
		
	}

	protected Diff diff(Object object1, Object object2) {
		return this.diffEngine.diff(object1, object2);
	}

}
