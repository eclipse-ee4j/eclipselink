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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.persistence.asm.Attribute;
import org.eclipse.persistence.asm.ClassVisitor;
import org.eclipse.persistence.internal.libraries.asm.ClassReader;
import org.eclipse.persistence.internal.libraries.asm.EclipseLinkClassReader;

public class ClassReaderImpl extends org.eclipse.persistence.asm.ClassReader {

    private ClassReader elClassReader;

    public ClassReaderImpl(final InputStream inputStream) throws IOException {
        elClassReader = new ClassReader(inputStream);
    }

    public ClassReaderImpl(final InputStream inputStream, final boolean checkClassVersion)  throws IOException {
        if (checkClassVersion) {
            elClassReader = new ClassReader(inputStream);
        } else {
            //EclipseLinkClassReader
            elClassReader = new EclipseLinkClassReader(inputStream);
        }
    }

    public ClassReaderImpl(final byte[] classFileBuffer) throws IOException {
        elClassReader = new ClassReader(classFileBuffer);
    }

    public ClassReaderImpl(final byte[] classFileBuffer, final int classFileOffset, final int classFileLength) throws IOException {
        elClassReader = new ClassReader(classFileBuffer, classFileOffset, classFileLength);
    }

    public ClassReader getInternal() {
        return this.elClassReader;
    }

    @Override
    public void accept(ClassVisitor classVisitor, int parsingOptions) {
        elClassReader.accept(classVisitor.unwrap(), parsingOptions);
    }

    @Override
    public void accept(ClassVisitor classVisitor, Attribute[] attributePrototypes, int parsingOptions) {
        org.eclipse.persistence.internal.libraries.asm.Attribute[] attributePrototypesUnwrap = Arrays.stream(attributePrototypes).map(value -> value.unwrap()).toArray(size -> new org.eclipse.persistence.internal.libraries.asm.Attribute[size]);
        elClassReader.accept(classVisitor.unwrap(), attributePrototypesUnwrap, parsingOptions);
    }

    @Override
    public int getAccess() {
        return elClassReader.getAccess();
    }

    @Override
    public String getSuperName() {
        return elClassReader.getSuperName();
    }

    @Override
    public String[] getInterfaces() {
        return elClassReader.getInterfaces();
    }

    @Override
    public <T> T unwrap() {
        return (T)this.elClassReader;
    }
}
