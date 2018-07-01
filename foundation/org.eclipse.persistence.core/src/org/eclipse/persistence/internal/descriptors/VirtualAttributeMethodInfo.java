/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     tware - initial implementation for Extensibililty feature
//
package org.eclipse.persistence.internal.descriptors;

import java.io.Serializable;

/**
 * Data-holding object that holds information about object used by mappings with
 * a VIRTUAL access type.
 * This data is used by our weaver to determine which methods to weave
 * @author tware
 *
 */
public class VirtualAttributeMethodInfo implements Serializable{

    protected String getMethodName = null;
    protected String setMethodName = null;

    public VirtualAttributeMethodInfo(String getMethodName, String setMethodName){
        this.getMethodName = getMethodName;
        this.setMethodName = setMethodName;
    }

    public String getGetMethodName() {
        return getMethodName;
    }
    public void setGetMethodName(String getMethodName) {
        this.getMethodName = getMethodName;
    }
    public String getSetMethodName() {
        return setMethodName;
    }
    public void setSetMethodName(String setMethodName) {
        this.setMethodName = setMethodName;
    }

    @Override
    public boolean equals(Object object){
        if (object == null || !(object instanceof VirtualAttributeMethodInfo)){
            return false;
        }
        VirtualAttributeMethodInfo info = (VirtualAttributeMethodInfo)object;
        if (getMethodName == info.getGetMethodName() && setMethodName == info.getSetMethodName()){
            return true;
        }
        if ((getMethodName == null && info.getGetMethodName() != null) || (setMethodName == null && info.getSetMethodName() != null)){
            return false;
        }
        return (getMethodName.equals(info.getGetMethodName()) && setMethodName.equals(info.getSetMethodName()));
    }

    @Override
    public int hashCode(){
        if (setMethodName == null){
            if (getMethodName == null){
                return super.hashCode();
            } else {
                return getMethodName.hashCode();
            }
        } else {
            if (getMethodName == null){
                return setMethodName.hashCode();
            } else {
                return getMethodName.hashCode() + setMethodName.hashCode();
            }
        }
    }

}
