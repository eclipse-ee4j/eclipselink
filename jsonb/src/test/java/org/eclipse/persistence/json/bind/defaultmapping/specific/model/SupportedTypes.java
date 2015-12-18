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

package org.eclipse.persistence.json.bind.defaultmapping.specific.model;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.OptionalLong;

/**
 * @author Roman Grigoriadi
 */
public class SupportedTypes {

    public static class NestedPojo {

        private Integer integerValue;

        public Integer getIntegerValue() {
            return integerValue;
        }

        public void setIntegerValue(Integer integerValue) {
            this.integerValue = integerValue;
        }

    }

    private Instant instant;

    private ZonedDateTime zonedDateTime;

    private OptionalLong optionalLong;

    private NestedPojo nestedPojo;

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public OptionalLong getOptionalLong() {
        return optionalLong;
    }

    public void setOptionalLong(OptionalLong optionalLong) {
        this.optionalLong = optionalLong;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    public NestedPojo getNestedPojo() {
        return nestedPojo;
    }

    public void setNestedPojo(NestedPojo nestedPojo) {
        this.nestedPojo = nestedPojo;
    }
}
