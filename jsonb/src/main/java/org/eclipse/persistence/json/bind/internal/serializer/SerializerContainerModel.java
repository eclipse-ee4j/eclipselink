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

package org.eclipse.persistence.json.bind.internal.serializer;

import org.eclipse.persistence.json.bind.internal.unmarshaller.ContainerModel;
import org.eclipse.persistence.json.bind.model.Customization;
import org.eclipse.persistence.json.bind.model.SerializerBindingModel;

import java.lang.reflect.Type;

/**
 * Container model for serializers. Represents collections arrays and maps.
 *
 * @author Roman Grigoriadi
 */
public class SerializerContainerModel extends ContainerModel implements SerializerBindingModel {

    private final Context context;

    private final String writeName;

    public SerializerContainerModel(Type valueRuntimeType, Customization customization, Context context, String writeName) {
        super(valueRuntimeType, customization);
        this.context = context;
        this.writeName = writeName;
    }

    /**
     * Name of json key that will be written by marshaller.
     *
     * @return
     */
    @Override
    public String getJsonWriteName() {
        return writeName;
    }

    /**
     * Current context of json generator.
     *
     * @return context
     */
    @Override
    public Context getContext() {
        return context;
    }
}
