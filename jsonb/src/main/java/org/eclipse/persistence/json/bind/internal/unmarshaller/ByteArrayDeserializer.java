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
 * Array unmarshaller item implementation for small int.
 *
 * @author Roman Grigoriadi
 */
public class ByteArrayDeserializer extends AbstractArrayDeserializer<byte[]> {

    private final List<Byte> items = new ArrayList<>();

    protected ByteArrayDeserializer(DeserializerBuilder builder) {
        super(builder);
    }

    @Override
    protected List<?> getItems() {
        return items;
    }

    @Override
    public byte[] getInstance() {
        final int size = items.size();
        final byte[] byteArray = new byte[size];
        for(int i=0; i<size; i++) {
            byteArray[i] = items.get(i);
        }
        return byteArray;
    }
}
