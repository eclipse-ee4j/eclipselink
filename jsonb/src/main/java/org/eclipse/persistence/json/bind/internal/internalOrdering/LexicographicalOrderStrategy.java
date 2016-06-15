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

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Lexicographical ordering strategy
 *
 * @author David Kral
 */
public class LexicographicalOrderStrategy implements PropOrderStrategy {

    @Override
    public List<PropertyModel> sortProperties(List<PropertyModel> properties) {
        return properties.stream().sorted((object1, object2) -> object1.getJsonWriteName().compareTo(object2.getJsonWriteName())).collect(toList());
    }

}
