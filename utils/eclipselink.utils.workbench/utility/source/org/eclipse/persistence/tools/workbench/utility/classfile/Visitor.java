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
package org.eclipse.persistence.tools.workbench.utility.classfile;

import org.eclipse.persistence.tools.workbench.utility.classfile.descriptor.ArrayType;
import org.eclipse.persistence.tools.workbench.utility.classfile.descriptor.BaseType;
import org.eclipse.persistence.tools.workbench.utility.classfile.descriptor.ObjectType;

/**
 * Standard GOF Visitor pattern that allows a visitor object to traverse
 * all the parts of a class file tree.
 */
public interface Visitor {

	void visit(ClassFile classFile);

	void visit(AnnotationDefaultAttribute annotationDefaultAttribute);

	void visit(ArrayType arrayType);

	void visit(AttributePool attributePool);

	void visit(BaseType baseType);

	void visit(ClassConstant classConstant);

	void visit(ClassDeclaration classDeclaration);

	void visit(CodeAttribute codeAttribute);

	void visit(ConstantPool constantPool);

	void visit(ConstantValueAttribute constantValueAttribute);

	void visit(DeprecatedAttribute deprecatedAttribute);

	void visit(DoubleConstant doubleConstant);

	void visit(EnclosingMethodAttribute enclosingMethodAttribute);

	void visit(ExceptionHandler exceptionHandler);

	void visit(ExceptionsAttribute exceptionsAttribute);

	void visit(Field field);

	void visit(FieldPool fieldPool);

	void visit(FieldRefConstant fieldRefConstant);

	void visit(FloatConstant floatConstant);

	void visit(ClassFile.Header header);

	void visit(InnerClass innerClass);

	void visit(InnerClassesAttribute innerClassesAttribute);

	void visit(IntegerConstant integerConstant);

	void visit(InterfaceMethodRefConstant interfaceMethodRefConstant);

	void visit(LineNumber lineNumber);

	void visit(LineNumberTableAttribute lineNumberTableAttribute);

	void visit(LocalVariable localVariable);

	void visit(LocalVariableTableAttribute localVariableTableAttribute);

	void visit(LocalVariableType localVariableType);

	void visit(LocalVariableTypeTableAttribute localVariableTypeTableAttribute);

	void visit(LongConstant longConstant);

	void visit(Method method);

	void visit(MethodPool methodPool);

	void visit(MethodRefConstant methodRefConstant);

	void visit(NameAndTypeConstant nameAndTypeConstant);

	void visit(NullConstant nullConstant);

	void visit(ObjectType objectType);

	void visit(RuntimeInvisibleAnnotationsAttribute runtimeInvisibleAnnotationsAttribute);

	void visit(RuntimeInvisibleParameterAnnotationsAttribute runtimeInvisibleParameterAnnotationsAttribute);

	void visit(RuntimeVisibleAnnotationsAttribute runtimeVisibleAnnotationsAttribute);

	void visit(RuntimeVisibleParameterAnnotationsAttribute runtimeVisibleParameterAnnotationsAttribute);

	void visit(SignatureAttribute signatureAttribute);

	void visit(SourceDebugExtensionAttribute sourceDebugExtensionAttribute);

	void visit(SourceFileAttribute sourceFileAttribute);

	void visit(StringConstant stringConstant);

	void visit(SyntheticAttribute syntheticAttribute);

	void visit(UnknownAttribute unknownAttribute);

	void visit(UTF8Constant utf8Constant);

}
