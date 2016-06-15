/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David Kral - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal.internalOrdering;

import org.eclipse.persistence.json.bind.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Annotation ordering strategy
 *
 * @author David Kral
 */
public class AnnotationOrderStrategy implements PropOrderStrategy {

    private String[] orderedPropertyNames;

    public AnnotationOrderStrategy(String[] orderedPropertyNames) {
        this.orderedPropertyNames = orderedPropertyNames;
    }

    @Override
    public List<PropertyModel> sortProperties(List<PropertyModel> properties) {
        List<PropertyModel> filteredAndSorted = new ArrayList<>();
        for (String fieldName : orderedPropertyNames) {
            filteredAndSorted.addAll(properties.stream().filter(propertyModel -> propertyModel.getJsonWriteName().equals(fieldName)).collect(Collectors.toList()));
        }
        return filteredAndSorted;
    }

}
