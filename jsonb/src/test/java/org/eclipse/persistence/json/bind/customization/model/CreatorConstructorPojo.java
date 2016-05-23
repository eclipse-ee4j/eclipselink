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

import javax.json.bind.annotation.JsonbCreator;
import java.math.BigDecimal;

/**
 * @author Roman Grigoriadi
 */
public class CreatorConstructorPojo {

    public String str1;

    public String str2;

    public BigDecimal bigDec;

    public CreatorFactoryMethodPojo innerFactoryCreator;

    @JsonbCreator
    public CreatorConstructorPojo(String str1, String str2) {
        this.str1 = str1;
        this.str2 = str2;
    }

}
