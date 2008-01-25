/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.classfile;

import java.io.IOException;

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * 
 */
public class RuntimeVisibleParameterAnnotationsAttribute extends Attribute {
	// TODO decrypt 'info'
	private byte[] info;

	public RuntimeVisibleParameterAnnotationsAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
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
