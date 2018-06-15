/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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

    @Override
    public void visit(ClassFile classFile) {
        // do nothing
    }

    @Override
    public void visit(AnnotationDefaultAttribute annotationDefaultAttribute) {
        // do nothing
    }

    @Override
    public void visit(ArrayType arrayType) {
        // do nothing
    }

    @Override
    public void visit(AttributePool attributePool) {
        // do nothing
    }

    @Override
    public void visit(BaseType baseType) {
        // do nothing
    }

    @Override
    public void visit(ClassConstant classConstant) {
        // do nothing
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        // do nothing
    }

    @Override
    public void visit(CodeAttribute codeAttribute) {
        // do nothing
    }

    @Override
    public void visit(ConstantPool constantPool) {
        // do nothing
    }

    @Override
    public void visit(ConstantValueAttribute constantValueAttribute) {
        // do nothing
    }

    @Override
    public void visit(DeprecatedAttribute deprecatedAttribute) {
        // do nothing
    }

    @Override
    public void visit(DoubleConstant doubleConstant) {
        // do nothing
    }

    @Override
    public void visit(EnclosingMethodAttribute enclosingMethodAttribute) {
        // do nothing
    }

    @Override
    public void visit(ExceptionHandler exceptionHandler) {
        // do nothing
    }

    @Override
    public void visit(ExceptionsAttribute exceptionsAttribute) {
        // do nothing
    }

    @Override
    public void visit(Field field) {
        // do nothing
    }

    @Override
    public void visit(FieldPool fieldPool) {
        // do nothing
    }

    @Override
    public void visit(FieldRefConstant fieldRefConstant) {
        // do nothing
    }

    @Override
    public void visit(FloatConstant floatConstant) {
        // do nothing
    }

    @Override
    public void visit(ClassFile.Header header) {
        // do nothing
    }

    @Override
    public void visit(InnerClass innerClass) {
        // do nothing
    }

    @Override
    public void visit(InnerClassesAttribute innerClassesAttribute) {
        // do nothing
    }

    @Override
    public void visit(IntegerConstant integerConstant) {
        // do nothing
    }

    @Override
    public void visit(InterfaceMethodRefConstant interfaceMethodRefConstant) {
        // do nothing
    }

    @Override
    public void visit(InvokeDynamicConstant invokeDynamicConstant) {
        // do nothing
    }

    @Override
    public void visit(LineNumber lineNumber) {
        // do nothing
    }

    @Override
    public void visit(LineNumberTableAttribute lineNumberTableAttribute) {
        // do nothing
    }

    @Override
    public void visit(LocalVariable localVariable) {
        // do nothing
    }

    @Override
    public void visit(LocalVariableTableAttribute localVariableTableAttribute) {
        // do nothing
    }

    @Override
    public void visit(LocalVariableType localVariableType) {
        // do nothing
    }

    @Override
    public void visit(LocalVariableTypeTableAttribute localVariableTypeTableAttribute) {
        // do nothing
    }

    @Override
    public void visit(LongConstant longConstant) {
        // do nothing
    }

    @Override
    public void visit(Method method) {
        // do nothing
    }

    @Override
    public void visit(MethodHandleConstant methodHandleConstant) {
        // do nothing
    }

    @Override
    public void visit(MethodPool methodPool) {
        // do nothing
    }

    @Override
    public void visit(MethodRefConstant methodRefConstant) {
        // do nothing
    }

    @Override
    public void visit(MethodTypeConstant methodTypeConstant) {
        // do nothing
    }

    @Override
    public void visit(NameAndTypeConstant nameAndTypeConstant) {
        // do nothing
    }

    @Override
    public void visit(NullConstant nullConstant) {
        // do nothing
    }

    @Override
    public void visit(ObjectType objectType) {
        // do nothing
    }

    @Override
    public void visit(RuntimeInvisibleAnnotationsAttribute runtimeInvisibleAnnotationsAttribute) {
        // do nothing
    }

    @Override
    public void visit(RuntimeInvisibleParameterAnnotationsAttribute runtimeInvisibleParameterAnnotationsAttribute) {
        // do nothing
    }

    @Override
    public void visit(RuntimeVisibleAnnotationsAttribute runtimeVisibleAnnotationsAttribute) {
        // do nothing
    }

    @Override
    public void visit(RuntimeVisibleParameterAnnotationsAttribute runtimeVisibleParameterAnnotationsAttribute) {
        // do nothing
    }

    @Override
    public void visit(SignatureAttribute signatureAttribute) {
        // do nothing
    }

    @Override
    public void visit(SourceDebugExtensionAttribute sourceDebugExtensionAttribute) {
        // do nothing
    }

    @Override
    public void visit(SourceFileAttribute sourceFileAttribute) {
        // do nothing
    }

    @Override
    public void visit(StringConstant stringConstant) {
        // do nothing
    }

    @Override
    public void visit(SyntheticAttribute syntheticAttribute) {
        // do nothing
    }

    @Override
    public void visit(UnknownAttribute unknownAttribute) {
        // do nothing
    }

    @Override
    public void visit(UTF8Constant utf8Constant) {
        // do nothing
    }

}
