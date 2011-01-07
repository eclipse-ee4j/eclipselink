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
package org.eclipse.persistence.testing.tests.validation;

import java.lang.reflect.Method;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.queries.IndirectListContainerPolicy;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


//used for Method.class

//Created by Ian Reid
//Date: Mar 5, 2k3
//non-standard Test
//This class simply calls the static method to get the code coverage for problem
//for untestable code (i.e. method is never called in source code)

public class ConstructingDescriptorExceptionTests extends AutoVerifyTestCase {
    public ConstructingDescriptorExceptionTests() {
        setDescription("This creates all untestable Descriptor Exceptions by calling each static method contructor for Code Coverage (methods and error codes could be Deprecated)");
    }

    Vector testResults = new Vector();
    EclipseLinkException caughtException = null;

    public void test() throws NoSuchMethodException {
        try {
            Class javaClass = ConstructingDescriptorExceptionTests.class;
            RelationalDescriptor descriptor = new RelationalDescriptor();
            descriptor.setJavaClass(javaClass);
            TestErrorException dummyException = new TestErrorException("no ERROR");
            DirectToFieldMapping mapping = new DirectToFieldMapping();
            ForeignReferenceMapping mappingFR = new OneToOneMapping();
            IndirectListContainerPolicy policy = new IndirectListContainerPolicy();
            Object object = new Object();
            DatabaseRecord row = new DatabaseRecord();
            Class[] parmClasses = { };
            Method method = javaClass.getDeclaredMethod("test", parmClasses);

            DescriptorException descriptorException;

            descriptorException = DescriptorException.javaClassNotSpecified(descriptor);
            if (descriptorException.getErrorCode() != 39)
                testResults.add("DescriptorException.javaClassNotSpecified");
            //    else testResults.add("Passed");
            descriptorException = DescriptorException.writeLockFieldInChildDescriptor(descriptor);
            if (descriptorException.getErrorCode() != 109)
                testResults.add("DescriptorException.writeLockFieldInChildDescriptor");
            //    else testResults.add("Passed");
            descriptorException = DescriptorException.childDoesNotDefineAbstractQueryKeyOfParent(descriptor, descriptor, "dummy");
            if (descriptorException.getErrorCode() != 120)
                testResults.add("DescriptorException.childDoesNotDefineAbstractQueryKeyOfParent");
            //    else testResults.add("Passed");
            descriptorException = DescriptorException.missingIndirectContainerConstructor(javaClass);
            if (descriptorException.getErrorCode() != 145)
                testResults.add("DescriptorException.missingIndirectContainerConstructor");
            //    else testResults.add("Passed");
            descriptorException = DescriptorException.couldNotInstantiateIndirectContainerClass(javaClass, dummyException);
            if (descriptorException.getErrorCode() != 146)
                testResults.add("DescriptorException.couldNotInstantiateIndirectContainerClass");
            //    else testResults.add("Passed");
            descriptorException = DescriptorException.invalidContainerPolicy(policy, javaClass);
            if (descriptorException.getErrorCode() != 147)
                testResults.add("DescriptorException.invalidContainerPolicy");
            //    else testResults.add("Passed");    
            descriptorException = DescriptorException.invalidUseOfNoIndirection(mapping);
            if (descriptorException.getErrorCode() != 149)
                testResults.add("DescriptorException.invalidUseOfNoIndirection");
            //    else testResults.add("Passed");
            descriptorException = DescriptorException.proxyIndirectionNotAvailable(mapping);
            if (descriptorException.getErrorCode() != 159)
                testResults.add("DescriptorException.proxyIndirectionNotAvailable");
            //    else testResults.add("Passed"); 

            descriptorException = DescriptorException.illegalAccesstWhileGettingValueThruInstanceVaraibleAccessor("attributeName", "objectName", dummyException);
            if (descriptorException.getErrorCode() != 13)
                testResults.add("DescriptorException.illegalAccesstWhileGettingValueThruInstanceVaraibleAccessor");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.illegalAccessWhileCloning(object, "methodName", descriptor, dummyException);
            if (descriptorException.getErrorCode() != 14)
                testResults.add("DescriptorException.illegalAccessWhileCloning");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.illegalAccessWhileEventExecution("eventMethodName", descriptor, dummyException);
            if (descriptorException.getErrorCode() != 16)
                testResults.add("DescriptorException.illegalAccessWhileEventExecution");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.illegalAccessWhileGettingValueThruMethodAccessor("methodName", "objectName", dummyException);
            if (descriptorException.getErrorCode() != 17)
                testResults.add("DescriptorException.illegalAccessWhileGettingValueThruMethodAccessor");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.illegalAccessWhileInstantiatingMethodBasedProxy(dummyException);
            if (descriptorException.getErrorCode() != 18)
                testResults.add("DescriptorException.illegalAccessWhileInstantiatingMethodBasedProxy");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.illegalAccessWhileInvokingAttributeMethod(mapping, dummyException);
            if (descriptorException.getErrorCode() != 19)
                testResults.add("DescriptorException.illegalAccessWhileInvokingAttributeMethod");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.illegalAccessWhileInvokingFieldToMethod("methodName", mapping, dummyException);
            if (descriptorException.getErrorCode() != 20)
                testResults.add("DescriptorException.illegalAccessWhileInvokingFieldToMethod");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.illegalAccessWhileInvokingRowExtractionMethod(row, method, descriptor, dummyException);
            if (descriptorException.getErrorCode() != 21)
                testResults.add("DescriptorException.illegalAccessWhileInvokingRowExtractionMethod");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.illegalAccessWhileMethodInstantiation("methodName", descriptor, dummyException);
            if (descriptorException.getErrorCode() != 22)
                testResults.add("DescriptorException.illegalAccessWhileMethodInstantiation");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.illegalAccessWhileObsoleteEventExecute("eventMethodName", descriptor, dummyException);
            if (descriptorException.getErrorCode() != 23)
                testResults.add("DescriptorException.illegalAccessWhileObsoleteEventExecute");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.illegalAccessWhileSettingValueThruInstanceVariableAccessor("attributeName", "objectName", object, dummyException);
            if (descriptorException.getErrorCode() != 24)
                testResults.add("DescriptorException.illegalAccessWhileSettingValueThruInstanceVariableAccessor");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.illegalAccessWhileSettingValueThruMethodAccessor("setMethodName", object, dummyException);
            if (descriptorException.getErrorCode() != 25)
                testResults.add("DescriptorException.illegalAccessWhileSettingValueThruMethodAccessor");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.missingClassForIndicatorFieldValue(object, descriptor);
            if (descriptorException.getErrorCode() != 43)
                testResults.add("DescriptorException.missingClassForIndicatorFieldValue");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.missingClassIndicatorField(row, descriptor);
            if (descriptorException.getErrorCode() != 44)
                testResults.add("DescriptorException.missingClassIndicatorField");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.notDeserializable(mapping, dummyException);
            if (descriptorException.getErrorCode() != 66)
                testResults.add("DescriptorException.notDeserializable");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.notSerializable(mapping, dummyException);
            if (descriptorException.getErrorCode() != 67)
                testResults.add("DescriptorException.notSerializable");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor("attributeName", object, dummyException);
            if (descriptorException.getErrorCode() != 71)
                testResults.add("DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.nullPointerWhileSettingValueThruMethodAccessor("setMethodName", object, dummyException);
            if (descriptorException.getErrorCode() != 72)
                testResults.add("DescriptorException.nullPointerWhileSettingValueThruMethodAccessor");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.securityOnFindMethod("methodName", descriptor, dummyException);
            if (descriptorException.getErrorCode() != 82)
                testResults.add("DescriptorException.securityOnFindMethod");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.securityOnFindObsoleteMethod("methodName", descriptor, dummyException);
            if (descriptorException.getErrorCode() != 83)
                testResults.add("DescriptorException.securityOnFindObsoleteMethod");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.securityOnInitializingAttributeMethod("attributeMethodName", mapping, dummyException);
            if (descriptorException.getErrorCode() != 84)
                testResults.add("DescriptorException.securityOnInitializingAttributeMethod");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.securityWhileConvertingToMethod("methodName", mapping, dummyException);
            if (descriptorException.getErrorCode() != 85)
                testResults.add("DescriptorException.securityWhileConvertingToMethod");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.securityWhileInitializingAttributesInInstanceVariableAccessor("attributeName", "javaClassName", dummyException);
            if (descriptorException.getErrorCode() != 86)
                testResults.add("DescriptorException.securityWhileInitializingAttributesInInstanceVariableAccessor");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.securityWhileInitializingAttributesInMethodAccessor("setMethodName", "getMethodName", "javaClassName");
            if (descriptorException.getErrorCode() != 87)
                testResults.add("DescriptorException.securityWhileInitializingAttributesInMethodAccessor");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.securityWhileInitializingClassExtractionMethod("methodName", descriptor, dummyException);
            if (descriptorException.getErrorCode() != 88)
                testResults.add("DescriptorException.securityWhileInitializingClassExtractionMethod");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.securityWhileInitializingCopyPolicy("methodName", descriptor, dummyException);
            if (descriptorException.getErrorCode() != 89)
                testResults.add("DescriptorException.securityWhileInitializingCopyPolicy");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.securityWhileInitializingInstantiationPolicy("methodName", descriptor, dummyException);
            if (descriptorException.getErrorCode() != 90)
                testResults.add("DescriptorException.securityWhileInitializingInstantiationPolicy");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.sizeMismatchOfForeignKeys(mappingFR);
            if (descriptorException.getErrorCode() != 92)
                testResults.add("DescriptorException.sizeMismatchOfForeignKeys");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.targetForeignKeysSizeMismatch(mappingFR);
            if (descriptorException.getErrorCode() != 96)
                testResults.add("DescriptorException.targetForeignKeysSizeMismatch");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.multipleTableInsertOrderMismatch(descriptor);
            if (descriptorException.getErrorCode() != 143)
                testResults.add("DescriptorException.multipleTableInsertOrderMismatch");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.variableOneToOneMappingIsNotDefinedProperly(mapping, descriptor, "targetKeyName");
            if (descriptorException.getErrorCode() != 166)
                testResults.add("DescriptorException.variableOneToOneMappingIsNotDefinedProperly");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.illegalAccessWhileConstructorInstantiationOfFactory(descriptor, dummyException);
            if (descriptorException.getErrorCode() != 170)
                testResults.add("DescriptorException.illegalAccessWhileConstructorInstantiationOfFactory");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.noSuchMethodWhileConstructorInstantiationOfFactory(descriptor, dummyException);
            if (descriptorException.getErrorCode() != 172)
                testResults.add("DescriptorException.noSuchMethodWhileConstructorInstantiationOfFactory");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.nullPointerWhileConstructorInstantiationOfFactory(descriptor, dummyException);
            if (descriptorException.getErrorCode() != 173)
                testResults.add("DescriptorException.nullPointerWhileConstructorInstantiationOfFactory");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.illegalAccessWhileMethodInstantiationOfFactory("methodName", descriptor, dummyException);
            if (descriptorException.getErrorCode() != 174)
                testResults.add("DescriptorException.illegalAccessWhileMethodInstantiationOfFactory");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.targetInvocationWhileMethodInstantiationOfFactory("methodName", descriptor, dummyException);
            if (descriptorException.getErrorCode() != 175)
                testResults.add("DescriptorException.targetInvocationWhileMethodInstantiationOfFactory");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.nullPointerWhileMethodInstantiationOfFactory("methodName", descriptor, dummyException);
            if (descriptorException.getErrorCode() != 176)
                testResults.add("DescriptorException.nullPointerWhileMethodInstantiationOfFactory");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.mappingForAttributeIsMissing("attributeName", descriptor);
            if (descriptorException.getErrorCode() != 177)
                testResults.add("DescriptorException.mappingForAttributeIsMissing");
            //    else testResults.add("Passed"); 
            descriptorException = DescriptorException.attributeMappingIsMissingForEntityBean("attributeName", "beanName");
            if (descriptorException.getErrorCode() != 178)
                testResults.add("DescriptorException.attributeMappingIsMissingForEntityBean");
            //    else testResults.add("Passed"); 
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    protected void verify() {
        if (caughtException != null) {
            throw new TestErrorException("An exception was thrown:" + org.eclipse.persistence.internal.helper.Helper.cr() + "caught exception was not null! \n\n[EXPECTING] no Exceptions ");
        }
        Iterator iteratorResults = testResults.iterator();
        while (iteratorResults.hasNext()) {
            String item = (String)iteratorResults.next();
            if (!item.equalsIgnoreCase("passed")) {
                throw new TestErrorException("The proper exception was not created:" + org.eclipse.persistence.internal.helper.Helper.cr() + "[Problem with METHOD] " + item);
            }

        }


    }
}
