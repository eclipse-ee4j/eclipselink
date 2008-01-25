/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.mapping;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import org.eclipse.persistence.platform.database.oracle.NClob;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConversionConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;


public class MWTypeConversionConverterTests 
	extends ModelProblemsTestCase 
{
	public MWTypeConversionConverterTests(String name) {
		super(name);
	}
	
	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", MWTypeConversionConverterTests.class.getName()});
	}
		
	public static Test suite() {
		return new TestSuite(MWTypeConversionConverterTests.class);
	}
	

	public void testNTypeNotSupportedOnPlatformRule() {
		String errorName = ProblemConstants.MAPPING_NTYPE_NOT_SUPPORTED_ON_PLATFORM;
				
		MWDirectToFieldMapping mapping = (MWDirectToFieldMapping) getCrimeSceneDescriptor().mappingNamed("time");
		MWTypeConversionConverter converter = (MWTypeConversionConverter) mapping.getConverter();
		
		converter.setDataType(new MWTypeDeclaration(converter, converter.typeFor(NClob.class)));
		getCrimeSceneProject().getDatabase().setDatabasePlatform(DatabasePlatformRepository.getDefault().platformNamed("Oracle9i"));
		checkMappingsForFalseFailures( errorName, MWDirectToFieldMapping.class );

		getCrimeSceneProject().getDatabase().setDatabasePlatform(DatabasePlatformRepository.getDefault().platformNamed("Attunity"));
		
		
		assertTrue( "should have problem", hasProblem(errorName, mapping));
	
		getCrimeSceneProject().getDatabase().setDatabasePlatform(DatabasePlatformRepository.getDefault().platformNamed("Oracle9i"));
		assertTrue( "restored original values -- should not have problem", !hasProblem(errorName, mapping));
	}
}
