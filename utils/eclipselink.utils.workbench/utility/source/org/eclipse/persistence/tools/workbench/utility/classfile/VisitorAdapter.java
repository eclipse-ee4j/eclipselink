/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Typical adapter that does nothing, but allows the creation of subclasses
 * that need only implement the relevant methods.
 */
public class VisitorAdapter implements Visitor {

	public VisitorAdapter() {
		super();
	}

	public void visit(ClassFile classFile) {
		// do nothing
	}

	public void visit(AnnotationDefaultAttribute annotationDefaultAttribute) {
		// do nothing
	}

	public void visit(ArrayType arrayType) {
		// do nothing
	}

	public void visit(AttributePool attributePool) {
		// do nothing
	}

	public void visit(BaseType baseType) {
		// do nothing
	}

	public void visit(ClassConstant classConstant) {
		// do nothing
	}

	public void visit(ClassDeclaration classDeclaration) {
		// do nothing
	}

	public void visit(CodeAttribute codeAttribute) {
		// do nothing
	}

	public void visit(ConstantPool constantPool) {
		// do nothing
	}

	public void visit(ConstantValueAttribute constantValueAttribute) {
		// do nothing
	}

	public void visit(DeprecatedAttribute deprecatedAttribute) {
		// do nothing
	}

	public void visit(DoubleConstant doubleConstant) {
		// do nothing
	}

	public void visit(EnclosingMethodAttribute enclosingMethodAttribute) {
		// do nothing
	}

	public void visit(ExceptionHandler exceptionHandler) {
		// do nothing
	}

	public void visit(ExceptionsAttribute exceptionsAttribute) {
		// do nothing
	}

	public void visit(Field field) {
		// do nothing
	}

	public void visit(FieldPool fieldPool) {
		// do nothing
	}

	public void visit(FieldRefConstant fieldRefConstant) {
		// do nothing
	}

	public void visit(FloatConstant floatConstant) {
		// do nothing
	}

	public void visit(ClassFile.Header header) {
		// do nothing
	}

	public void visit(InnerClass innerClass) {
		// do nothing
	}

	public void visit(InnerClassesAttribute innerClassesAttribute) {
		// do nothing
	}

	public void visit(IntegerConstant integerConstant) {
		// do nothing
	}

	public void visit(InterfaceMethodRefConstant interfaceMethodRefConstant) {
		// do nothing
	}

	public void visit(LineNumber lineNumber) {
		// do nothing
	}

	public void visit(LineNumberTableAttribute lineNumberTableAttribute) {
		// do nothing
	}

	public void visit(LocalVariable localVariable) {
		// do nothing
	}

	public void visit(LocalVariableTableAttribute localVariableTableAttribute) {
		// do nothing
	}

	public void visit(LocalVariableType localVariableType) {
		// do nothing
	}

	public void visit(LocalVariableTypeTableAttribute localVariableTypeTableAttribute) {
		// do nothing
	}

	public void visit(LongConstant longConstant) {
		// do nothing
	}

	public void visit(Method method) {
		// do nothing
	}

	public void visit(MethodPool methodPool) {
		// do nothing
	}

	public void visit(MethodRefConstant methodRefConstant) {
		// do nothing
	}

	public void visit(NameAndTypeConstant nameAndTypeConstant) {
		// do nothing
	}

	public void visit(NullConstant nullConstant) {
		// do nothing
	}

	public void visit(ObjectType objectType) {
		// do nothing
	}

	public void visit(RuntimeInvisibleAnnotationsAttribute runtimeInvisibleAnnotationsAttribute) {
		// do nothing
	}

	public void visit(RuntimeInvisibleParameterAnnotationsAttribute runtimeInvisibleParameterAnnotationsAttribute) {
		// do nothing
	}

	public void visit(RuntimeVisibleAnnotationsAttribute runtimeVisibleAnnotationsAttribute) {
		// do nothing
	}

	public void visit(RuntimeVisibleParameterAnnotationsAttribute runtimeVisibleParameterAnnotationsAttribute) {
		// do nothing
	}

	public void visit(SignatureAttribute signatureAttribute) {
		// do nothing
	}

	public void visit(SourceDebugExtensionAttribute sourceDebugExtensionAttribute) {
		// do nothing
	}

	public void visit(SourceFileAttribute sourceFileAttribute) {
		// do nothing
	}

	public void visit(StringConstant stringConstant) {
		// do nothing
	}

	public void visit(SyntheticAttribute syntheticAttribute) {
		// do nothing
	}

	public void visit(UnknownAttribute unknownAttribute) {
		// do nothing
	}

	public void visit(UTF8Constant utf8Constant) {
		// do nothing
	}

}
