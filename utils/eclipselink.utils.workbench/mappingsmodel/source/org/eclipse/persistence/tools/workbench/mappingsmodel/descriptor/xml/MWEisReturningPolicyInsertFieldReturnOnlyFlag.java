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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWReturningPolicyInsertFieldReturnOnlyFlag;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class MWEisReturningPolicyInsertFieldReturnOnlyFlag
	extends MWReturningPolicyInsertFieldReturnOnlyFlag
	implements MWXpathContext
{
	private MWXmlField field;


	// ********** constructors/initialization **********

	/** Default constructor - for TopLink use only */
	private MWEisReturningPolicyInsertFieldReturnOnlyFlag() {
		super();
	}
	
	MWEisReturningPolicyInsertFieldReturnOnlyFlag(MWEisReturningPolicy parent) {
		super(parent);
	}

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.field = new MWXmlField(this);
	}

	protected void addChildrenTo(List list) {
		super.addChildrenTo(list);
		list.add(this.field);
	}


	// ********** MWReturningPolicyInsertFieldReturnOnlyFlag implementation **********

	public MWDataField getField() {
		return this.field;
	}


	// ********** XML field **********

	public MWXmlField getXmlField() {
		return this.field;
	}
	
	
	// ********** MWXpathContext implementation **********
	
	public MWSchemaContextComponent schemaContext(MWXmlField xmlField) {
		return this.eisDescriptor().getSchemaContext();
	}
	
	public MWXpathSpec xpathSpec(MWXmlField xmlField) {
		return this.buildXpathSpec();
	}
	
	private MWXpathSpec buildXpathSpec() {
		return new MWXpathSpec() {
			public boolean mayUseCollectionData() {
				return false;
			}
			
			public boolean mayUseComplexData() {
				return false;
			}
			
			public boolean mayUseSimpleData() {
				return true;
			}
		};
	}
	
	
	// ********** problems **********

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		// TODO ????
	}
	
	
	// ********** Convenience **********
	
	public MWEisReturningPolicy eisReturningPolicy() {
		return (MWEisReturningPolicy) this.getParent();
	}
	
	private MWEisDescriptor eisDescriptor() {
		return this.eisReturningPolicy().eisDescriptor();
	}
	
	
	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWEisReturningPolicyInsertFieldReturnOnlyFlag.class);

		XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
		fieldMapping.setReferenceClass(MWXmlField.class);
		fieldMapping.setAttributeName("field");
		fieldMapping.setGetMethodName("getFieldForTopLink");
		fieldMapping.setSetMethodName("setFieldForTopLink");
		fieldMapping.setXPath("field");
		descriptor.addMapping(fieldMapping);

		((XMLDirectMapping) descriptor.addDirectMapping("returnOnly", "return-only/text()")).setNullValue(Boolean.FALSE);
		
		return descriptor;
	}
	
	private MWXmlField getFieldForTopLink() {
		return (this.field.isSpecified()) ? this.field : null;
	}	
	
	private void setFieldForTopLink(MWXmlField field) {
		this.field = ((field == null) ? new MWXmlField(this) : field);
	}

}
