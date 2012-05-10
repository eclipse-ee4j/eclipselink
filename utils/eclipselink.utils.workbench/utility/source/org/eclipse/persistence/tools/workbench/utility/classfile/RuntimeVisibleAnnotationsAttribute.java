/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.classfile;

import java.io.IOException;

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a runtime-visible annotations attribute:
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 num_annotations;
 *     {
 *         u2 type_index;
 *         u2 num_element_value_pairs;
 *         {
 *             u2 element_name_index;
 *             element_value {
 *                 u1 tag;
 *                 union
 *                 {
 *                     u2 const_value_index;
 *                     {
 *                         u2 type_name_index;
 *                         u2 const_name_index;
 *                     } enum_const_value;
 *                     u2 class_info_index;
 *                     annotation annotation_value;
 *                     {
 *                         u2 num_values;
 *                         element_value[num_values] values; // recurse?
 *                     } array_value;
 *                 } value;
 *             } value;
 *         }[num_element_value_pairs] element_value_pairs
 *     }[num_annotations] annotations;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class RuntimeVisibleAnnotationsAttribute extends Attribute {
	// TODO decrypt 'info'
	private byte[] info;

	public RuntimeVisibleAnnotationsAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
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
