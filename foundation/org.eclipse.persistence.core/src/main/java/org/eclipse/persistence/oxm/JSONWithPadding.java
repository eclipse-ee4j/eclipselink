/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - November 2012
package org.eclipse.persistence.oxm;

/**
 * <p>JSONWithPadding is used to hold an Object along with the corresponding callback name
 * to marshal.
 * <p>Sample Usage:
 * {@literal JSONWithPadding<Person>} jsonWithPaddingObject = new JSONWithPadding(person, "theCallBackName");
 * Marshal this jsonWithPaddingObject object would result in JSON like this:
 *     theCallBackName({"person":{"id":10,"name":"Bob"}});
 */
public class JSONWithPadding<T> {
    private T rootObject;
    private String callbackName;

    public static final String DEFAULT_CALLBACK_NAME = "callback";

    public JSONWithPadding(){
        this.callbackName = DEFAULT_CALLBACK_NAME;
    }

    public JSONWithPadding(T rootObject){
        this(rootObject, DEFAULT_CALLBACK_NAME);
    }

    public JSONWithPadding(T rootObject, String callbackName){
        this.rootObject = rootObject;
        setCallbackName(callbackName);
    }

    /**
     * The Object that will be marshalled
     * @return
     */
    public T getObject() {
        return rootObject;
    }

    /**
     * The Object to be marshalled
     * @param rootObject
     */
    public void setObject(T rootObject) {
        this.rootObject = rootObject;
    }

    /**
     * The callback name that should me marshalled with the object
     * @return
     */
    public String getCallbackName() {
        return callbackName;
    }

    /**
     * The callback name that should me marshalled with the object
     */
    public void setCallbackName(String callbackName) {
        if(callbackName == null){
            this.callbackName = DEFAULT_CALLBACK_NAME;
        }else{
            this.callbackName = callbackName;
        }
    }
}
