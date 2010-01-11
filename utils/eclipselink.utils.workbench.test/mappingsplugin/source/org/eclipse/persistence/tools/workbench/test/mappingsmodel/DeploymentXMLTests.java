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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel;

import java.io.File;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.EISLogin;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.MultipleTableProject;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffEngine;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;

public class DeploymentXMLTests 
	extends TestCase {

	protected DiffEngine diffEngine;

	public static Test suite() {
		TestTools.setUpJUnitThreadContextClassLoader();
		return new TestSuite(DeploymentXMLTests.class);
	}
	
	public DeploymentXMLTests(String name) {
		super(name);
	}
	protected void setUp() throws Exception {
		super.setUp();
		
		this.diffEngine = MappingsModelTestTools.buildRuntimeDeploymentXmlDiffEngine();
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	private void verifyDeploymentXML(MWProject mwProject) throws Exception {
		Project originalProject = mwProject.buildRuntimeProject();

		for (Iterator stream = originalProject.getOrderedDescriptors().iterator(); stream.hasNext(); ) {
			ClassDescriptor descriptor = (ClassDescriptor) stream.next();
			if (descriptor.getAmendmentClassName() != null) {
				descriptor.setAmendmentClass(ConversionManager.loadClass(descriptor.getAmendmentClassName()));
				descriptor.applyAmendmentMethod();
			}
		}
	
		File deploymentXmlFile = tempFile(originalProject);
		
		// Write the XML
		XMLProjectWriter.write(deploymentXmlFile.getAbsolutePath(), originalProject);
		
		// Read the XML
		Project project = XMLProjectReader.read(deploymentXmlFile.getAbsolutePath(), this.getClass().getClassLoader());
		
		forceLazyIntialization(project);
		
		Diff diff = this.diffEngine.diff(project, originalProject);
		assertTrue(diff.getDescription(), diff.identical());
		
		DatasourceLogin login = (DatasourceLogin) project.getDatasourceLogin();
		if (login instanceof EISLogin) {            
            DatabaseSessionImpl session = (DatabaseSessionImpl) project.createDatabaseSession();
            session.dontLogMessages();
            //hard to login since I don't know how to define a url for eis connections
            //This will do, initialization is what we want to test anyway.
            session.initializeDescriptors();
        }
        else {
            if ( ! (login instanceof XMLLogin)) {		// don't munge an XML project - it doesn't use a Connector
				login.setConnector(mwProject.getDatabase().getDeploymentLoginSpec().buildConnector());
			}
			loginWithDeploymentXml(project);
			project.getDescriptors();
		}
	}

	private void forceLazyIntialization(Project project) {
		project.getDescriptors();
		for (Iterator stream = project.getDescriptors().keySet().iterator(); stream.hasNext(); ) {
			ClassDescriptor descriptor = ((ClassDescriptor) project.getDescriptors().get(stream.next()));
			for (Iterator mappings = descriptor.getMappings().iterator(); mappings.hasNext(); ) {
				DatabaseMapping mapping = (DatabaseMapping) mappings.next();
				if (mapping.isManyToManyMapping()) {
					DataModifyQuery query = ((DataModifyQuery) ClassTools.getFieldValue(mapping, "insertQuery"));
					query.getSelectionCriteria();
					query = ((DataModifyQuery) ClassTools.getFieldValue(mapping, "deleteQuery"));
					query.getSelectionCriteria();
					query = ((DataModifyQuery) ClassTools.invokeMethod(mapping, "getDeleteAllQuery"));
					query.getSelectionCriteria();
				}
				else if (mapping.isDirectCollectionMapping()) {
					//runtime likes to lazy initialize this one so it gets lazy initialized
					//when writing out the deployment xml, then it purposefully
					//doesn't get set when reading the deployment xml.
					//see ObjectPersistenceXmlProject.buildDatabaseQueryDescriptor selectionCriteria mapping
					((DirectCollectionMapping) mapping).getSelectionQuery().getSelectionCriteria();
				}
			}
			
			for (Iterator queries = descriptor.getQueryManager().getAllQueries().iterator(); queries.hasNext(); ) {
				DatabaseQuery query = (DatabaseQuery) queries.next();
				query.getSelectionCriteria();
			}
			
		}
	}
	
	private void loginWithDeploymentXml(Project runtimeProject) throws Exception {	
		DatabaseSession session = runtimeProject.createDatabaseSession();
		session.dontLogMessages();
//		session.logMessages();
		session.login();	// this will verify the mappings
		session.logout();
	}


	private File tempFile(Project project) {
		return this.tempFile(project.getName());
	}
	
	private File tempFile(String projectName) {
		return new File(FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName()), projectName + ".xml");
	}
/*	
	public void testComplexInheritanceSystem() throws Exception {
		MWProject bldrProject = new ComplexInheritanceProject().getProject();
		verifyDeploymentXML(bldrProject);
	}
	
	public void testContactProject() throws Exception {
		MWProject bldrProject = new SimpleContactProject().getProject();
		verifyDeploymentXML(bldrProject);
	}
	
	public void testCrimeSceneProject() throws Exception {
		MWProject bldrProject = new CrimeSceneProject().getProject();
		verifyDeploymentXML(bldrProject);
	}
	
	public void testEmployeeDemo() throws Exception {
		MWProject bldrProject = new EmployeeProject().getProject();
		verifyDeploymentXML(bldrProject);
	}
	
	//this fails because of runtime bug #3684981
	public void testEmployeeEisDemo() throws Exception {
		MWProject mwProject = new EmployeeEisProject().getProject();
		verifyDeploymentXML(mwProject);
	}
	
	public void testEmployeeOXDemo() throws Exception {
		MWProject mwProject = new EmployeeOXProject().getProject();
		verifyDeploymentXML(mwProject);
	}
	
	public void testEmployeeJaxbProject() throws Exception {
		MWProject bldrProject = new EmployeeJAXBProject().getProject();
		verifyDeploymentXML(bldrProject);
	}

	public void testInsuranceDemo() throws Exception {
		MWProject bldrProject = new InsuranceProject().getProject();			
		verifyDeploymentXML(bldrProject);
	}
    
    public void testLockingPolicyProject() throws Exception {
        MWProject bldrProject = new LockingPolicyProject().getProject();            
        verifyDeploymentXML(bldrProject);
    }
    
	public void testQueryProject() throws Exception {
		MWProject mwProject = new QueryProject().getProject();
		verifyDeploymentXML(mwProject);
	}

	public void testPhoneCompanyUsesSharedAggregatesProject() throws Exception {
		MWProject mwProject = new PhoneCompanyProject(true).getProject();
		verifyDeploymentXML(mwProject);
	}

	public void testPhoneCompanyProject() throws Exception {
		MWProject mwProject = new PhoneCompanyProject(false).getProject();
		verifyDeploymentXML(mwProject);
	}
	
	public void testComplexAggregateProject() throws Exception {
		MWProject mwProject = new ComplexAggregateProject().getProject();
		verifyDeploymentXML(mwProject);
	}
	
	public void testReturningPolicyEisProject() throws Exception {
		MWProject mwProject = new ReturningPolicyEisProject().getProject();
		verifyDeploymentXML(mwProject);
	}
    
    public void testReturningPolicyProject() throws Exception {
        MWProject mwProject = new ReturningPolicyProject().getProject();
        verifyDeploymentXML(mwProject);
    }
*/    
    public void testMultipleTableProject() throws Exception {
    	MWProject mwProject = new MultipleTableProject().getProject();
    	verifyDeploymentXML(mwProject);
    }
}
