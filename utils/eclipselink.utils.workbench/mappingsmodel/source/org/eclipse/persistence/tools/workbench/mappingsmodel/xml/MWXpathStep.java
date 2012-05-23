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
package org.eclipse.persistence.tools.workbench.mappingsmodel.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWAttributeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXpathableSchemaComponent;

final class MWXpathStep
	extends MWModel
{
	// **************** Variables *********************************************
	
	/** The entire string of the step (includes namespace prefix) */
	private transient String stepString;
	
	/** The resolved namespace used for prefixing this step  (may be null) */
	private transient MWNamespace namespace;
	
	/** The resolved component  (may be null) */
	private MWXpathableSchemaComponent xpathComponent;
	
	/** This step points to a text node */
	private boolean isText;
	
	/** This step points to an attribute node */
	private boolean isAttribute;
	
	/** The local name of the node this step points to (no prefix) */
	private String localName = "";
	
	/** The position of this step (if applicable) */
	private int position = ANY_POSITION;
	
	/** The default position */
	private static int ANY_POSITION = 0;
	
	/** Return true if this xpath step has been resolved to a valid schema location */
	private transient boolean resolved;
	
	/** Whether this xpath step is valid */
	private transient boolean valid;
	
	
	// **************** Constructors ******************************************
	
	private MWXpathStep(MWXmlField parent) {
		super(parent);
	}
	
	MWXpathStep(MWXmlField parent, String stepString) {
		this(parent);
		this.stepString = stepString;
	}
	
	
	// **************** "Exposed" *********************************************
	
	boolean isText() {
		return this.isText;
	}
	
	boolean isAttribute() {
		return this.xpathComponent instanceof MWAttributeDeclaration;
	}
	
	boolean isElement() {
		return this.xpathComponent instanceof MWElementDeclaration;
	}
	
	boolean isPositional() {
		return this.position != ANY_POSITION;
	}
	
	/** 
	 * Return true if my position is not ANY_POSITION 
	 * or if my component has a max occurs of 1 or less
	 */
	boolean isSingular() { 
		if (this.xpathComponent != null && this.xpathComponent.getMaxOccurs() == -1) {
			return false;
		} else if (this.isText
					|| this.position != ANY_POSITION
					|| this.xpathComponent.getMaxOccurs() <= 1) {
			return true;
		}
		return false;
	}
	
	MWXpathableSchemaComponent xpathComponent() {
		return this.xpathComponent;
	}
		
	
	// **************** Step string *******************************************
	
	String getStepString() {
		return this.stepString;
	}
	
	void updateStepString() {
		if (this.isText) {
			this.stepString = this.textStepString();
		}
		else {
			this.stepString = this.componentStepString();
		}
	}
	
	private String textStepString() {
		return MWXmlField.TEXT;
	}
	
	private String componentStepString() {
		String stepString = "";
		
		if (this.isAttribute) {
			stepString += "@";
		}
		
		stepString += this.namespacePrefix();
		stepString += this.localName;
		stepString += this.positionString();
		
		return stepString;
	}
	
	private String namespacePrefix() {
		if (this.namespace != null 
				&& ! "".equals(this.namespace.getNamespacePrefix()) 
				&& !this.namespace.getSchema().getDefaultNamespaceUrl().equals(this.namespace.getNamespaceUrl())) {
			return this.namespace.getNamespacePrefix() + ":";
		}
		else {
			return "";
		}
	}
	
	private String positionString() {
		if (this.position != ANY_POSITION) {
			return "[" + this.position + "]";
		}
		else {
			return "";
		}
	}
	
	
	// **************** Resolution/validation *********************************
	
	boolean isResolved() {
		return this.resolved;
	}
	
	boolean isValid() {
		return this.valid;
	}
	
	MWSchemaContextComponent resolveContext(MWSchemaContextComponent parentContext) {
		if (parentContext == null) {
			this.unresolve();
			return null;
		}
		else if (MWXmlField.TEXT.equals(this.stepString)) {
			return this.resolveTextContext(parentContext);
		}
		else if (this.stepString.startsWith("@")) {
			return this.resolveAttributeContext(parentContext);
		}
		else {
			return this.resolveElementContext(parentContext);
		}
	}
	
	private void unresolve() {
		this.namespace = null;
		this.xpathComponent = null;
		this.localName = "";
		this.isAttribute = false;
		this.isText = false;
		this.position = ANY_POSITION;
		this.resolved = false;
		this.valid = false;
	}
	
	private MWSchemaContextComponent resolveTextContext(MWSchemaContextComponent parentContext) {
		this.namespace = null;
		this.isText = true;
		this.isAttribute = false;
		this.localName = "";
		this.position = ANY_POSITION;
		this.resolved = true;
		this.valid = parentContext.containsText();
		
		return null;
	}
	
	private MWSchemaContextComponent resolveAttributeContext(MWSchemaContextComponent parentContext) {
		this.isAttribute = true;
		this.position = ANY_POSITION;
		String qName = this.stepString.substring(1);
		return this.resolveComponentContext(parentContext, qName);
	}
	
	private MWSchemaContextComponent resolveElementContext(MWSchemaContextComponent parentContext) {
		this.isAttribute = false;
		this.position = ANY_POSITION;  // to be possibly changed below
		
		int openBraceIndex = this.stepString.indexOf('[');
		int closeBraceIndex = this.stepString.indexOf(']');
		String qName = this.stepString;
		
		
		if (openBraceIndex != -1 && closeBraceIndex > (openBraceIndex + 1)) {
			try {
				this.position = Integer.valueOf(this.stepString.substring(openBraceIndex + 1, closeBraceIndex)).intValue();
				qName = this.stepString.substring(0, openBraceIndex);
			}
			catch (NumberFormatException nfe) {
				// do nothing - if exception is thrown, qname is not set above
			}
		}
		
		return this.resolveComponentContext(parentContext, qName);
	}
	
	private MWSchemaContextComponent resolveComponentContext(MWSchemaContextComponent parentContext, String qName) {
		String namespacePrefix = "";
		String namespaceUrl = "";
		String localName = qName;
		
		int colonIndex = qName.indexOf(':');
		
		if (colonIndex != -1) {
			namespacePrefix = qName.substring(0, colonIndex);
			namespaceUrl = parentContext.getSchema().namespaceUrlForPrefix(namespacePrefix);
			localName = localName.substring(colonIndex + 1);
		}
		
		this.localName = localName;
		this.namespace = parentContext.getSchema().namespaceForUrl(namespaceUrl);
		
		if (this.isAttribute) {
			this.xpathComponent = parentContext.nestedAttribute(namespaceUrl, localName);
		}
		else {
			this.xpathComponent = parentContext.nestedElement(namespaceUrl, localName);
		}
		
		this.resolved = (this.xpathComponent != null);
		this.valid = this.xpathComponent != null && this.position <= this.xpathComponent.getMaxOccurs();
		return this.xpathComponent;
	}
	
	
	// **************** Used for runtime conversion ***************************
	
	static int compareSchemaOrder(MWSchemaContextComponent contextComponent, MWXpathStep step1, MWXpathStep step2) {
		if (step1.isAttribute()) {
			// attributes come equal to other attributes ...
			if (step2.isAttribute()) {
				return 0;
			}
			// ... and before text and elements
			else {
				return  -1;
			}
		}
		else if (step1.isText()) {
			// text come after attributes ...
			if (step2.isAttribute()) {
				return +1;
			}
			// ... equal to other text ...
			else if (step2.isText()) {
				return 0;
			}
			// ... and before elements
			else {
				return -1;
			}
		}
		else {
			// elements come after attributes and text ...
			if (! step2.isElement()) {
				return +1;
			}
			else {
				MWElementDeclaration element1 = (MWElementDeclaration) step1.xpathComponent();
				MWElementDeclaration element2 = (MWElementDeclaration) step2.xpathComponent();
				int comparison = contextComponent.compareSchemaOrder(element1, element2);
				
				if (comparison == 0) {
					return new Integer(step1.position).compareTo(new Integer(step2.position));
				}
				else {
					return comparison;
				}
			}
		}
	}
}
