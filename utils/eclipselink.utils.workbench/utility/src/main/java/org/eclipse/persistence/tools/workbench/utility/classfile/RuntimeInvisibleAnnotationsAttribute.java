/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.utility.classfile;

import java.io.IOException;

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 *
 */
public class RuntimeInvisibleAnnotationsAttribute extends Attribute {
    // TODO decrypt 'info'
    private byte[] info;

    public RuntimeInvisibleAnnotationsAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
        super(stream, nameIndex, pool);
    }

    void initializeInfo(ClassFileDataInputStream stream) throws IOException {
        int length = this.getLength();
        this.info = new byte[length];
        stream.read(this.info);
    }

    void displayInfoStringOn(IndentingPrintWriter writer) {
        this.writeHexStringOn(this.info, writer);
        writer.println();
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    void toString(StringBuffer sb) {
        this.appendHexStringTo(this.info, sb);
    }

}
