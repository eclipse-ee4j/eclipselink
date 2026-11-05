/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.asm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassVisitorInnerClassTest {
    @Test
    void deliversMemberMetadataAndExplicitSuperForwardsExactlyOnce() throws IOException {
        ClassWriter writer = ASMFactory.createClassWriter(0);
        List<String> seen = new ArrayList<>();
        new EclipseLinkClassReader(source()).accept(new PassThrough(writer) {
            @Override
            public void visitInnerClass(String name, String outer, String inner, int access) {
                seen.add(entry(name, outer, inner, access));
                visitInnerClassSuper(name, outer, inner, access);
            }
        }, 0);

        assertFalse(seen.isEmpty());
        assertEquals(seen, entries(writer.toByteArray()));
        assertTrue(seen.stream().anyMatch(value -> value.contains("Member$Literal")));
    }

    @Test
    void customVisitorsWithoutOverridePreserveEntriesAcrossChains() throws IOException {
        byte[] source = source();
        ClassWriter writer = ASMFactory.createClassWriter(0);
        new EclipseLinkClassReader(source).accept(new PassThrough(new PassThrough(writer)), 0);

        assertEquals(entries(source), entries(writer.toByteArray()));
    }

    @Test
    void customOverrideMayConsumeEntriesWithoutImplicitDuplicateForwarding() throws IOException {
        ClassWriter writer = ASMFactory.createClassWriter(0);
        List<String> seen = new ArrayList<>();
        new EclipseLinkClassReader(source()).accept(new PassThrough(writer) {
            @Override
            public void visitInnerClass(String name, String outer, String inner, int access) {
                seen.add(entry(name, outer, inner, access));
            }
        }, 0);

        assertEquals(entries(source()), seen);
        assertTrue(entries(writer.toByteArray()).isEmpty());
    }

    @Test
    void writerFacadeAndImplementationBothAcceptDirectEntries() throws IOException {
        ClassWriter facade = new ClassWriter(0) {
            @Override
            public String getCommonSuperClass(String first, String second) {
                return "java/lang/Object";
            }

            @Override
            public <T> T unwrap() {
                return getInternal().unwrap();
            }
        };
        for (ClassWriter writer : List.of(facade, ASMFactory.createClassWriter(0))) {
            writer.visit(Opcodes.JAVA_CLASS_VERSION, Opcodes.ACC_PUBLIC, "sample/Outer", null, "java/lang/Object", null);
            writer.visitInnerClass("sample/Outer$Inner", "sample/Outer", "Inner", Opcodes.ACC_PUBLIC);
            writer.visitInnerClassSuper("sample/Outer$Dollar$Name", "sample/Outer", "Dollar$Name", Opcodes.ACC_PUBLIC);
            writer.visitEnd();

            assertEquals(List.of(entry("sample/Outer$Inner", "sample/Outer", "Inner", Opcodes.ACC_PUBLIC),
                    entry("sample/Outer$Dollar$Name", "sample/Outer", "Dollar$Name", Opcodes.ACC_PUBLIC)),
                    entries(writer.toByteArray()));
        }
    }

    private static List<String> entries(byte[] bytes) throws IOException {
        List<String> entries = new ArrayList<>();
        new EclipseLinkClassReader(bytes).accept(new PassThrough(ASMFactory.createClassWriter(0)) {
            @Override
            public void visitInnerClass(String name, String outer, String inner, int access) {
                entries.add(entry(name, outer, inner, access));
            }
        }, 0);
        return entries;
    }

    private static String entry(String name, String outer, String inner, int access) {
        return name + ":" + outer + ":" + inner + ":" + access;
    }

    private static byte[] source() throws IOException {
        String resource = "/" + Member$Literal.class.getName().replace('.', '/') + ".class";
        try (InputStream stream = Member$Literal.class.getResourceAsStream(resource)) {
            return stream.readAllBytes();
        }
    }

    static class Member$Literal { }

    /** The existing custom-visitor contract, intentionally without an InnerClasses override. */
    private static class PassThrough extends EclipseLinkClassVisitor {
        PassThrough(ClassVisitor delegate) {
            super(delegate);
            setCustomClassVisitor(this);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String parent, String[] interfaces) {
            visitSuper(version, access, name, signature, parent, interfaces);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return visitAnnotationSuper(descriptor, visible);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            return visitFieldSuper(access, name, descriptor, signature, value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            return visitMethodSuper(access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            // The platform visitor already forwards visitEnd to the delegate.
        }
    }
}
