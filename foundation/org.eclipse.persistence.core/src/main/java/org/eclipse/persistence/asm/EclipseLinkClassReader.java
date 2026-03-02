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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EclipseLinkClassReader extends ClassReader {

    private ClassReader classReader;

    public EclipseLinkClassReader(InputStream stream) throws IOException {
        this(getStream(stream));
    }

    public EclipseLinkClassReader(final byte[] classFile) throws IOException {
        this(classFile, 0, classFile.length);
    }

    public EclipseLinkClassReader(final byte[] classFileBuffer, final int classFileOffset, final int classFileLength) throws IOException {
        classReader = ASMFactory.createClassReader(classFileBuffer, classFileOffset, classFileLength);
    }

    // not closing stream is intentional here
    private static byte[] getStream(final InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            outputStream.write(data, 0, bytesRead);
        }
        outputStream.flush();
        return outputStream.toByteArray();
    }

    @Override
    public void accept(ClassVisitor classVisitor, int parsingOptions) {
        classReader.accept(classVisitor, parsingOptions);
    }

    @Override
    public void accept(ClassVisitor classVisitor, Attribute[] attributePrototypes, int parsingOptions) {
        classReader.accept(classVisitor, attributePrototypes, parsingOptions);
    }

    @Override
    public int getAccess() {
        return classReader.getAccess();
    }

    @Override
    public String getSuperName() {
        return classReader.getSuperName();
    }

    @Override
    public String[] getInterfaces() {
        return classReader.getInterfaces();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T unwrap() {
        if (this.classReader instanceof org.eclipse.persistence.asm.internal.platform.ow2.ClassReaderImpl) {
            return (T)((org.eclipse.persistence.asm.internal.platform.ow2.ClassReaderImpl)this.classReader).getInternal();
        } else {
            return null;
        }
    }

}
