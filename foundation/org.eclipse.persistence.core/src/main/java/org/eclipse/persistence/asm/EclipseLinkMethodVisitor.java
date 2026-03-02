/*
 * Copyright (c) 2023, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.asm;

public class EclipseLinkMethodVisitor extends MethodVisitor {

    protected MethodVisitor methodVisitor;

    public EclipseLinkMethodVisitor() {
        this.methodVisitor = ASMFactory.createMethodVisitor(Opcodes.ASM_API_SELECTED);
    }

    public EclipseLinkMethodVisitor(MethodVisitor methodVisitor) {
        this.methodVisitor = ASMFactory.createMethodVisitor(Opcodes.ASM_API_SELECTED, methodVisitor);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        this.methodVisitor.visitVarInsn(opcode, var);
    }

    @Override
    public void visitVarInsnSuper(int opcode, int var) {
        this.methodVisitor.visitVarInsnSuper(opcode, var);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        this.methodVisitor.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitMethodInsnSuper(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        this.methodVisitor.visitMethodInsnSuper(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInsn(int opcode) {
        this.methodVisitor.visitInsn(opcode);
    }

    @Override
    public void visitInsnSuper(int opcode) {
        this.methodVisitor.visitInsnSuper(opcode);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        this.methodVisitor.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitMaxsSuper(int maxStack, int maxLocals) {
        this.methodVisitor.visitMaxsSuper(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        this.methodVisitor.visitEnd();
    }

    @Override
    public void visitCode() {
        this.methodVisitor.visitCode();
    }

    @Override
    public void visitLdcInsn(Object value) {
        this.methodVisitor.visitLdcInsn(value);
    }

    @Override
    public void visitLdcInsnSuper(Object value) {
        this.methodVisitor.visitLdcInsnSuper(value);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        this.methodVisitor.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitTypeInsnSuper(int opcode, String type) {
        this.methodVisitor.visitTypeInsnSuper(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        this.methodVisitor.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitFieldInsnSuper(int opcode, String owner, String name, String descriptor) {
        this.methodVisitor.visitFieldInsnSuper(opcode, owner, name, descriptor);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        this.methodVisitor.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitIntInsnSuper(int opcode, int operand) {
        this.methodVisitor.visitIntInsnSuper(opcode, operand);
    }

    @Override
    public void visitLabel(Label label) {
        this.methodVisitor.visitLabel(label);
    }

    @Override
    public void visitLabelSuper(Label label) {
        this.methodVisitor.visitLabelSuper(label);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        this.methodVisitor.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitJumpInsnSuper(int opcode, Label label) {
        this.methodVisitor.visitJumpInsnSuper(opcode, label);
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        this.methodVisitor.visitFrame(type, numLocal, local, numStack, stack);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.methodVisitor.visitLineNumber(line, start);
    }

    @Override
    public void visitLineNumberSuper(int line, Label start) {
        this.methodVisitor.visitLineNumberSuper(line, start);
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        this.methodVisitor.visitLocalVariable(name, descriptor, signature, start, end, index);
    }

    @Override
    public void visitLocalVariableSuper(String name, String descriptor, String signature, Label start, Label end, int index) {
        this.methodVisitor.visitLocalVariableSuper(name, descriptor, signature, start, end, index);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        this.methodVisitor.visitIincInsn(var, increment);
    }

    @Override
    public void visitIincInsnSuper(int var, int increment) {
        this.methodVisitor.visitIincInsnSuper(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        this.methodVisitor.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitTableSwitchInsnSuper(int min, int max, Label dflt, Label... labels) {
        this.methodVisitor.visitTableSwitchInsnSuper(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        this.methodVisitor.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitLookupSwitchInsnSuper(Label dflt, int[] keys, Label[] labels) {
        this.methodVisitor.visitLookupSwitchInsnSuper(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        this.methodVisitor.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public void visitMultiANewArrayInsnSuper(String desc, int dims) {
        this.methodVisitor.visitMultiANewArrayInsnSuper(desc, dims);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.methodVisitor.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public void visitTryCatchBlockSuper(Label start, Label end, Label handler, String type) {
        this.methodVisitor.visitTryCatchBlockSuper(start, end, handler, type);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        this.methodVisitor.visitAttribute(attr);
    }

    @Override
    public void visitAttributeSuper(Attribute attr) {
        this.methodVisitor.visitAttributeSuper(attr);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return this.methodVisitor.visitAnnotation(descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitAnnotationSuper(String descriptor, boolean visible) {
        return this.methodVisitor.visitAnnotationSuper(descriptor, visible);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T unwrap() {
        if (this.methodVisitor instanceof org.eclipse.persistence.asm.internal.platform.ow2.MethodVisitorImpl) {
            return (T)((org.eclipse.persistence.asm.internal.platform.ow2.MethodVisitorImpl)this.methodVisitor).getInternal(this.customMethodVisitor);
        } else {
            return null;
        }
    }

}
