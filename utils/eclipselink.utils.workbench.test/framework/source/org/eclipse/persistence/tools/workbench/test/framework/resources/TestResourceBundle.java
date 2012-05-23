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
package org.eclipse.persistence.tools.workbench.test.framework.resources;

public final class TestResourceBundle extends java.util.ListResourceBundle {
	
	private static final Object[][] contents = {
		{"copy",							"&Copy"},
		{"copy.toolTip",				"Copy"},
		{"OK",							"O&K"},
		{"CANCEL",						"&Cancel & Die"},
		{"FORMATTED_MSG_1",	"Single-argument message: {0, number}."},
		{"FORMATTED_MSG_2",	"Two-argument message: {0, number} + {1, number}."},
		{"FORMATTED_MSG_3",	"Three-argument message: {0, number} + {1, number} = {2, number}."},
		{"FORMATTED_MSG_4",	"{3}-argument message: {0, number} + {1, number} = {2, number}."},
	};

	public Object[][] getContents() {
		return contents;
	}
}
