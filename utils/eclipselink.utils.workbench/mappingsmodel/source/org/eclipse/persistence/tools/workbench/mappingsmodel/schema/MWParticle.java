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

import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;


public interface MWParticle 
	extends MWSchemaComponent 
{
	/** Return the minimum number of times the particle occurs in a document */
	int getMinOccurs();
	
	/** Return the maximum number of times the particle occurs in a document */
	int getMaxOccurs();
	
	/** Return whether this particle may serve as a descriptor's schema context */
	boolean isDescriptorContextComponent();
	
	void addDirectlyOwnedComponentsTo(Collection directlyOwnedComponents);
	
	/** 
	 * Return -1 if element1 comes before element2, +1 if it comes after,
	 * or 0 if there is no order dependence.
	 */
	int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2);
	
	/**
	 * Used during reloading, 
	 * return true if the MWParticle can be used for the XS particle
	 * or false if another MWParticle must be constructed.
	 */
	boolean isEquivalentTo(XSParticleDecl xsParticle);
	
	
	class ParticleFactory
	{
		/**
		 */
		static MWParticle newParticle(AbstractSchemaModel parent, XSParticleDecl xsParticle) {
			MWParticle newParticle = null; 

			if (xsParticle == null) {
				newParticle = new NullParticle(parent);
			}
			else if (xsParticle.getTerm() instanceof XSElementDeclaration) {
				XSElementDeclaration elementNode = (XSElementDeclaration) xsParticle.getTerm();
				
				if (elementNode.getScope() == XSElementDecl.SCOPE_GLOBAL) {
					newParticle = new ReferencedElementDeclaration(parent, elementNode.getName(), elementNode.getNamespace());
				}
				else {
					newParticle = new ExplicitElementDeclaration(parent, elementNode.getName());
				}
			}
			else if (xsParticle.getTerm() instanceof XSModelGroup) {
				XSModelGroup groupNode = (XSModelGroup) xsParticle.getTerm();
				
				if (groupNode.getName() != null) {
					// The group is a model group definition (global <group>) reference.
					newParticle = new ReferencedModelGroup(parent, groupNode.getName(), groupNode.getNamespace());
				}
				else {
					newParticle = new ExplicitModelGroup(parent);
				}
			}
			else  /** if (particleNode instanceof XSDAny) */ {
				newParticle = new Wildcard(parent);
			}
			
			newParticle.reload(xsParticle);
			return newParticle;
		}
	}
}
