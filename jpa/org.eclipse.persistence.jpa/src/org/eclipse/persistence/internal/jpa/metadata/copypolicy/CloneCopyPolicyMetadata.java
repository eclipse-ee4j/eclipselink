/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     tware - March 28/2008 - 1.0M7 - Initial implementation
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.internal.jpa.metadata.copypolicy;

import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Used to store information about CloneCopyPolicy as it is read from XML or
 * annotations.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @see org.eclipse.persistence.annotations.CloneCopyPolicy
 * @author tware
 */
public class CloneCopyPolicyMetadata extends CopyPolicyMetadata {
    private String methodName;
    private String workingCopyMethodName;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public CloneCopyPolicyMetadata() {
        super("<clone-copy-policy>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading
     */
    public CloneCopyPolicyMetadata(MetadataAnnotation copyPolicy, MetadataAccessor accessor) {
        super(copyPolicy, accessor);

        methodName = copyPolicy.getAttributeString("method");
        workingCopyMethodName = copyPolicy.getAttributeString("workingCopyMethod");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof CloneCopyPolicyMetadata) {
            CloneCopyPolicyMetadata cloneCopyPolicy = (CloneCopyPolicyMetadata) objectToCompare;

            if (! valuesMatch(methodName, cloneCopyPolicy.getMethodName())) {
                return false;
            }

            return valuesMatch(workingCopyMethodName, cloneCopyPolicy.getWorkingCopyMethodName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = methodName != null ? methodName.hashCode() : 0;
        result = 31 * result + (workingCopyMethodName != null ? workingCopyMethodName.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     */
    public CopyPolicy getCopyPolicy() {
        if (methodName == null && workingCopyMethodName == null){
            throw ValidationException.copyPolicyMustSpecifyEitherMethodOrWorkingCopyMethod(getLocation());
        }

        CloneCopyPolicy copyPolicy = new CloneCopyPolicy();
        copyPolicy.setMethodName(methodName);
        copyPolicy.setWorkingCopyMethodName(workingCopyMethodName);
        return copyPolicy;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getWorkingCopyMethodName() {
        return workingCopyMethodName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setWorkingCopyMethodName(String workingCopyMethodName) {
        this.workingCopyMethodName = workingCopyMethodName;
    }
}
