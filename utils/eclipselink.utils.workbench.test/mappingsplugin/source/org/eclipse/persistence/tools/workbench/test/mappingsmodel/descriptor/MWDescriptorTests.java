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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.utility.Classpath;


public class MWDescriptorTests extends ModelProblemsTestCase {
	
	public static Test suite() {
		return new TestSuite(MWDescriptorTests.class);
	}
	
	public MWDescriptorTests(String name) {
		super(name);
	}

//	public void testAddQueryKey() throws ClassNotFoundException {
//		CrimeSceneProject csp = new CrimeSceneProject();
//		
//		MWRDescriptor desc = csp.getPersonDescriptor();
//		MWDatabaseField field = desc.getFieldNamed("L_NAME");
//		MWUserDefinedQueryKey qk = desc.addQueryKey("surname",field);
//		assertTrue("Query key not added", desc.queryKeyNamed("surname").equals(qk));
//		int numQueryKeys = desc.getAllQueryKeys().size();
//		
//		// make sure it only adds unique ones
//		qk = desc.addQueryKey("surname", field);
//		assertEquals(numQueryKeys, desc.getAllQueryKeys().size());
//	
//		// make sure it won't allow adding a user-generated one with the same name as an auto-generated one
//		qk = desc.addQueryKey("id", field);
//		assertTrue("Should not added a query key when an existing auto-generated key with the same name exists.", 
//			(! new ArrayList(desc.getAllQueryKeys()).contains(qk)));	// convert to ArrayList so we use object identity in contains()
//	}
	
	public void testClassShouldBePublicProblem() {
		String problem = ProblemConstants.DESCRIPTOR_CLASS_NOT_PUBLIC;
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWTableDescriptor desc = csp.getPersonDescriptor();
		
		assertTrue("The descriptor should not have problem: " + problem, !hasProblem(problem, desc));
		
		desc.getMWClass().getModifier().setPrivate(true);
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));
	}
	
	public void testShouldNotSubclassFinalClassProblem() {
		String problem = ProblemConstants.DESCRIPTOR_CLASS_SUBCLASSES_FINAL_CLASS;
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWTableDescriptor desc = csp.getVictimDescriptor();
		MWTableDescriptor personDesc = csp.getPersonDescriptor();
		
		assertTrue("The descriptor should not have problem: " + problem, !hasProblem(problem, desc));
		
		personDesc.getMWClass().getModifier().setFinal(true);
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));
	}
	
	public void testInheritanceClassIndicatorFields() {
		MWProject project = new MWRelationalProject("relational", MappingsModelTestTools.buildSPIManager(), DatabasePlatformRepository.getDefault().platformNamed("Oracle"));
		project.getClassRepository().addClasspathEntry(Classpath.locationFor(this.getClass()));
		
		MWClass vehicleType = null;
		MWClass boatType = null;
		MWClass nonFueledVehicleType = null;

		MWMappingDescriptor vehicleDescriptor =  null;
		MWMappingDescriptor boatDescriptor =  null;
		MWMappingDescriptor nonFueledVehicleDescriptor =  null;
		try {
			vehicleType = project.typeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Vehicle");
			vehicleType.refresh();
			boatType = project.typeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.Boat");
			boatType.refresh();
			nonFueledVehicleType = project.typeNamed("org.eclipse.persistence.tools.workbench.test.models.complexinheritance.NonFueledVehicle");
			nonFueledVehicleType.refresh();
		} catch(ExternalClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		try {
			vehicleDescriptor = (MWMappingDescriptor) project.addDescriptorForType(vehicleType);
			boatDescriptor = (MWMappingDescriptor) project.addDescriptorForType(boatType);
			nonFueledVehicleDescriptor = (MWMappingDescriptor) project.addDescriptorForType(nonFueledVehicleType);
			
		} catch(InterfaceDescriptorCreationException e) {
			throw new RuntimeException(e);
		}
		
		
		vehicleDescriptor.addInheritancePolicy();
		((MWDescriptorInheritancePolicy) vehicleDescriptor.getInheritancePolicy()).setIsRoot(true);
		boatDescriptor.addInheritancePolicy();
		nonFueledVehicleDescriptor.addInheritancePolicy();
		
		MWClassIndicatorFieldPolicy vehicleClassIndPolicy = (MWClassIndicatorFieldPolicy) vehicleDescriptor.getInheritancePolicy().getClassIndicatorPolicy();
		
		assertTrue("There are not 3 class indicator values: Boat, Vehicle, and NonFueldVehicle", vehicleClassIndPolicy.classIndicatorValuesSize() == 3);
		
		
		nonFueledVehicleDescriptor.removeInheritancePolicy();
		
		assertTrue("Vehicle should now only have 1 classIndicator", vehicleClassIndPolicy.classIndicatorValuesSize() == 1);
		
		
		nonFueledVehicleDescriptor.addInheritancePolicy();
		assertTrue("There are not 3 class indicator values: Boat, Vehicle, and NonFueldVehicle", vehicleClassIndPolicy.classIndicatorValuesSize() == 3);
		
		((MWDescriptorInheritancePolicy) nonFueledVehicleDescriptor.getInheritancePolicy()).setIsRoot(true);
		assertTrue("Vehicle should now only have 1 classIndicator", vehicleClassIndPolicy.classIndicatorValuesSize() == 1);
		
		
		((MWDescriptorInheritancePolicy) nonFueledVehicleDescriptor.getInheritancePolicy()).setIsRoot(false);
		assertTrue("There are not 3 class indicator values: Boat, Vehicle, and NonFueldVehicle", vehicleClassIndPolicy.classIndicatorValuesSize() == 3);
	}

}
