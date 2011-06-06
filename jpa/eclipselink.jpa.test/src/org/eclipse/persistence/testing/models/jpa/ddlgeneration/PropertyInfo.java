/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/22/2010-2.0.1 Karen Moore 
 *       - 294361: incorrect generated table for element collection attribute overrides
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.math.BigDecimal;

import javax.persistence.Embeddable;

@Embeddable
public class PropertyInfo {
    public Integer parcelNumber;
    public Integer size;
    public BigDecimal tax;
}

