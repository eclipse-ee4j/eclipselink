/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.utility.node.NodeModel;

import org.eclipse.persistence.descriptors.InheritancePolicy;

public interface MWClassIndicatorPolicy
    extends NodeModel, MWXmlNode
{
    /** subclass type, only used in the UI */
    //TODO this should be removed
    String getType();
        String CLASS_INDICATOR_FIELD_TYPE = "fieldClassIndicatorPolicy";
        String CLASS_EXTRACTION_METHOD_TYPE = "classExtractionMethodClassIndicatorPolicy";
        String NULL_TYPE = "nullPolicy";


    void setDescriptorsAvailableForIndicatorDictionary(Iterator descriptors);
    void setDescriptorsAvailableForIndicatorDictionaryForTopLink(Iterator descriptors);
    void rebuildClassIndicatorValues(Collection descriptors);

    void automap();

    /**
     * aggregate support - if appropriate the ClassIndicatorPolicy will be added
     * to the collection of aggregateFieldNameGenerators
     */
    void addToAggregateFieldNameGenerators(Collection generators);


    /** model synchronization */
    void parentDescriptorMorphedToAggregate();

    /** runtime conversion */
    void adjustRuntimeInheritancePolicy(InheritancePolicy runtimeInheritancePolicy);

    /** persistence */
    MWAbstractClassIndicatorPolicy getValueForTopLink();


    public interface Parent
        extends MWNode
    {
        MWMappingDescriptor getContainingDescriptor();
    }
}
