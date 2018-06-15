/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;

public abstract class MWContainerAccessor extends MWModel {

    public final static String CONTAINER_ACCESSOR_PROPERTY = "containerAccessor";

    // **************** Constructors ******************************************

    /** Default constructor - for TopLink use only (sorta) */
    public MWContainerAccessor() {
        super();
    }

    public MWContainerAccessor(MWNode parent) {
        super(parent);
    }

    // **************** Containment *********************************************

    protected Parent getCompositeParent() {
        return (Parent) this.getParent();
    }

    private MWAbstractCompositeMapping compositeMapping() {
        return this.getCompositeParent().compositeMapping();
    }

    protected MWMappingDescriptor parentDescriptor() {
        return this.compositeMapping().getParentDescriptor();
    }

    // **************** UI support *********************************************

    public abstract String accessorDisplayString();

    // **************** Runtime conversion ************************************

    public abstract void adjustRuntimeMapping(AbstractCompositeCollectionMapping mapping);

    public abstract void adjustRuntimeMapping(AbstractCompositeObjectMapping mapping);

    // **************** TopLink Methods ***************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWContainerAccessor.class);

        InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
        ip.setClassIndicatorFieldName("@type");
        ip.addClassIndicator(MWAttributeContainerAccessor.class, "attribute-based");
        ip.addClassIndicator(MWMethodContainerAccessor.class, "method-based");

        return descriptor;
    }

    public MWContainerAccessor valueForTopLink() {
        return this;
    }

    public static MWContainerAccessor buildAccessorForTopLink(MWContainerAccessor accesssor) {
        return (accesssor == null) ? new MWNullContainerAccessor() : accesssor;
    }

    public abstract boolean isNull();

    public abstract boolean isMethods();

    public abstract boolean isAttribute();

    // **************** Member Interface ***************************************

    /**
     * the accessor's parent should be able to return the appropriate
     * composite mapping
     */
    public interface Parent extends MWNode {
        MWAbstractCompositeMapping compositeMapping();
    }


}
