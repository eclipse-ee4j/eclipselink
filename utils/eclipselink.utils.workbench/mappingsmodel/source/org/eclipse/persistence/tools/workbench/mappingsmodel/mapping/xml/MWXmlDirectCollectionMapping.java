/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class MWXmlDirectCollectionMapping
	extends MWAbstractXmlDirectCollectionMapping
	implements MWXmlMapping, MWXpathedMapping, MWXpathContext, MWDirectCollectionMapping, MWContainerMapping
{
	private boolean isCdata;
		public final static String IS_CDATA_PROPERTY= "isCdata";

	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWXmlDirectCollectionMapping() {
		super();
	}
	
	MWXmlDirectCollectionMapping(MWXmlDescriptor descriptor, MWClassAttribute attribute, String name) {
		super(descriptor, attribute, name);
	}
	
	// **************** Morphing **********************************************
	
	public MWXmlDirectCollectionMapping asMWXmlDirectCollectionMapping() {
		return this;
	}
	
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWXmlDirectCollectionMapping(this);
	}
	
	protected void initializeFromMWXpathedMapping(MWXpathedMapping oldMapping) {
		super.initializeFromMWXpathedMapping(oldMapping);
		this.getXmlField().setXpath(oldMapping.getXmlField().getXpath());
		this.getXmlField().setTyped(oldMapping.getXmlField().isTyped());
	}
	
	@Override
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.isCdata = false;
	}
	
	// **************** Runtime conversion ************************************
	
	protected DatabaseMapping buildRuntimeMapping() {
		return this.xmlDescriptor().buildDefaultRuntimeDirectCollectionMapping();
	}	
	
	public DatabaseMapping runtimeMapping() {
		AbstractCompositeDirectCollectionMapping runtimeMapping = (AbstractCompositeDirectCollectionMapping) super.runtimeMapping();
		if (!xmlDescriptor().isEisDescriptor()) {
			((XMLCompositeDirectCollectionMapping)runtimeMapping).setIsCDATA(this.isCdata());
		}
		return runtimeMapping;
	}

	// **************** TopLink Methods *****************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWXmlDirectCollectionMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractXmlDirectCollectionMapping.class);
	
		((XMLDirectMapping) descriptor.addDirectMapping("isCdata", "is-cdata/text()")).setNullValue(Boolean.FALSE);

        return descriptor;	
	}

	public static XMLDescriptor legacy60BuildDescriptor() {	
		XMLDescriptor descriptor = MWModel.legacy60BuildStandardDescriptor();
		descriptor.setJavaClass(MWXmlDirectCollectionMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractXmlDirectCollectionMapping.class);
		
        return descriptor;	
	}

	public boolean isCdata() {
		return isCdata;
	}

	public void setCdata(boolean isCdata) {
		boolean oldvalue = this.isCdata;
		this.isCdata = isCdata;
		firePropertyChanged(IS_CDATA_PROPERTY, oldvalue, this.isCdata);
	}
	
}
