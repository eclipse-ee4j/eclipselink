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
package org.eclipse.persistence.asm.internal.platform.ow2;

import org.eclipse.persistence.asm.Attribute;
import org.eclipse.persistence.asm.ClassVisitor;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ClassReaderImpl extends org.eclipse.persistence.asm.ClassReader {

    private ClassReader ow2ClassReader;

    public ClassReaderImpl(final InputStream inputStream) throws IOException {
        ow2ClassReader = new ClassReader(inputStream);
    }

    public ClassReaderImpl(final byte[] classFileBuffer) throws IOException {
        ow2ClassReader = new ClassReader(classFileBuffer);
    }

    public ClassReaderImpl(final byte[] classFileBuffer, final int classFileOffset, final int classFileLength) throws IOException {
        ow2ClassReader = new ClassReader(classFileBuffer, classFileOffset, classFileLength);
    }

    public ClassReader getInternal() {
        return this.ow2ClassReader;
    }

    @Override
    public void accept(ClassVisitor classVisitor, int parsingOptions) {
        ow2ClassReader.accept(classVisitor.unwrap(), parsingOptions);
    }

    @Override
    public void accept(ClassVisitor classVisitor, Attribute[] attributePrototypes, int parsingOptions) {
        org.objectweb.asm.Attribute[] attributePrototypesUnwrap = Arrays.stream(attributePrototypes).map(value -> value.unwrap()).toArray(org.objectweb.asm.Attribute[]::new);
        ow2ClassReader.accept(classVisitor.unwrap(), attributePrototypesUnwrap, parsingOptions);
    }

    @Override
    public int getAccess() {
        return ow2ClassReader.getAccess();
    }

    @Override
    public String getSuperName() {
        return ow2ClassReader.getSuperName();
    }

    @Override
    public String[] getInterfaces() {
        return ow2ClassReader.getInterfaces();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T unwrap() {
        return (T)this.ow2ClassReader;
    }
}
