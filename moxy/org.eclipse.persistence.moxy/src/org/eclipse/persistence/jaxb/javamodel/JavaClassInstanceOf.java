/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.jaxb.javamodel;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>To provide faster alternative to instanceof.
 *
 * @author Martin Vojtek (martin.vojtek@oracle.com)
 */
public enum JavaClassInstanceOf {
    JAVA_CLASS_IMPL, OXM_JAVA_CLASS_IMPL, OXM_JAXB_ELEMENT_IMPL, OXM_OBJECT_FACTORY_IMPL, XJC_JAVA_CLASS_IMPL
}
