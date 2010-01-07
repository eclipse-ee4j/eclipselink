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
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsio.ProjectIOManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethodParameter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.ComplexInheritanceProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.ComplexMappingProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.InsuranceProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.LegacyEmployeeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.LegacyPhoneCompanyProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.LegacySimpleContactProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.ReadOnlyProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.SimpleAggregateProject;
import org.eclipse.persistence.tools.workbench.utility.NullPreferences;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
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
public abstract class BackwardCompatibilityInternalTestCase extends BackwardCompatibilityTestCase {

	protected BackwardCompatibilityInternalTestCase(String name) {
		super(name);
	}

	
	protected void compareToOldProject(MWProject expectedProject) throws Exception {
		MWProject oldProject = this.readOldProjectFor(expectedProject);
        compareProjects(expectedProject, oldProject);
	}
	
    protected void compareProjects(MWProject expectedProject, MWProject oldProject) {
        this.convertNewClasses(oldProject);

        Diff diff = this.diff(expectedProject, oldProject);

        // sometimes we dump the branch size so we can compare it to the
        // number of objects compared by the diff engine
        // System.out.println("project size: " + expectedProject.branchSize());

        assertTrue(diff.getDescription(), diff.identical());       
    }
    
	/**
	 * rename and read the corresponding legacy project
	 */
	protected MWProject readOldProjectFor(MWProject expectedProject) throws Exception {
		String projectName = expectedProject.getName();
	
		// this is the *original* (un-renamed) legacy project
		File projectDirectory = FileTools.resourceFile("/backwards-compatibility/" + this.version() + "/" + projectName);
	
		// run the package renamer on it
		projectDirectory = this.renameDirectoryTree(projectDirectory, version());
	
		// read the resulting (renamed) project and return it
		ProjectIOManager ioMgr = new ProjectIOManager();
		return ioMgr.read(new File(projectDirectory, projectName + ".mwp"), NullPreferences.instance());
	}

	/**
	 * change method signatures in the "old" project to use the current,
	 * non-deprecated runtime classes so they match up with the old signatures
	 */
	private void convertNewClasses(MWProject oldProject) {
		// loop through the descriptors' classes
		for (Iterator stream = oldProject.descriptors(); stream.hasNext(); ) {
			this.convertNewClasses(((MWDescriptor) stream.next()).getMWClass());
		}
	}

	private void convertNewClasses(MWClass type) {
		for (Iterator stream = type.methods(); stream.hasNext(); ) {
			this.convertNewClasses((MWMethod) stream.next());
		}
	}
 
	private void convertNewClasses(MWMethod method) {
		for (Iterator stream = method.methodParameters(); stream.hasNext(); ) {
			this.convertNewClasses((MWMethodParameter) stream.next());
		}
	}
 
	private void convertNewClasses(MWMethodParameter parameter) {
		if (parameter.getType().getName().equals("org.eclipse.persistence.publicinterface.DatabaseRow")) {
			parameter.setType(parameter.typeFor(org.eclipse.persistence.sessions.Record.class));
		} else if (parameter.getType().getName().equals("org.eclipse.persistence.publicinterface.Session")) {
			parameter.setType(parameter.typeFor(org.eclipse.persistence.sessions.Session.class));
		} else if (parameter.getType().getName().equals("org.eclipse.persistence.publicinterface.DescriptorEvent")) {
			parameter.setType(parameter.typeFor(org.eclipse.persistence.descriptors.DescriptorEvent.class));
		} 
	}
 
	/**
	 * The method subclasses are required to implement.
	 */
	protected abstract String version();

	public void testComplexAggregate() throws Exception {
		this.compareToOldProject(this.buildComplexAggregateProject());
	}

	/**
	 * Override this to instantiate and return the correct legacy complex aggregate project.
	 */
	protected abstract MWRelationalProject buildComplexAggregateProject() throws Exception;
	
	public void testComplexInheritance() throws Exception {
		this.compareToOldProject(this.buildComplexInheritanceProject());
	}
	
	protected MWRelationalProject buildComplexInheritanceProject() throws Exception {
		return new ComplexInheritanceProject().getProject();
	}
	
	public void testComplexMapping() throws Exception {
		this.compareToOldProject(this.buildComplexMappingProject());
	}
	
	protected MWRelationalProject buildComplexMappingProject() throws Exception {
		return new ComplexMappingProject().getProject();
	}
	
	public void testEmployee() throws Exception {
		this.compareToOldProject(this.buildEmployeeProject());
	}
	
	protected MWRelationalProject buildEmployeeProject() throws Exception {
		return new LegacyEmployeeProject().getProject();
	}
	
	public void testInsurance() throws Exception {
		this.compareToOldProject(this.buildInsuranceProject());
	}
	
	protected MWRelationalProject buildInsuranceProject() throws Exception {
		return new InsuranceProject().getProject();
	}

	public void testPhoneCompany() throws Exception {
		this.compareToOldProject(this.buildPhoneCompanyProject());
	}
	
	protected MWRelationalProject buildPhoneCompanyProject() throws Exception {
		return new LegacyPhoneCompanyProject(false).getProject();
	}
		
	public void testReadOnly() throws Exception {
		this.compareToOldProject(this.buildReadOnlyProject());
	}
	
	protected MWRelationalProject buildReadOnlyProject() throws Exception {
		return new ReadOnlyProject().getProject();
	}
	
	public void testSimpleAggregate() throws Exception {
		this.compareToOldProject(this.buildSimpleAggregateProject());
	}
	
	protected MWRelationalProject buildSimpleAggregateProject() throws Exception {
		return new SimpleAggregateProject().getProject();
	}
	
	public void testSimpleContact() throws Exception {
		this.compareToOldProject(this.buildSimpleContactProject());
	}
		
	protected MWRelationalProject buildSimpleContactProject() throws Exception {
		return new LegacySimpleContactProject().getProject();
	}
}
