/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;

public class MWTableGenerationPolicy extends MWModel {

	private volatile String defaultPrimaryKeyName;
		public final static String DEFAULT_PRIMARY_KEY_NAME_PROPERTY = "defaultPrimaryKeyName";
		
	private volatile String primaryKeySearchPattern;
		public final static String PRIMARY_KEY_SEARCH_PATTERN_PROPERTY = "primaryKeySearchPattern";
	

	// ********** static methods **********
	
	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWTableGenerationPolicy.class);

		descriptor.addDirectMapping("defaultPrimaryKeyName", "default-primary-key-name/text()");
		descriptor.addDirectMapping("primaryKeySearchPattern", "primary-key-search-pattern/text()");

		return descriptor;
	}

	/**
	 * default constructor - for TopLink use only
	 */
	private MWTableGenerationPolicy() {
		// for TopLink use only
		super();
	}
	
	public MWTableGenerationPolicy(MWRelationalProject project) {
		super(project);
		//set defaults:
		this.defaultPrimaryKeyName = "ID";
		this.primaryKeySearchPattern = "*ID";
	}

	public String getDefaultPrimaryKeyName() {
		return this.defaultPrimaryKeyName;
	}
	
	public void setDefaultPrimaryKeyName(String newDefaultPrimaryKeyName) {
		String oldDefaultPrimaryKeyName = this.defaultPrimaryKeyName;
		this.defaultPrimaryKeyName = newDefaultPrimaryKeyName;
		firePropertyChanged(DEFAULT_PRIMARY_KEY_NAME_PROPERTY, oldDefaultPrimaryKeyName, newDefaultPrimaryKeyName);
	}
	
	public String getPrimaryKeySearchPattern() {
		return this.primaryKeySearchPattern;
	}
	
	public void setPrimaryKeySearchPattern(String newPrimaryKeySearchPattern) {
		String oldPrimaryKeySearchPattern = this.primaryKeySearchPattern;
		this.primaryKeySearchPattern = newPrimaryKeySearchPattern;
		firePropertyChanged(PRIMARY_KEY_SEARCH_PATTERN_PROPERTY, oldPrimaryKeySearchPattern, newPrimaryKeySearchPattern);
	}

	//********** display methods ***********
	
	public void toString(StringBuffer sb) {
		sb.append("Default PK Name=");
		sb.append(this.getDefaultPrimaryKeyName());
		sb.append(" PK Search Pattern=");
		sb.append(this.getPrimaryKeySearchPattern());
	}
}
