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
import java.util.Date;

/**
 * @author Roman Grigoriadi
 */
public class DatePojo extends AbstractDateTimePojo<Date> {

    public DatePojo() {
    }

    public DatePojo(Date dateObj) {
        super(dateObj);
        this.customDate = dateObj;
    }

    @JsonbDateFormat("HH:mm:ss | dd-MM-yyyy")
    public Date customDate;
}
