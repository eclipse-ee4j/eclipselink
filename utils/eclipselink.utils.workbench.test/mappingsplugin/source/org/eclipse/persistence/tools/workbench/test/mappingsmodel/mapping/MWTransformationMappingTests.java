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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.mapping;

import java.util.Iterator;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.mappings.transformers.AttributeTransformerAdapter;
import org.eclipse.persistence.mappings.transformers.FieldTransformerAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMethodBasedTransformer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethodParameter;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;

public class MWTransformationMappingTests 
	extends ModelProblemsTestCase 
{
	public MWTransformationMappingTests(String name) {
		super(name);
	}
			
	public static Test suite() {
		return new TestSuite(MWTransformationMappingTests.class);
	}
	
	public void testMWRelationalDirectCollectionMappingMapping() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		MWRelationalTransformationMapping original = crimeSceneProject.getHeightMappingInSuspect();
		original.setReadOnly(true);
		assertCommonAttributesEqual(original, original.asMWAggregateMapping());			
	}

	public void testAttributeTransformerNotSpecifiedProblem() {
		String errorName = ProblemConstants.MAPPING_ATTRIBUTE_TRANSFORMER_NOT_SPECIFIED;
		
		CrimeSceneProject csProject = new CrimeSceneProject();
		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		MWMethodBasedTransformer attributeTransformer = (MWMethodBasedTransformer) heightMapping.getAttributeTransformer();
		MWMethod method = attributeTransformer.getMethod();
		heightMapping.clearAttributeTransformer();
		assertTrue("should have problem", hasProblem(errorName, heightMapping));
		
		heightMapping.setAttributeTransformer(method);
		assertTrue("should not have problem", !hasProblem(errorName, heightMapping));
	}
	
	public void testAttributeTransformerMethodMissingProblem() {
		String errorName = ProblemConstants.MAPPING_ATTRIBUTE_TRANSFORMER_METHOD_MISSING;
		
		CrimeSceneProject csProject = new CrimeSceneProject();
		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		MWMethodBasedTransformer attributeTransformer = 
			(MWMethodBasedTransformer) heightMapping.getAttributeTransformer();
		MWMethod method = attributeTransformer.getMethod();
		MWClass type = method.getDeclaringType();
		type.removeMethod(method);
		assertTrue("should have problem", hasProblem(errorName, heightMapping));
		
		MWMethod newMethod = type.addMethod(method.getName(), method.getReturnType());
		for (Iterator stream = method.methodParameters(); stream.hasNext(); ) {
			MWMethodParameter param = (MWMethodParameter) stream.next();
			newMethod.addMethodParameter(param.getType(), param.getDimensionality());
		}
		heightMapping.setAttributeTransformer(newMethod);
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
	}
	
	public void testAttributeTransformerMethodInvalidProblem() {
		String errorName = ProblemConstants.MAPPING_ATTRIBUTE_TRANSFORMER_METHOD_INVALID;
		
		CrimeSceneProject csProject = new CrimeSceneProject();
		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		MWMethodBasedTransformer attributeTransformer = 
			(MWMethodBasedTransformer) heightMapping.getAttributeTransformer();
		MWMethod method = attributeTransformer.getMethod();
		MWMethodParameter param = method.addMethodParameter(method.typeFor(Object.class));
		assertTrue("should have problem", hasProblem(errorName, heightMapping));
		
		method.removeMethodParameter(param);
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
	}
	
	public void testAttributeTransformerMethodNotVisibleProblem() {
		String errorName = ProblemConstants.MAPPING_ATTRIBUTE_TRANSFORMER_METHOD_NOT_VISIBLE;
		
		CrimeSceneProject csProject = new CrimeSceneProject();
		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		// can't really test for failure
	}
	
	public void testAttributeTransformerClassMissingProblem() {
		String errorName = ProblemConstants.MAPPING_ATTRIBUTE_TRANSFORMER_CLASS_MISSING;
		
		CrimeSceneProject csProject = new CrimeSceneProject();
		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		heightMapping.setAttributeTransformer(heightMapping.typeFor(GoodAttributeTransformer.class));
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		// can't remove a class, so can't test for failure
	}
	
	// Until we can reliably verify what a user's class extends or implements,
	// this test has been temporarily removed.
//	public void testAttributeTransformerClassInvalidProblem() {
//		String errorName = ProblemConstants.MAPPING_ATTRIBUTE_TRANSFORMER_CLASS_INVALID;
//		
//		CrimeSceneProject csProject = new CrimeSceneProject();
//		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
//		MWClass goodTransformer = heightMapping.typeFor(GoodAttributeTransformer.class);
//		MWClass badTransformer = heightMapping.typeFor(BadTransformer.class);
//		try {
//			goodTransformer.refresh();
//			badTransformer.refresh();
//		}
//		catch (ExternalClassNotFoundException ecnfe) {
//			// shouldn't happen
//			throw new RuntimeException(ecnfe);
//		}
//		
//		heightMapping.setAttributeTransformer(goodTransformer);
//		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
//		
//		heightMapping.setAttributeTransformer(badTransformer);
//		assertTrue("should have problem", hasProblem(errorName, heightMapping));
//		
//		heightMapping.setAttributeTransformer(goodTransformer);
//		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
//	}
	
	public void testFieldTransformerAssociationsNotSpecifiedProblem() {
		String errorName = ProblemConstants.MAPPING_FIELD_TRANSFORMER_ASSOCIATIONS_NOT_SPECIFIED;
		
		CrimeSceneProject csProject = new CrimeSceneProject();
		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		Vector fieldTransformerAssociations = new Vector();
		for (Iterator stream = heightMapping.fieldTransformerAssociations(); stream.hasNext(); ) {
			fieldTransformerAssociations.add(stream.next());
		}
		
		heightMapping.clearFieldTransformerAssociations();
		assertTrue("should have problem", hasProblem(errorName, heightMapping));
		
		for (Iterator stream = fieldTransformerAssociations.iterator(); stream.hasNext(); ) {
			MWRelationalFieldTransformerAssociation fta = (MWRelationalFieldTransformerAssociation) stream.next();
			heightMapping.addFieldTransformerAssociation(fta.getColumn(), ((MWMethodBasedTransformer) fta.getFieldTransformer()).getMethod());
		}
		assertTrue("should not have problem", !hasProblem(errorName, heightMapping));
	}
	
	public void testFieldTransformerNotSpecifiedProblem() {
		String errorName = ProblemConstants.MAPPING_FIELD_TRANSFORMER_NOT_SPECIFIED;
		
		CrimeSceneProject csProject = new CrimeSceneProject();
		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		// not sure how to test for failure
	}
	
	public void testFieldTransformerMethodMissingProblem() {
		String errorName = ProblemConstants.MAPPING_FIELD_TRANSFORMER_METHOD_MISSING;
		
		CrimeSceneProject csProject = new CrimeSceneProject();
		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		MWColumn heightInchesField = csProject.tableNamed("PERSON").columnNamed("HEIGHT_INCHES");
		MWRelationalFieldTransformerAssociation fieldTransformerAssociation = 
			heightMapping.fieldTransformerAssociationFor(heightInchesField);
		MWMethodBasedTransformer fieldTransformer =	
			(MWMethodBasedTransformer) fieldTransformerAssociation.getFieldTransformer();
		MWMethod method = fieldTransformer.getMethod();
		MWClass type = method.getDeclaringType();
		
		type.removeMethod(method);
		assertTrue("should have problem", hasProblem(errorName, heightMapping));
		
		MWMethod newMethod = type.addMethod(method.getName(), method.getReturnType());
		for (Iterator stream = method.methodParameters(); stream.hasNext(); ) {
			MWMethodParameter param = (MWMethodParameter) stream.next();
			newMethod.addMethodParameter(param.getType(), param.getDimensionality());
		}
		fieldTransformerAssociation.setFieldTransformer(newMethod);
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
	}
	
	public void testFieldTransformerMethodInvalidProblem() {
		String errorName = ProblemConstants.MAPPING_FIELD_TRANSFORMER_METHOD_INVALID;
		
		CrimeSceneProject csProject = new CrimeSceneProject();
		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		MWColumn heightInchesField = csProject.tableNamed("PERSON").columnNamed("HEIGHT_INCHES");
		MWRelationalFieldTransformerAssociation fieldTransformerAssociation = 
			heightMapping.fieldTransformerAssociationFor(heightInchesField);
		MWMethodBasedTransformer fieldTransformer =	
			(MWMethodBasedTransformer) fieldTransformerAssociation.getFieldTransformer();
		MWMethod method = fieldTransformer.getMethod();
		
		MWMethodParameter param = method.addMethodParameter(method.typeFor(Object.class));
		assertTrue("should have problem", hasProblem(errorName, heightMapping));
		
		method.removeMethodParameter(param);
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
	}
	
	public void testFieldTransformerMethodNotVisibleProblem() {
		String errorName = ProblemConstants.MAPPING_FIELD_TRANSFORMER_METHOD_NOT_VISIBLE;
		
		CrimeSceneProject csProject = new CrimeSceneProject();
		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		// can't really test for failure
	}
	
	public void testFieldTransformerClassMissingProblem() {
		String errorName = ProblemConstants.MAPPING_FIELD_TRANSFORMER_CLASS_MISSING;
		
		CrimeSceneProject csProject = new CrimeSceneProject();
		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		MWColumn heightInchesField = csProject.tableNamed("PERSON").columnNamed("HEIGHT_INCHES");
		MWRelationalFieldTransformerAssociation fieldTransformerAssociation = 
			heightMapping.fieldTransformerAssociationFor(heightInchesField);
		fieldTransformerAssociation.setFieldTransformer(heightMapping.typeFor(GoodFieldTransformer.class));
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		// can't remove a class, so can't test for failure
	}
	
	// Until we can reliably verify what a user's class extends or implements,
	// this test has been temporarily removed.
//	public void testFieldTransformerClassInvalidProblem() 
//		throws DuplicateFieldException
//	{
//		String errorName = ProblemConstants.MAPPING_FIELD_TRANSFORMER_CLASS_INVALID;
//		
//		CrimeSceneProject csProject = new CrimeSceneProject();
//		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
//		MWClass goodTransformer = heightMapping.typeFor(GoodFieldTransformer.class);
//		MWClass badTransformer = heightMapping.typeFor(BadTransformer.class);
//		try {
//			goodTransformer.refresh();
//			badTransformer.refresh();
//		}
//		catch (ExternalClassNotFoundException ecnfe) {
//			// shouldn't happen
//			throw new RuntimeException(ecnfe);
//		}
//		MWDatabaseField heightInchesField = csProject.getTableNamed("PERSON").databaseFieldNamed("HEIGHT_INCHES");
//		MWRelationalFieldTransformerAssociation fieldTransformerAssociation = 
//			heightMapping.fieldTransformerAssociationFor(heightInchesField);
//		
//		heightMapping.editFieldTransformerAssociation(fieldTransformerAssociation, heightInchesField, goodTransformer);
//		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
//		
//		heightMapping.editFieldTransformerAssociation(fieldTransformerAssociation, heightInchesField, badTransformer);
//		assertTrue("should have problem", hasProblem(errorName, heightMapping));
//		
//		heightMapping.editFieldTransformerAssociation(fieldTransformerAssociation, heightInchesField, goodTransformer);
//		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
//	}
	
	public void testValueHolderAttributeWithoutValueHolderIndirectionProblem() {
		String errorName = ProblemConstants.MAPPING_VALUE_HOLDER_ATTRIBUTE_WITHOUT_VALUE_HOLDER_INDIRECTION;
		
		CrimeSceneProject csProject = new CrimeSceneProject();
		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
		MWClass attributeType = heightMapping.getInstanceVariable().getType();
		
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		heightMapping.getInstanceVariable().setType(heightMapping.typeFor(ValueHolderInterface.class));
		assertTrue("should have problem", hasProblem(errorName, heightMapping));
		
		heightMapping.getInstanceVariable().setType(attributeType);
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
	}
	
	public void testValueHolderIndirectionWithoutValueHolderAttributeProblem() {
		String errorName = ProblemConstants.MAPPING_VALUE_HOLDER_INDIRECTION_WITHOUT_VALUE_HOLDER_ATTRIBUTE;
		
		CrimeSceneProject csProject = new CrimeSceneProject();
		MWRelationalTransformationMapping heightMapping = csProject.getHeightMappingInSuspect();
		
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
		
		heightMapping.setUseValueHolderIndirection();
		assertTrue("should have problem", hasProblem(errorName, heightMapping));
		
		heightMapping.setUseNoIndirection();
		assertTrue("should not have problem", ! hasProblem(errorName, heightMapping));
	}
	
	// **************** Member classes ****************************************
	
	private class GoodAttributeTransformer
		extends AttributeTransformerAdapter {}
	
	private class GoodFieldTransformer
		extends FieldTransformerAdapter {}
	
	private class BadTransformer {}
}
