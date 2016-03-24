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
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.unmarshaller;

import java.util.ArrayList;
import java.util.List;

/**
 * Array unmarshaller item implementation for small long.
 *
 * @author Roman Grigoriadi
 */
public class LongArrayItem extends AbstractArrayItem<long[]> {

    private final List<Long> items = new ArrayList<>();

    protected LongArrayItem(UnmarshallerItemBuilder builder) {
        super(builder);
    }

    @Override
    protected List<?> getItems() {
        return items;
    }

    @Override
    public long[] getInstance() {
        final int size = items.size();
        final long[] longArray = new long[size];
        for(int i=0; i<size; i++) {
            longArray[i] = items.get(i);
        }
        return longArray;
    }
}
