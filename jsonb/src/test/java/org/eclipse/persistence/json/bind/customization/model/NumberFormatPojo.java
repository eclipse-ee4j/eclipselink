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

package org.eclipse.persistence.json.bind.customization.model;

import javax.json.bind.annotation.JsonbNumberFormat;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Roman Grigoriadi
 */
@JsonbNumberFormat("0.0")
public class NumberFormatPojo {

    @JsonbNumberFormat("00000000.000000")
    public BigDecimal bigDecimal;

    @JsonbNumberFormat("00000000")
    public BigInteger bigInteger;

    @JsonbNumberFormat("000.00000000")
    public Double aDouble;

    @JsonbNumberFormat("000.00000000")
    public Float aFloat;

    @JsonbNumberFormat("00000000000")
    public Long aLong;

    public Integer integer;

    @JsonbNumberFormat("00000")
    public Short aShort;

    @JsonbNumberFormat("000")
    public Byte aByte;
}
