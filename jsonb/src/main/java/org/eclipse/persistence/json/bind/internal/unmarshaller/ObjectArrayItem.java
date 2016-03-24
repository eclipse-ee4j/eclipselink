/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal.unmarshaller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Item for handling arrays of objects.
 *
 * @author Roman Grigoriadi
 */
public class ObjectArrayItem<T> extends AbstractArrayItem<T[]> {

    private final List<T> items = new ArrayList<>();

    private T[] arrayInstance;

    protected ObjectArrayItem(UnmarshallerItemBuilder builder) {
        super(builder);
    }

    @Override
    protected List<?> getItems() {
        return items;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] getInstance() {
        if (arrayInstance == null || arrayInstance.length != items.size()) {
            arrayInstance = (T[]) Array.newInstance(componentClass, items.size());
        }
        return items.toArray(arrayInstance);
    }
}
