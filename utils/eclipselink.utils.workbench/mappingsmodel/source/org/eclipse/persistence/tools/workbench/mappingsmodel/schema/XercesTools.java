/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


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
