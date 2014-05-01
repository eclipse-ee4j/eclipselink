/*******************************************************************************
 * Copyright (c) 2010-2014 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - initial sig compare utility (Bug 352151)
 ******************************************************************************/
package eclipselink.utils.sigcompare;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.persistence.internal.libraries.asm.ClassReader;
import org.eclipse.persistence.internal.libraries.asm.ClassVisitor;
import org.eclipse.persistence.internal.libraries.asm.FieldVisitor;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;

public class SignatureImporter {

    public Map<String, ClassSignature> importSignatures(File jarFile) throws Exception {
        ZipFile zipFile = new ZipFile(jarFile);
        SignatureClassVisitor visitor = new SignatureClassVisitor();

        for (Enumeration<? extends ZipEntry> zipEnum = zipFile.entries(); zipEnum.hasMoreElements();) {
            ZipEntry entry = zipEnum.nextElement();

            if (entry.getName().endsWith(".class")) {
                InputStream in = zipFile.getInputStream(entry);
                ClassReader reader = new ClassReader(in);
                reader.accept(visitor, ClassReader.SKIP_CODE + ClassReader.SKIP_DEBUG);
                in.close();
            }
        }

        for (ClassSignature classSig : visitor.classes.values()) {
            classSig.initialzeParent(visitor.classes);
        }

        return visitor.classes;
    }

    class SignatureClassVisitor extends ClassVisitor {

        protected Map<String, ClassSignature> classes = new HashMap<String, ClassSignature>();

        protected ClassSignature sig = null;

        SignatureClassVisitor() {
        	super(Opcodes.ASM5);
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if ((access & Opcodes.ACC_PUBLIC) > 0) {
                this.sig = new ClassSignature(name, superName, interfaces);
            } // TODO: Handle inheritance
        }

        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if (this.sig != null && (access & Opcodes.ACC_PUBLIC) > 0) {
                this.sig.addField(name, desc);
            }
            return null;
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (this.sig != null && (access & Opcodes.ACC_PUBLIC) > 0) {
                this.sig.addMethod(name, desc);
            }
            return null;
        }

        public void visitEnd() {
            if (this.sig != null) {
                this.classes.put(this.sig.getName(), this.sig);
            }
            this.sig = null;
        }

    }
}
