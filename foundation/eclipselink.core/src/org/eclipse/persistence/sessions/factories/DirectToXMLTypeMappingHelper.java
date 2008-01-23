/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.factories;

import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;
import org.eclipse.persistence.sessions.Project;

/**
 * Helper class to abstract the XML mapping for DirectToXMLType
 * 
 * @author djclarke
 * @since EclipseLink 0.1
 */
public class DirectToXMLTypeMappingHelper {
	
	private static DirectToXMLTypeMappingHelper singleton = null;
	

	public static DirectToXMLTypeMappingHelper getInstance() {
		
		if (singleton == null) {
			Class helperClass = null;
			
			try {
				helperClass = (Class) new PrivilegedClassForName(
						"org.eclipse.persistence.tools.workbench.OracleDirectToXMLTypeMappingHelper")
						.run();
			} catch (ClassNotFoundException cnfe) {
				helperClass = DirectToXMLTypeMappingHelper.class;
			}
			try {
				singleton = (DirectToXMLTypeMappingHelper) new PrivilegedNewInstanceFromClass(helperClass).run();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException("Helper create failed: " + helperClass);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException("Helper create failed: " + helperClass);
			}
		}
		return singleton;
	}

	
	/**
	 * 
	 */
	public void addClassIndicator(XMLDescriptor descriptor) {
	}
	
	/**
	 * 
	 */
	public void writeShouldreadWholeDocument(NonreflectiveMethodDefinition method, String mappingName, DatabaseMapping mapping) {
	}
	
	/**
	 * Invoked from a descriptor add the descriptor for DirectToXMLTypeMapping 
	 */
	public void addXDBDescriptors(String name, Project project) {
	}
}
