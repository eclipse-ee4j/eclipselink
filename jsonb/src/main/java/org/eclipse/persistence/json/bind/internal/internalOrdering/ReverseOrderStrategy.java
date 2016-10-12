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

import javax.json.bind.config.PropertyNamingStrategy;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Revers ordering strategy
 *
 * @author David Kral
 */
public class ReverseOrderStrategy extends PropOrderStrategy implements Comparator<PropertyModel> {

    @Override
    public List<PropertyModel> sortProperties(Collection<PropertyModel> properties) {
        return properties.stream().sorted(this).collect(toList());
    }

    @Override
    public int compare(PropertyModel object1, PropertyModel object2) {
        return object2.getWriteName().compareTo(object1.getWriteName());
    }
}
