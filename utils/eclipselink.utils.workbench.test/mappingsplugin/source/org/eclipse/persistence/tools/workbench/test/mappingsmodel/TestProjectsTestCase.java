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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverterMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToXmlTypeMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.test.models.eis.employee.LargeProject;
import org.eclipse.persistence.tools.workbench.test.models.eis.employee.Project;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeEisProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeOXProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.QueryProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.SimpleContactProject;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

public abstract class TestProjectsTestCase extends TestCase {
	private MWRelationalProject crimeSceneProject;
	private MWRelationalProject contactProject;
	private MWRelationalProject queryProject;
	private MWEisProject employeeEisProject;
	private MWOXProject employeeOXProject;
	

	protected TestProjectsTestCase(String name) {
		super(name);
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	protected MWRelationalProject getCrimeSceneProject() {
		if (this.crimeSceneProject == null) {
			this.crimeSceneProject = new CrimeSceneProject().getProject();
		}
		return this.crimeSceneProject;
	}
	
	protected MWRelationalProject getContactProject() {
		if (this.contactProject == null) {
			this.contactProject = new SimpleContactProject().getProject();
		}
		return this.contactProject;
	}
	
	protected MWRelationalProject getQueryProject() {
		if (this.queryProject == null) {
			this.queryProject = new QueryProject().getProject();
		}
		return this.queryProject;
	}
	
	protected MWEisProject getEmployeeEisProject() {
		if (this.employeeEisProject == null) {
			this.employeeEisProject = (MWEisProject) new EmployeeEisProject().getProject();
		}
		return this.employeeEisProject;
	}
	
	protected MWOXProject getEmployeeOXProject() {
		if (this.employeeOXProject == null) {
			this.employeeOXProject = (MWOXProject) new EmployeeOXProject().getProject();
		}
		return this.employeeOXProject;
	}

	protected MWTableDescriptor getCrimeSceneDescriptor() {
		MWClass crimeSceneClass = getCrimeSceneProject().typeFor(org.eclipse.persistence.tools.workbench.test.models.crimescene.CrimeScene.class);
		return (MWTableDescriptor) getCrimeSceneProject().descriptorForType(crimeSceneClass);
	}
	
	protected MWTableDescriptor getPhoneNumberDescriptor() {
		MWClass phoneNumberClass = getQueryProject().typeFor(org.eclipse.persistence.tools.workbench.test.models.query.PhoneNumber.class);
		return (MWTableDescriptor) getQueryProject().descriptorForType(phoneNumberClass);
	}
	
	protected MWDatabase getDatabase() {
		return getCrimeSceneProject().getDatabase();
	}

	/**
	 * return the first mapping in the project that is an instance of the mapping class
	 */
	protected MWMapping getMappingForClass( Class mappingClass, MWProject project ) {
		for (Iterator descriptors = project.descriptors(); descriptors.hasNext(); ) {
			MWMappingDescriptor descriptor = (MWMappingDescriptor) descriptors.next();
			for (Iterator mappings = descriptor.mappings(); mappings.hasNext(); ) {
				MWMapping mapping = (MWMapping) mappings.next();
				if (mappingClass.isInstance(mapping)) {
					return mapping;
				}
			}
		}
		return null;
	}

	/**
	 * return the first mapping in the project that has the specified name
	 */
	protected MWMapping getMappingNamed( String mappingName, MWProject project ) {
		if (mappingName == null || mappingName.equals("")) {
			return null;
		}
		for (Iterator descriptors = project.descriptors(); descriptors.hasNext(); ) {
			MWMappingDescriptor descriptor = (MWMappingDescriptor) descriptors.next();
			for (Iterator mappings = descriptor.mappings(); mappings.hasNext(); ) {
				MWMapping mapping = (MWMapping) mappings.next();
				if (mappingName.equals(mapping.getName())) {
					return mapping;
				}
			}
		}
		return null;
	}

	/**
	 * return all of the mappings in the project that are
	 * instances of the mappingClass.
	 */
	protected Collection getMappingsForClass( Class mappingClass, MWProject project ) {
		Collection allMappings = new ArrayList();
		for (Iterator descriptors = project.mappingDescriptors(); descriptors.hasNext(); ) {
			MWMappingDescriptor descriptor = (MWMappingDescriptor) descriptors.next();
			for (Iterator mappings = descriptor.mappings(); mappings.hasNext(); ) {
				MWMapping mapping = (MWMapping) mappings.next();
				if (mappingClass.isInstance(mapping)) {
					allMappings.add(mapping);
				}
			}
		}
		return allMappings;
	}
	
	protected MWTableDescriptor getPersonDescriptor() {
		MWClass personClass = getCrimeSceneProject().typeFor(org.eclipse.persistence.tools.workbench.test.models.crimescene.Person.class);
		return (MWTableDescriptor) getCrimeSceneProject().descriptorForType(personClass);
	}

	protected MWTableDescriptor getPieceOfEvidenceDescriptor() {
		MWClass poeClass = getCrimeSceneProject().typeFor(org.eclipse.persistence.tools.workbench.test.models.crimescene.PieceOfEvidence.class);
		return (MWTableDescriptor) getCrimeSceneProject().descriptorForType(poeClass);
	}
	
	protected MWTable getTableNamed( String tableName ) {
		if (tableName == null || tableName.equals("")) {
			return null;
		}
		return getCrimeSceneProject().getDatabase().tableNamed(tableName);
	}
	
	protected MWTableDescriptor getVictimDescriptor() {
		MWClass victimClass = getCrimeSceneProject().typeFor(org.eclipse.persistence.tools.workbench.test.models.crimescene.Victim.class);
		return (MWTableDescriptor) getCrimeSceneProject().descriptorForType(victimClass);
	}
	
	protected MWEisDescriptor getEmployeeEisDescriptor() {
		MWClass employeeClass = getEmployeeEisProject().typeFor(org.eclipse.persistence.tools.workbench.test.models.eis.employee.Employee.class);
		return (MWEisDescriptor) getEmployeeEisProject().descriptorForType(employeeClass);
	}

	protected MWOXDescriptor getEmployeeOXDescriptor() {
		MWClass employeeClass = getEmployeeOXProject().typeFor(org.eclipse.persistence.tools.workbench.test.models.xml.employee.Employee.class);
		return (MWOXDescriptor) getEmployeeOXProject().descriptorForType(employeeClass);
	}

	protected MWEisDescriptor getProjectEisDescriptor() {
		MWClass projectClass = getEmployeeEisProject().typeFor(Project.class);
		return (MWEisDescriptor) getEmployeeEisProject().descriptorForType(projectClass);
	}
	
	protected MWEisDescriptor getLargeProjectEisDescriptor() {
		MWClass largeProjectClass = getEmployeeEisProject().typeFor(LargeProject.class);
		return (MWEisDescriptor) getEmployeeEisProject().descriptorForType(largeProjectClass);
	}
	
	protected void assertCommonAttributesEqual(MWCollectionMapping mapping1, MWCollectionMapping mapping2) {
		assertCommonAttributesEqual((MWAbstractTableReferenceMapping) mapping1, (MWAbstractTableReferenceMapping) mapping2);
		assertEquals(mapping1.getContainerPolicy().getDefaultingContainerClass().getContainerClass(), mapping2.getContainerPolicy().getDefaultingContainerClass().getContainerClass());
        if (mapping1.getContainerPolicy() instanceof MWMapContainerPolicy) {
            assertEquals(((MWMapContainerPolicy) mapping1.getContainerPolicy()).getKeyMethod(), ((MWMapContainerPolicy) mapping2.getContainerPolicy()).getKeyMethod());
        }
		assertEquals(mapping1.usesTransparentIndirection(), mapping2.usesTransparentIndirection());
	}
	
	protected void assertCommonAttributesEqual(MWCollectionMapping mapping1, MWRelationalDirectCollectionMapping mapping2) {
		assertCommonAttributesEqual((MWAbstractTableReferenceMapping) mapping1, mapping2);
		assertEquals(mapping1.getContainerPolicy().getDefaultingContainerClass().getContainerClass(), mapping2.getContainerPolicy().getDefaultingContainerClass().getContainerClass());
		assertEquals(mapping1.usesTransparentIndirection(), mapping2.usesTransparentIndirection());
	}
	
	protected void assertCommonAttributesEqual(MWDirectToXmlTypeMapping mapping1, MWDirectToXmlTypeMapping mapping2) {
		assertCommonAttributesEqual((MWDirectMapping) mapping1, (MWDirectMapping) mapping2);
		assertEquals(mapping1.getColumn(), mapping2.getColumn());
	}

	protected void assertCommonAttributesEqual(MWDirectMapping mapping1, MWDirectMapping mapping2) {
		assertCommonAttributesEqual((MWMapping) mapping1, (MWMapping) mapping2);
	}

	protected void assertCommonAttributesEqual(MWDirectToFieldMapping mapping1, MWDirectToFieldMapping mapping2) {
		assertCommonAttributesEqual((MWDirectMapping) mapping1, (MWDirectMapping) mapping2);
		assertEquals(mapping1.getNullValuePolicy(), mapping2.getNullValuePolicy());
	}

	protected void assertCommonAttributesEqual(MWMapping mapping1, MWMapping mapping2) {
		assertEquals(mapping1.getName(), mapping2.getName());
		assertEquals(mapping1.getInstanceVariable().getName(), mapping2.getInstanceVariable().getName());
		assertEquals(mapping1.usesMethodAccessing(), mapping2.usesMethodAccessing());
		assertEquals(mapping1.getGetMethod(), mapping2.getGetMethod());
		assertEquals(mapping1.getSetMethod(), mapping2.getSetMethod());
		assertEquals(mapping1.isReadOnly(), mapping2.isReadOnly());
		assertEquals(mapping1.isInherited(), mapping2.isInherited());
	}
	
	protected void assertCommonAttributesEqual(MWAbstractTableReferenceMapping mapping1, MWAggregateMapping mapping2) {
		assertCommonAttributesEqual((MWMapping) mapping1, (MWMapping) mapping2);
		assertEquals(mapping1.getReferenceDescriptor(), mapping2.getReferenceDescriptor());
	}

	protected void assertCommonAttributesEqual(MWAbstractTableReferenceMapping mapping1, MWRelationalDirectContainerMapping mapping2) {
		assertCommonAttributesEqual((MWMapping) mapping1, (MWMapping) mapping2);
		assertEquals(mapping1.getReference(), mapping2.getReference());
		assertEquals(mapping1.usesValueHolderIndirection(), mapping2.usesValueHolderIndirection());
		assertEquals(mapping1.usesBatchReading(), mapping2.usesBatchReading());
	}
	
	protected void assertCommonAttributesEqual(MWAbstractTableReferenceMapping mapping1, MWAbstractTableReferenceMapping mapping2) {
		assertCommonAttributesEqual((MWMapping) mapping1, (MWMapping) mapping2);
		assertEquals(mapping1.isPrivateOwned(), mapping2.isPrivateOwned());
		assertEquals(mapping1.usesBatchReading(), mapping2.usesBatchReading());
		assertEquals(mapping1.getReferenceDescriptor(), mapping2.getReferenceDescriptor());
		assertEquals(mapping1.usesValueHolderIndirection(), mapping2.usesValueHolderIndirection());
		assertEquals(mapping1.getReference(), mapping2.getReference());
	}
	
	protected void assertCommonAttributesEqual(MWRelationalDirectContainerMapping mapping1, MWRelationalDirectContainerMapping mapping2) {
		assertCommonAttributesEqual((MWDirectContainerMapping) mapping1, (MWDirectContainerMapping) mapping2);
		assertEquals(mapping1.getTargetTable(), mapping2.getTargetTable());
		assertEquals(mapping1.getDirectValueColumn(), mapping2.getDirectValueColumn());
		assertEquals(mapping1.getReference(), mapping2.getReference());
		assertEquals(mapping1.usesValueHolderIndirection(), mapping2.usesValueHolderIndirection());
		assertEquals(mapping1.usesTransparentIndirection(), mapping2.usesTransparentIndirection());
		assertEquals(mapping1.usesBatchReading(), mapping2.usesBatchReading());
	}
	
	protected void assertCommonAttributesEqual(MWDirectContainerMapping mapping1, MWDirectContainerMapping mapping2) {
		assertCommonAttributesEqual((MWConverterMapping) mapping1, (MWConverterMapping) mapping2);
	}
	
	protected void assertCommonAttributesEqual(MWConverterMapping mapping1, MWConverterMapping mapping2) {
		assertCommonAttributesEqual((MWMapping) mapping1, (MWMapping) mapping2);
		assertEquals(mapping1.getConverter().getType(), mapping2.getConverter().getType());
	}

}
