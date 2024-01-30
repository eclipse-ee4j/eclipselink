/*
 * Copyright (c) 2010, 2024 Oracle and/or its affiliates. All rights reserved.
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
//  dclarke - initial sig compare utility (Bug 352151)
package eclipselink.utils.sigcompare;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.persistence.asm.ASMFactory;
import org.eclipse.persistence.asm.ClassReader;
import org.eclipse.persistence.asm.ClassVisitor;
import org.eclipse.persistence.asm.FieldVisitor;
import org.eclipse.persistence.asm.MethodVisitor;
import org.eclipse.persistence.asm.Opcodes;


public class SignatureImporter {

    public Map<String, ClassSignature> importSignatures(File jarFile) throws Exception {
        ZipFile zipFile = new ZipFile(jarFile);
        SignatureClassVisitor visitor = new SignatureClassVisitor();

        for (Enumeration<? extends ZipEntry> zipEnum = zipFile.entries(); zipEnum.hasMoreElements();) {
            ZipEntry entry = zipEnum.nextElement();

            if (entry.getName().endsWith(".class")) {
                InputStream in = zipFile.getInputStream(entry);
                ClassReader reader = ASMFactory.createClassReader(in);
                reader.accept(visitor, ClassReader.valueInt("SKIP_CODE") + ClassReader.valueInt("SKIP_DEBUG"));
                in.close();
            }
        }

        zipFile.close();

        for (ClassSignature classSig : visitor.classes.values()) {
            classSig.initialzeParent(visitor.classes);
        }

        return visitor.classes;
    }

    class SignatureClassVisitor extends ClassVisitor {

        protected Map<String, ClassSignature> classes = new HashMap<>();

        protected ClassSignature sig = null;

        SignatureClassVisitor() {
            super(Opcodes.valueInt("ASM5"));
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if ((access & Opcodes.valueInt("ACC_PUBLIC")) > 0) {
                this.sig = new ClassSignature(name, superName, interfaces);
            } // TODO: Handle inheritance
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if (this.sig != null && (access & Opcodes.valueInt("ACC_PUBLIC")) > 0) {
                this.sig.addField(name, desc);
            }
            return null;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (this.sig != null && (access & Opcodes.valueInt("ACC_PUBLIC")) > 0) {
                this.sig.addMethod(name, desc);
            }
            return null;
        }

        @Override
        public void visitEnd() {
            if (this.sig != null) {
                this.classes.put(this.sig.getName(), this.sig);
            }
            this.sig = null;
        }

    }
}
