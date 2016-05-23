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

package org.eclipse.persistence.json.bind.serializers.model;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeSerializer;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Roman Grigoriadi
 */
public class Crate {

    @JsonbProperty("crate_str")
    public String crateStr;

    public CrateInner crateInner;

    public List<CrateInner> crateInnerList;

    public BigDecimal crateBigDec;

    @JsonbDateFormat("dd.MM.yyy ^ HH:mm:ss")
    public Date date;

    public AnnotatedWithSerializerType annotatedType;

    @JsonbTypeSerializer(AnnotatedWithSerializerTypeSerializerOverride.class)
    public AnnotatedWithSerializerType annotatedTypeOverridenOnProperty;
}
