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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.Collection;

import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;

public abstract class Content 
	extends AbstractSchemaModel
{
	// **************** Static methods ****************************************
	
	static Content reloadedContent(ExplicitComplexTypeDefinition complexType, 
								   Content oldContent,
								   XSComplexTypeDecl typeDecl) {
		Content newContent = oldContent;
		if (typeDecl.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_SIMPLE) {
			if (! (oldContent instanceof SimpleContent)) {
				newContent = new SimpleContent(complexType);
			}
		}
		else if (typeDecl.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_ELEMENT) {
			if (! (oldContent instanceof ComplexContent)) {
				newContent = new ComplexContent(complexType, false);
			}
		}	
		else if (typeDecl.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_MIXED) {
			if (! (oldContent instanceof ComplexContent)) {
				newContent = new ComplexContent(complexType, true);
			}
		}
		else {
			// complexTypeNode.getContent() == XSComplexTypeDefinition.CONTENTTYPE_EMPTY
			if (! (oldContent instanceof EmptyContent)) {
				newContent = new EmptyContent(complexType);
			}
		}
		
		newContent.reload(typeDecl);
		return newContent;
	}
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(Content.class);
		
		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(ComplexContent.class, "complex");
		ip.addClassIndicator(SimpleContent.class, "simple");
		ip.addClassIndicator(EmptyContent.class, "empty");
		
		return descriptor;
	}
	
	
	// **************** Constructors ******************************************
	
	/** Toplink use only */
	protected Content() {
		super();
	}
	
	Content(ExplicitComplexTypeDefinition parent) {
		super(parent);
	}
	
	
	// **************** Behavior **********************************************
	
	/** No-op */
	void addDirectlyOwnedComponentsTo(Collection directlyOwnedComponents) {}
	
	abstract boolean hasTextContent();
	
	abstract boolean containsWildcard();
	
	/** Default implementation */
	int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
		return 0;
	}
}
