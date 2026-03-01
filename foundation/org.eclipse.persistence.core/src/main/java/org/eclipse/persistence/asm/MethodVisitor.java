/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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

public abstract class MethodVisitor {

    protected MethodVisitor customMethodVisitor;

    public void setCustomMethodVisitor(MethodVisitor methodVisitor) {
        this.customMethodVisitor = methodVisitor;
    }

    public abstract void visitVarInsn(final int opcode, final int var);

    public abstract void visitVarInsnSuper(final int opcode, final int var);

    public abstract void visitMethodInsn(final int opcode, final String owner, final String name, final String descriptor, final boolean isInterface);

    public abstract void visitMethodInsnSuper(final int opcode, final String owner, final String name, final String descriptor, final boolean isInterface);

    public abstract void visitInsn(final int opcode);

    public abstract void visitInsnSuper(final int opcode);

    public abstract void visitMaxs(final int maxStack, final int maxLocals);

    public abstract void visitMaxsSuper(final int maxStack, final int maxLocals);

    public abstract void visitEnd();

    public abstract void visitCode();

    public abstract void visitLdcInsn(final Object value);

    public abstract void visitLdcInsnSuper(final Object value);

    public abstract void visitTypeInsn(final int opcode, final String type);

    public abstract void visitTypeInsnSuper(final int opcode, final String type);

    public abstract void visitFieldInsn(final int opcode, final String owner, final String name, final String descriptor);

    public abstract void visitFieldInsnSuper(final int opcode, final String owner, final String name, final String descriptor);

    public abstract void visitIntInsn(final int opcode, final int operand);

    public abstract void visitIntInsnSuper(final int opcode, final int operand);

    public abstract void visitLabel(final Label label);

    public abstract void visitLabelSuper(final Label label);

    public abstract void visitJumpInsn(final int opcode, final Label label);

    public abstract void visitJumpInsnSuper(final int opcode, final Label label);

    public abstract void visitFrame(final int type, final int numLocal, final Object[] local, final int numStack, final Object[] stack);

    public abstract void visitLineNumber(final int line, final Label start);

    public abstract void visitLineNumberSuper(final int line, final Label start);

    public abstract void visitLocalVariable(final String name, final String descriptor, final String signature, final Label start, final Label end, final int index);

    public abstract void visitLocalVariableSuper(final String name, final String descriptor, final String signature, final Label start, final Label end, final int index);

    public abstract void visitIincInsn (final int var, final int increment);

    public abstract void visitIincInsnSuper(final int var, final int increment);

    public abstract void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label... labels);

    public abstract void visitTableSwitchInsnSuper(final int min, final int max, final Label dflt, final Label... labels);

    public abstract void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels);

    public abstract void visitLookupSwitchInsnSuper(final Label dflt, final int[] keys, final Label[] labels);

    public abstract void visitMultiANewArrayInsn(final String desc, final int dims);

    public abstract void visitMultiANewArrayInsnSuper(final String desc, final int dims);

    public abstract void visitTryCatchBlock(final Label start, final Label end,final Label handler, final String type);

    public abstract void visitTryCatchBlockSuper(final Label start, final Label end,final Label handler, final String type);

    public abstract void visitAttribute(final Attribute attr);

    public abstract void visitAttributeSuper(final Attribute attr);

    public abstract AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible);

    public abstract AnnotationVisitor visitAnnotationSuper(final String descriptor, final boolean visible);

    public abstract <T> T unwrap();
}
