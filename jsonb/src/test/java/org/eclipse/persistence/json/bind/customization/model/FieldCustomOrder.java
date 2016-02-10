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
 * David Kral
 ******************************************************************************/

package org.eclipse.persistence.json.bind.customization.model;

import javax.json.bind.annotation.JsonbPropertyOrder;

/**
 * @author David Kral
 */
@JsonbPropertyOrder({"aField", "cField", "dField" ,"bField"})
public class FieldCustomOrder {

    public String aField = "aValue";
    public String dField = "dValue";
    public String bField = "bValue";
    public String cField = "cValue";

}
