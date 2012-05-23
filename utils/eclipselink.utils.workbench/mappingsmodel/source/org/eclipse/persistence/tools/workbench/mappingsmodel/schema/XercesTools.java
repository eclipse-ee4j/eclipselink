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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.ArrayList;
import java.util.ListIterator;

import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;


final class XercesTools 
{
	static void schemaElement() {
	}
	
	/**
     * Get all the top level groups in the schema
     * @return an array of top level nodes
     */
	static void getSchemaNodeGroupSet() {
	}
	
	static boolean attributeNodeIsProhibited() {
		return false;
	}
	
	static String getElementNodeEquivClassRefNamespace() {
		return  "equivRefNamespace";
	}
	
	static ListIterator<XSObject> listIteratorFromXSObjectList(XSObjectList list) {
		ArrayList<XSObject> objects = new ArrayList<XSObject>(list.getLength());
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i) != null) {
				objects.add(list.item(i));
			}
		}
		return objects.listIterator();
	}

}
