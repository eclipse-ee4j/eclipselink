/*
 * Copyright (c) 2016, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     12/05/2016-2.6 Jody Grassel
//       - 443546: Converter autoApply does not work for primitive types

package org.eclipse.persistence.jpa.converter.model.autoapply;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ConvertEntityI2S {
    @Id
    private long id;
    
    private int valueConvert;
    
    @Convert(disableConversion=true)
    private int valueNoConvert;
    
    public ConvertEntityI2S() {
        
    }
    
    public ConvertEntityI2S(long id) {
        this.id = id;
    }
    
    public ConvertEntityI2S(long id, int valueConvert, int valueNoConvert) {
        this.id = id;
        this.valueConvert = valueConvert;
        this.valueNoConvert = valueNoConvert;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getValueConvert() {
        return valueConvert;
    }

    public void setValueConvert(int valueConvert) {
        this.valueConvert = valueConvert;
    }

    public int getValueNoConvert() {
        return valueNoConvert;
    }

    public void setValueNoConvert(int valueNoConvert) {
        this.valueNoConvert = valueNoConvert;
    }

    @Override
    public String toString() {
        return "ConvertEntityI2S [id=" + id + ", valueConvert=" + valueConvert + ", valueNoConvert=" + valueNoConvert + "]";
    }
    
    
}
