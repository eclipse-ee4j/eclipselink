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
package org.eclipse.persistence.tools.workbench.test.mappingsplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.codegen.AccessLevel;
import org.eclipse.persistence.internal.codegen.ClassDefinition;
import org.eclipse.persistence.internal.codegen.CodeDefinition;
import org.eclipse.persistence.internal.codegen.CodeGenerator;
import org.eclipse.persistence.internal.codegen.NonreflectiveAttributeDefinition;
import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;

public class CodeDefinitionTests 
	extends TestCase 
{
	public static Test suite() {
		return new TestSuite(CodeDefinitionTests.class);
	}

	public CodeDefinitionTests(String name)
	{
		super(name);
	}
	
	public void testCalculateImports()
	{
		ClassDefinition classToTest = buildClassDefinition();
		classToTest.calculateImports();
		
		CodeGenerator generator = new CodeGenerator();
		StringWriter writer = new StringWriter();
		generator.setOutput(writer);
		classToTest.write(generator);
		
		try
		{
			String lineSep = System.getProperty("line.separator");
			File file = FileTools.resourceFile("/export/MyClassWithCalculatedImports.java");
   		BufferedReader reader = new BufferedReader(new FileReader(file));
			String nextLine = "";
			StringBuffer pregeneratedOutput = new StringBuffer();
			
			while ((nextLine = reader.readLine()) != null) 
			{
				pregeneratedOutput.append(nextLine);
				// note: BufferedReader strips the EOL character.
				pregeneratedOutput.append(lineSep);
			}
		
			String expectedOutput = pregeneratedOutput.toString();
			String actualOutput = writer.getBuffer().toString();
			assertEquals("The generated java file was not equal to the pregenerated output file.", expectedOutput, actualOutput);
		}
		catch (Exception ex)
		{
			assertTrue("An exception occurred reading \"MyClassWithCalculatedImports.java\".  Check your test setup.", false);
		}	
	}
	
	public void testParseForTypeNames()
	{
		Set typeNames = (Set) ClassTools.invokeStaticMethod(CodeDefinition.class, "parseForTypeNames", String.class, "java.util.Collection(*&&& org.eclipse.persistence.indirection.Foo!!!");
		
		assertTrue("java.util.Collection was not in the returned type names.", typeNames.contains("java.util.Collection"));
		assertTrue("org.eclipse.persistence.indirection.Foo was not in the returned type names.", typeNames.contains("org.eclipse.persistence.indirection.Foo"));
	}
	
	private ClassDefinition buildClassDefinition()
	{
		ClassDefinition classToTest = new ClassDefinition();
		
		classToTest.setAccessLevel(new AccessLevel(AccessLevel.PUBLIC));
		classToTest.setName("MyClassWithCalculatedImports");
		classToTest.setComment("\"No comment\" - Richard M. Nixon");
		classToTest.setPackageName("mypackage");
		classToTest.setType(ClassDefinition.CLASS_TYPE);
		classToTest.setSuperClass("mypackage.MySuperclass");
		classToTest.addInterface("java.io.Serializable");
		classToTest.addInterface("myotherpackage.MyInterface");
		
		
		// Attributes
		
		NonreflectiveAttributeDefinition attributeA = new NonreflectiveAttributeDefinition();
		attributeA.setAccessLevel(new AccessLevel(AccessLevel.PUBLIC));
		attributeA.setType("myotherpackage.TypeSpecifiedByName");
		attributeA.setName("a_attributeSpecifiedByName");
		classToTest.addAttribute(attributeA);
		
		NonreflectiveAttributeDefinition attributeB = new NonreflectiveAttributeDefinition();
		attributeB.setAccessLevel(new AccessLevel(AccessLevel.PUBLIC));
		attributeB.setType("java.util.Vector");
		attributeB.setName("b_attributeSpecifiedByType");
		classToTest.addAttribute(attributeB);
		
		NonreflectiveAttributeDefinition attributeC = new NonreflectiveAttributeDefinition();
		attributeC.setAccessLevel(new AccessLevel(AccessLevel.PUBLIC));
		attributeC.setType("java.lang.String");
		attributeC.setName("c_attributeWithJavaLangType");
		classToTest.addAttribute(attributeC);
		
		NonreflectiveAttributeDefinition attributeD = new NonreflectiveAttributeDefinition();
		attributeD.setAccessLevel(new AccessLevel(AccessLevel.PROTECTED));
		attributeD.setType("mypackage.AnotherTypeInTheSamePackage");
		attributeD.setName("d_attributeWithTypeInSamePackage");
		classToTest.addAttribute(attributeD);
		
		NonreflectiveAttributeDefinition attributeE = new NonreflectiveAttributeDefinition();
		attributeE.setAccessLevel(new AccessLevel(AccessLevel.PROTECTED));
		attributeE.setType("myotherpackage.TypeInRepeatedPackage");
		attributeE.setName("e_attributeWithTypeInRepeatedPackage");
		classToTest.addAttribute(attributeE);
		
		NonreflectiveAttributeDefinition attributeF = new NonreflectiveAttributeDefinition();
		attributeF.setAccessLevel(new AccessLevel(AccessLevel.PROTECTED));
		attributeF.setType("java.lang.Object");
		attributeF.setName("f_attributeWithInitialValue");
		attributeF.setInitialValue("new java.lang.String()");
		classToTest.addAttribute(attributeF);
		
		NonreflectiveAttributeDefinition attributeG = new NonreflectiveAttributeDefinition();
		attributeG.setAccessLevel(new AccessLevel(AccessLevel.PRIVATE));
		attributeG.setType("mythirdpackage.ClassInThirdPackage");
		attributeG.setName("g_attributeWithTypeInThirdPackage");
		classToTest.addAttribute(attributeG);
		
		NonreflectiveAttributeDefinition attributeH = new NonreflectiveAttributeDefinition();
		attributeH.setAccessLevel(new AccessLevel(AccessLevel.PRIVATE));
		attributeH.setType("org.eclipse.persistence.indirection.ValueHolderInterface");
		attributeH.setName("h_anAttributeWithIndirectionInitialValue");
		attributeH.setInitialValue("new org.eclipse.persistence.indirection.ValueHolder()");
		classToTest.addAttribute(attributeH);
		
		NonreflectiveAttributeDefinition attributeI = new NonreflectiveAttributeDefinition();
		attributeI.setAccessLevel(new AccessLevel(AccessLevel.PRIVATE));
		attributeI.setType("mypackage.AmbiguousType");
		attributeI.setName("i_attributeWithAmbiguousType");
		classToTest.addAttribute(attributeI);
		
		NonreflectiveAttributeDefinition attributeJ = new NonreflectiveAttributeDefinition();
		attributeJ.setAccessLevel(new AccessLevel(AccessLevel.PRIVATE));
		attributeJ.setType("myotherpackage.AmbiguousType");
		attributeJ.setName("j_attributeWithAmbiguousType");
		classToTest.addAttribute(attributeJ);
		
		NonreflectiveAttributeDefinition attributeK = new NonreflectiveAttributeDefinition();
		attributeK.setAccessLevel(new AccessLevel(AccessLevel.PRIVATE));
		attributeK.setType("int");
		attributeK.setName("k_attributeWithPrimitiveType");
		classToTest.addAttribute(attributeK);
		
		
		// Methods
		
		NonreflectiveMethodDefinition constructor = new NonreflectiveMethodDefinition();
		constructor.setAccessLevel(new AccessLevel(AccessLevel.PUBLIC));
		constructor.setIsConstructor(true);
		constructor.setName("MyClassWithCalculatedImports");
		classToTest.addMethod(constructor);
		
		NonreflectiveMethodDefinition methodA = new NonreflectiveMethodDefinition();
		methodA.setAccessLevel(new AccessLevel(AccessLevel.PUBLIC));
		methodA.setName("a_methodWithNoArgument");
		classToTest.addMethod(methodA);
		
		NonreflectiveMethodDefinition methodB = new NonreflectiveMethodDefinition();
		methodB.setAccessLevel(new AccessLevel(AccessLevel.PUBLIC));
		methodB.setName("b_methodWithArgumentSpecifiedByName");
		methodB.addArgument("myotherpackage.TypeSpecifiedByName", "argumentSpecifiedByName");
		classToTest.addMethod(methodB);
		
		NonreflectiveMethodDefinition methodC = new NonreflectiveMethodDefinition();
		methodC.setAccessLevel(new AccessLevel(AccessLevel.PUBLIC));
		methodC.setName("c_methodWithArgumentSpecifiedByType");
		methodC.addArgument("java.math.BigDecimal", "argumentSpecifiedByType");
		classToTest.addMethod(methodC);
		
		NonreflectiveMethodDefinition methodD = new NonreflectiveMethodDefinition();
		methodD.setAccessLevel(new AccessLevel(AccessLevel.PROTECTED));
		methodD.setReturnType("java.util.Vector");
		methodD.setName("d_methodWithReturnType");
		classToTest.addMethod(methodD);
		
		NonreflectiveMethodDefinition methodE = new NonreflectiveMethodDefinition();
		methodE.setAccessLevel(new AccessLevel(AccessLevel.PROTECTED));
		methodE.setReturnType("java.util.List");
		methodE.setName("e_methodWithMethodBody");
		methodE.addLine("java.util.List myList = (java.util.List) c_methodWithReturnType();");
		methodE.addLine("return myList;");
		classToTest.addMethod(methodE);
		
		NonreflectiveMethodDefinition methodF = new NonreflectiveMethodDefinition();
		methodF.setAccessLevel(new AccessLevel(AccessLevel.PROTECTED));
		methodF.setReturnType("mypackage.MyClassWithCalculatedImports");
		methodF.setName("f_methodWithThisClassAsReturnType");
		classToTest.addMethod(methodF);
		
		NonreflectiveMethodDefinition methodG = new NonreflectiveMethodDefinition();
		methodG.setAccessLevel(new AccessLevel(AccessLevel.PROTECTED));
		methodG.setReturnType("mypackage.mysubpackage.ClassInSubpackage");
		methodG.setName("g_methodWithReturnTypeInSubpackage");
		classToTest.addMethod(methodG);
		
		NonreflectiveMethodDefinition methodH = new NonreflectiveMethodDefinition();
		methodH.setAccessLevel(new AccessLevel(AccessLevel.PRIVATE));
		methodH.setReturnType("int");
		methodH.setName("h_methodWithAmbiguousArgumentType");
		methodH.addArgument("myotherpackage.AmbiguousType", "argumentWithAmbiguousType");
		classToTest.addMethod(methodH);
		
		NonreflectiveMethodDefinition methodI = new NonreflectiveMethodDefinition();
		methodI.setAccessLevel(new AccessLevel(AccessLevel.PRIVATE));
		methodI.setReturnType("java.lang.Object");
		methodI.setName("i_methodWithMultipleArgumentTypes");
		methodI.addArgument("mypackage.AnotherTypeInTheSamePackage", "argument1");
		methodI.addArgument("int[]", "argument2");
		classToTest.addMethod(methodI);
		
		NonreflectiveMethodDefinition methodJ = new NonreflectiveMethodDefinition();
		methodJ.setAccessLevel(new AccessLevel(AccessLevel.PRIVATE));
		methodJ.setReturnType("java.lang.Byte[]");
		methodJ.setName("j_methodWithArrayReturnType");
		classToTest.addMethod(methodJ);
		
		NonreflectiveMethodDefinition methodK = new NonreflectiveMethodDefinition();
		methodK.setAccessLevel(new AccessLevel(AccessLevel.PRIVATE));
		methodK.setName("k_methodWithThrownException");
		methodK.addException("java.io.IOException");
		classToTest.addMethod(methodK);
		
		NonreflectiveMethodDefinition methodL = new NonreflectiveMethodDefinition();
		methodL.setAccessLevel(new AccessLevel(AccessLevel.PRIVATE));
		methodL.setName("l_methodWithArrayArgumentType");
		methodL.addArgument("java.net.URL[]", "argumentWithArrayType");
		classToTest.addMethod(methodL);
		
		return classToTest;
	}
}
