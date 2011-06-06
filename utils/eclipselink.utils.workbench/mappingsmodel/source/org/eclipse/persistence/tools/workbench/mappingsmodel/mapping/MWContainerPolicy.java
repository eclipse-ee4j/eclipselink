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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;

public interface MWContainerPolicy 
	extends MWNode
{
    DefaultingContainerClass getDefaultingContainerClass();
    
    MWClass defaultContainerClass();
    
	boolean usesSorting();
	void setUsesSorting(boolean sort);
		public static final String SORT_PROPERTY = "sort";

	MWClass getComparatorClass();
	void setComparatorClass(MWClass comparatorClass);
		public static final String COMPARATOR_CLASS_PROPERTY = "comparatorClass";
	
	void referenceDescriptorChanged(MWDescriptor newReferenceDescriptor);
    
    ContainerPolicy runtimeContainerPolicy();
        
    //This class is just used to "trick" TopLink into thinking we have an inheritance situation
    //It is a workaround for a Variable 1-1 mapping.
    public final class MWContainerPolicyRoot {       
        public static XMLDescriptor buildDescriptor() {
            XMLDescriptor descriptor = new XMLDescriptor();
            descriptor.setJavaClass(MWContainerPolicyRoot.class);
            
            InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
            ip.setClassIndicatorFieldName("@type");
            ip.addClassIndicator(MWCollectionContainerPolicy.class, "collection");
            ip.addClassIndicator(MWListContainerPolicy.class, "list");
            ip.addClassIndicator(MWMapContainerPolicy.class, "map");
            ip.addClassIndicator(MWSetContainerPolicy.class, "set");

            
            return descriptor;
        } 
    }
}
