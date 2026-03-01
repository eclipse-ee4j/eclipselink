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
package org.eclipse.persistence.asm.internal.platform.eclipselink;

import org.eclipse.persistence.asm.AnnotationVisitor;
import org.eclipse.persistence.asm.Attribute;
import org.eclipse.persistence.asm.Label;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;

import java.util.Arrays;

public class MethodVisitorImpl extends org.eclipse.persistence.asm.MethodVisitor {

    private class ELMethodVisitor extends MethodVisitor {

        public ELMethodVisitor(final int api) {
            super(api);
        }

        public ELMethodVisitor(final int api, final MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public org.eclipse.persistence.internal.libraries.asm.AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
            if (customMethodVisitor == null) {
                return super.visitAnnotation(descriptor, visible);
            } else {
                AnnotationVisitor annotationVisitor = customMethodVisitor.visitAnnotation(descriptor, visible);
                //In case of lazy like org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass.m_isLazy == true annotationVisitor is null
                return (annotationVisitor != null) ? annotationVisitor.unwrap() : null;
            }
        }

        @Override
        public void visitInsn(final int opcode) {
            if (customMethodVisitor == null) {
                super.visitInsn(opcode);
            } else {
                customMethodVisitor.visitInsn(opcode);
            }
        }

        public void visitInsnSuper(final int opcode) {
            super.visitInsn(opcode);
        }

        @Override
        public void visitIntInsn(final int opcode, final int operand) {
            if (customMethodVisitor == null) {
                super.visitIntInsn(opcode, operand);
            } else {
                customMethodVisitor.visitIntInsn(opcode, operand);
            }
        }

        public void visitIntInsnSuper(final int opcode, final int operand) {
            super.visitIntInsn(opcode, operand);
        }

        @Override
        public void visitVarInsn(final int opcode, final int var) {
            if (customMethodVisitor == null) {
                super.visitVarInsn(opcode, var);
            } else {
                customMethodVisitor.visitVarInsn(opcode, var);
            }
        }

        public void visitVarInsnSuper(final int opcode, final int var) {
            super.visitVarInsn(opcode, var);
        }

        @Override
        public void visitTypeInsn(final int opcode, final String desc) {
            if (customMethodVisitor == null) {
                super.visitTypeInsn(opcode, desc);
            } else {
                customMethodVisitor.visitTypeInsn(opcode, desc);
            }
        }

        public void visitTypeInsnSuper(final int opcode, final String desc) {
            super.visitTypeInsn(opcode, desc);
        }

        @Override
        public void visitFieldInsn(final int opcode, final String owner, final String name, final String descriptor) {
            if (customMethodVisitor == null) {
                super.visitFieldInsn(opcode, owner, name, descriptor);
            } else {
                customMethodVisitor.visitFieldInsn(opcode, owner, name, descriptor);
            }
        }

        public void visitFieldInsnSuper(final int opcode, final String owner, final String name, final String descriptor) {
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }

        @Override
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, boolean intf) {
            if (customMethodVisitor == null) {
                super.visitMethodInsn(opcode, owner, name, desc, intf);
            } else {
                customMethodVisitor.visitMethodInsn(opcode, owner, name, desc, intf);
            }
        }

        public void visitMethodInsnSuper(final int opcode, final String owner, final String name, final String desc, boolean intf) {
            super.visitMethodInsn(opcode, owner, name, desc, intf);
        }

        @Override
        public void visitJumpInsn(final int opcode, final org.eclipse.persistence.internal.libraries.asm.Label label) {
            if (customMethodVisitor == null) {
                super.visitJumpInsn(opcode, label);
            } else {
                customMethodVisitor.visitJumpInsn(opcode, new LabelImpl(label));
            }
        }

        public void visitJumpInsnSuper(final int opcode, final org.eclipse.persistence.internal.libraries.asm.Label label) {
            super.visitJumpInsn(opcode, label);
        }

        @Override
        public void visitLabel(final org.eclipse.persistence.internal.libraries.asm.Label label) {
            if (customMethodVisitor == null) {
                super.visitLabel(label);
            } else {
                customMethodVisitor.visitLabel(new LabelImpl(label));
            }
        }

        public void visitLabelSuper(final org.eclipse.persistence.internal.libraries.asm.Label label) {
            super.visitLabel(label);
        }

        @Override
        public void visitLdcInsn(final Object cst) {
            if (customMethodVisitor == null) {
                super.visitLdcInsn(cst);
            } else {
                customMethodVisitor.visitLdcInsn(cst);
            }
        }

        public void visitLdcInsnSuper(final Object cst) {
            super.visitLdcInsn(cst);
        }

        @Override
        public void visitIincInsn(final int var, final int increment) {
            if (customMethodVisitor == null) {
                super.visitIincInsn(var, increment);
            } else {
                customMethodVisitor.visitIincInsn(var, increment);
            }
        }

        public void visitIincInsnSuper(final int var, final int increment) {
            super.visitIincInsn(var, increment);
        }

        @Override
        public void visitTableSwitchInsn(final int min, final int max, final org.eclipse.persistence.internal.libraries.asm.Label dflt, final org.eclipse.persistence.internal.libraries.asm.Label... labels) {
            if (customMethodVisitor == null) {
                super.visitTableSwitchInsn(min, max, dflt, labels);
            } else {
                LabelImpl[] labelsImpl = Arrays.stream(labels).map(value -> new LabelImpl(value)).toArray(size -> new LabelImpl[size]);
                customMethodVisitor.visitTableSwitchInsn(min, max, new LabelImpl(dflt), labelsImpl);
            }
        }

        public void visitTableSwitchInsnSuper(final int min, final int max, final org.eclipse.persistence.internal.libraries.asm.Label dflt, final org.eclipse.persistence.internal.libraries.asm.Label... labels) {
            super.visitTableSwitchInsn(min, max, dflt, labels);
        }

        @Override
        public void visitLookupSwitchInsn(final org.eclipse.persistence.internal.libraries.asm.Label dflt, final int[] keys, final org.eclipse.persistence.internal.libraries.asm.Label[] labels) {
            if (customMethodVisitor == null) {
                super.visitLookupSwitchInsn(dflt, keys, labels);
            } else {
                LabelImpl[] labelsImpl = Arrays.stream(labels).map(value -> new LabelImpl(value)).toArray(size -> new LabelImpl[size]);
                customMethodVisitor.visitLookupSwitchInsn (new LabelImpl(dflt), keys, labelsImpl);
            }
        }

        public void visitLookupSwitchInsnSuper(final org.eclipse.persistence.internal.libraries.asm.Label dflt, final int[] keys, final org.eclipse.persistence.internal.libraries.asm.Label[] labels) {
            super.visitLookupSwitchInsn(dflt, keys, labels);
        }

        @Override
        public void visitMultiANewArrayInsn(final String desc, final int dims) {
            if (customMethodVisitor == null) {
                super.visitMultiANewArrayInsn(desc, dims);
            } else {
                customMethodVisitor.visitMultiANewArrayInsn (desc, dims);
            }
        }

        public void visitMultiANewArrayInsnSuper(final String desc, final int dims) {
            super.visitMultiANewArrayInsn(desc, dims);
        }

        @Override
        public void visitTryCatchBlock(final org.eclipse.persistence.internal.libraries.asm.Label start, final org.eclipse.persistence.internal.libraries.asm.Label end,final org.eclipse.persistence.internal.libraries.asm.Label handler, final String type) {
            if (customMethodVisitor == null) {
                super.visitTryCatchBlock(start, end, handler, type);
            } else {
                customMethodVisitor.visitTryCatchBlock(new LabelImpl(start), new LabelImpl(end), new LabelImpl(handler), type);
            }
        }

        public void visitTryCatchBlockSuper(final org.eclipse.persistence.internal.libraries.asm.Label start, final org.eclipse.persistence.internal.libraries.asm.Label end,final org.eclipse.persistence.internal.libraries.asm.Label handler, final String type) {
                super.visitTryCatchBlock(start, end, handler, type);
        }

        @Override
        public void visitMaxs(final int maxStack, final int maxLocals) {
            if (customMethodVisitor == null) {
                super.visitMaxs(maxStack, maxLocals);
            } else {
                customMethodVisitor.visitMaxs(maxStack, maxLocals);
            }
        }

        public void visitMaxsSuper(final int maxStack, final int maxLocals) {
            super.visitMaxs(maxStack, maxLocals);
        }

        @Override
        public void visitLocalVariable(final String name, final String desc, String signature, final org.eclipse.persistence.internal.libraries.asm.Label start, final org.eclipse.persistence.internal.libraries.asm.Label end, final int index) {
            if (customMethodVisitor == null) {
                super.visitLocalVariable(name, desc, signature, start, end, index);
            } else {
                customMethodVisitor.visitLocalVariable(name, desc, signature, new LabelImpl(start), new LabelImpl(end), index);
            }
        }

        public void visitLocalVariableSuper(final String name, final String desc, String signature, final org.eclipse.persistence.internal.libraries.asm.Label start, final org.eclipse.persistence.internal.libraries.asm.Label end, final int index) {
            super.visitLocalVariable(name, desc, signature, start, end, index);
        }

        @Override
        public void visitLineNumber(final int line, final org.eclipse.persistence.internal.libraries.asm.Label start) {
            if (customMethodVisitor == null) {
                super.visitLineNumber(line, start);
            } else {
                customMethodVisitor.visitLineNumber(line, new LabelImpl(start));
            }
        }

        public void visitLineNumberSuper(final int line, final org.eclipse.persistence.internal.libraries.asm.Label start) {
            super.visitLineNumber(line, start);
        }

        @Override
        public void visitAttribute(final org.eclipse.persistence.internal.libraries.asm.Attribute attr) {
            if (customMethodVisitor == null) {
                super.visitAttribute(attr);
            } else {
                customMethodVisitor.visitAttribute(new AttributeImpl(attr));
            }
        }

        public void visitAttributeSuper(final org.eclipse.persistence.internal.libraries.asm.Attribute attr) {
            super.visitAttribute(attr);
        }

        public org.eclipse.persistence.internal.libraries.asm.AnnotationVisitor visitAnnotationSuper(final String descriptor, final boolean visible) {
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        public void visitEnd() {
            if (customMethodVisitor == null) {
                super.visitEnd();
            } else {
                //should lead into infinite loop if visitEnd() is not implemented in customClassVisitor
                customMethodVisitor.visitEnd();
            }
        }
    }

    MethodVisitor ow2MethodVisitor;

    public MethodVisitorImpl(MethodVisitor methodVisitor) {
        this.ow2MethodVisitor = methodVisitor;
    }

    public MethodVisitorImpl(final int api) {
        this.ow2MethodVisitor = new ELMethodVisitor(api);
    }

    public MethodVisitorImpl(final int api, final org.eclipse.persistence.asm.MethodVisitor methodVisitor) {
        this.ow2MethodVisitor = new ELMethodVisitor(api, methodVisitor.unwrap());
    }

    public MethodVisitor getInternal(final org.eclipse.persistence.asm.MethodVisitor methodVisitor) {
        this.customMethodVisitor = methodVisitor;
        return this.ow2MethodVisitor;
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        this.ow2MethodVisitor.visitVarInsn(opcode, var);
    }

    @Override
    public void visitVarInsnSuper(int opcode, int var) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitVarInsnSuper(opcode, var);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        this.ow2MethodVisitor.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitMethodInsnSuper(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitMethodInsnSuper(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInsn(int opcode) {
        this.ow2MethodVisitor.visitInsn(opcode);
    }

    @Override
    public void visitInsnSuper(int opcode) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitInsnSuper(opcode);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        this.ow2MethodVisitor.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitMaxsSuper(int maxStack, int maxLocals) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitMaxsSuper(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        this.ow2MethodVisitor.visitEnd();
    }

    @Override
    public void visitCode() {
        this.ow2MethodVisitor.visitCode();
    }

    @Override
    public void visitLdcInsn(final Object value) {
        this.ow2MethodVisitor.visitLdcInsn(value);
    }

    @Override
    public void visitLdcInsnSuper(final Object value) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitLdcInsnSuper(value);
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        this.ow2MethodVisitor.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitTypeInsnSuper(final int opcode, final String type) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitTypeInsnSuper(opcode, type);
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String descriptor) {
        this.ow2MethodVisitor.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitFieldInsnSuper(final int opcode, final String owner, final String name, final String descriptor) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitFieldInsnSuper(opcode, owner, name, descriptor);
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        this.ow2MethodVisitor.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitIntInsnSuper(final int opcode, final int operand) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitIntInsnSuper(opcode, operand);
    }

    @Override
    public void visitLabel(final Label label) {
        this.ow2MethodVisitor.visitLabel(label.unwrap());
    }

    @Override
    public void visitLabelSuper(final Label label) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitLabelSuper(label.unwrap());
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        this.ow2MethodVisitor.visitJumpInsn(opcode, label.unwrap());
    }

    @Override
    public void visitJumpInsnSuper(final int opcode, final Label label) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitJumpInsnSuper(opcode, label.unwrap());
    }

    @Override
    public void visitFrame(final int type, final int numLocal, final Object[] local, final int numStack, final Object[] stack){
        this.ow2MethodVisitor.visitFrame(type,numLocal, local, numStack, stack);
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
        this.ow2MethodVisitor.visitLineNumber(line, start.unwrap());
    }

    @Override
    public void visitLineNumberSuper(final int line, final Label start) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitLineNumberSuper(line, start.unwrap());
    }

    @Override
    public void visitLocalVariable(final String name, final String descriptor, final String signature, final Label start, final Label end, final int index) {
        this.ow2MethodVisitor.visitLocalVariable(name, descriptor, signature, start.unwrap(), end.unwrap(), index);
    }

    @Override
    public void visitLocalVariableSuper(final String name, final String descriptor, final String signature, final Label start, final Label end, final int index) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitLocalVariableSuper(name, descriptor, signature, start.unwrap(), end.unwrap(), index);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        this.ow2MethodVisitor.visitIincInsn(var, increment);
    }

    @Override
    public void visitIincInsnSuper(int var, int increment) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitIincInsnSuper(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        org.eclipse.persistence.internal.libraries.asm.Label[] labelsUnwrap = Arrays.stream(labels).map(value -> value.unwrap()).toArray(org.eclipse.persistence.internal.libraries.asm.Label[]::new);
        this.ow2MethodVisitor.visitTableSwitchInsn(min, max, dflt.unwrap(), labelsUnwrap);
    }

    @Override
    public void visitTableSwitchInsnSuper(int min, int max, Label dflt, Label... labels) {
        org.eclipse.persistence.internal.libraries.asm.Label[] labelsUnwrap = Arrays.stream(labels).map(value -> value.unwrap()).toArray(org.eclipse.persistence.internal.libraries.asm.Label[]::new);
        ((ELMethodVisitor)this.ow2MethodVisitor).visitTableSwitchInsnSuper(min, max, dflt.unwrap(), labelsUnwrap);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        org.eclipse.persistence.internal.libraries.asm.Label[] labelsUnwrap = Arrays.stream(labels).map(value -> value.unwrap()).toArray(org.eclipse.persistence.internal.libraries.asm.Label[]::new);
        this.ow2MethodVisitor.visitLookupSwitchInsn(dflt.unwrap() , keys, labelsUnwrap);
    }

    @Override
    public void visitLookupSwitchInsnSuper(Label dflt, int[] keys, Label[] labels) {
        org.eclipse.persistence.internal.libraries.asm.Label[] labelsUnwrap = Arrays.stream(labels).map(value -> value.unwrap()).toArray(org.eclipse.persistence.internal.libraries.asm.Label[]::new);
        ((ELMethodVisitor)this.ow2MethodVisitor).visitLookupSwitchInsnSuper(dflt.unwrap() , keys, labelsUnwrap);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        this.ow2MethodVisitor.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public void visitMultiANewArrayInsnSuper(String desc, int dims) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitMultiANewArrayInsnSuper(desc, dims);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.ow2MethodVisitor.visitTryCatchBlock(start.unwrap(), end.unwrap(), handler.unwrap(), type);
    }

    @Override
    public void visitTryCatchBlockSuper(Label start, Label end, Label handler, String type) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitTryCatchBlockSuper(start.unwrap(), end.unwrap(), handler.unwrap(), type);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        this.ow2MethodVisitor.visitAttribute(attr.unwrap());
    }

    @Override
    public void visitAttributeSuper(Attribute attr) {
        ((ELMethodVisitor)this.ow2MethodVisitor).visitAttributeSuper(attr.unwrap());
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        return new AnnotationVisitorImpl(ow2MethodVisitor.visitAnnotation(descriptor, visible));
    }

        @Override
    public AnnotationVisitor visitAnnotationSuper(final String descriptor, final boolean visible) {
        return new AnnotationVisitorImpl(((ELMethodVisitor)this.ow2MethodVisitor).visitAnnotationSuper(descriptor, visible));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T unwrap() {
        return (T)this.ow2MethodVisitor;
    }
}
