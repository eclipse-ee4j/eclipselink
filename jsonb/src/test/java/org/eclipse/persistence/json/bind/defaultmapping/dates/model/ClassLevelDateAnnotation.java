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
import java.time.ZonedDateTime;
import java.util.Calendar;

/**
 * @author Roman Grigoriadi
 */
@JsonbDateFormat(value = "X z E MMMM dd-MM-yyyy HH:mm:ss", locale = "fr")
public class ClassLevelDateAnnotation extends ClassLevelDateAnnotationParent {

    public ZonedDateTime zonedDateTime;

    public Calendar calendar;

    @JsonbDateFormat(value = JsonbDateFormat.DEFAULT_FORMAT)
    public ZonedDateTime defaultZoned;

}
