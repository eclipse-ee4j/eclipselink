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

import java.util.List;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

public final class MWCompositeObjectMapping 
	extends MWAbstractCompositeMapping
{
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWCompositeObjectMapping() {
		super();
	}
	
	MWCompositeObjectMapping(MWXmlDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}
	
	
	// **************** MWXpathContext implementation *************************
	
	protected boolean mayUseCollectionData() {
		return false;
	}
	
	
	// **************** Morphing **********************************************
	
	public MWCompositeObjectMapping asCompositeObjectMapping() {
		return this;
	}
	
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWCompositeObjectMapping(this);
	}
	
	
	// **************** Problem handling **************************************
	
	protected void addXmlFieldProblemsTo(List newProblems) {
		super.addXmlFieldProblemsTo(newProblems);
		this.addXmlFieldNotSingularProblemTo(newProblems);
	}
	
	private void addXmlFieldNotSingularProblemTo(List newProblems) {
		if (this.getXmlField().isValid() && ! this.getXmlField().isSingular()) {
			newProblems.add(this.buildProblem(ProblemConstants.XPATH_NOT_SINGULAR, this.getXmlField().getXpath()));
		}
	}
	
	
	// **************** Runtime conversion ************************************
	
	protected DatabaseMapping buildRuntimeMapping() {
		return this.xmlDescriptor().buildDefaultRuntimeCompositeObjectMapping();
	}
	
	public DatabaseMapping runtimeMapping() {
		AbstractCompositeObjectMapping runtimeMapping = 
			(AbstractCompositeObjectMapping) super.runtimeMapping();
		ClassTools.invokeMethod(runtimeMapping, "setField", new Class[] {DatabaseField.class}, new Object[] {this.getXmlField().runtimeField()});
		
		if (runtimeMapping.isXMLMapping()) {
			this.getContainerAccessor().adjustRuntimeMapping(runtimeMapping);
		}
		
		return runtimeMapping;
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWCompositeObjectMapping.class);
		descriptor.descriptorIsAggregate();
		
		descriptor.getInheritancePolicy().setParentClass(MWAbstractCompositeMapping.class);
		
		return descriptor;
	}
}
