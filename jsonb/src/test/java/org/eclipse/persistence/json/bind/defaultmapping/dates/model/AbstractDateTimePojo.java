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

package org.eclipse.persistence.json.bind.defaultmapping.dates.model;

import javax.json.bind.annotation.JsonbDateFormat;

/**
 * Common parent class for testing date objects.
 * Contains {@link JsonbDateFormat} DEFAULT_FORMAT and TIME_IN_MILLIS annotated date objects.
 * @author Roman Grigoriadi
 */
public class AbstractDateTimePojo<T> {

    public AbstractDateTimePojo() {
    }

    public AbstractDateTimePojo(T dateObj) {
        this.defaultFormatted = dateObj;
        this.millisFormatted = dateObj;
    }

    @JsonbDateFormat(JsonbDateFormat.DEFAULT_FORMAT)
    public T defaultFormatted;

    @JsonbDateFormat(JsonbDateFormat.TIME_IN_MILLIS)
    public T millisFormatted;

}
