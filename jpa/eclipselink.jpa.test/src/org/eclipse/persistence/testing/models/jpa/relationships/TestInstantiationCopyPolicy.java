/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - April 1 2008 - 1.0M6 - initial implementation
 ******************************************************************************/  

package org.eclipse.persistence.testing.models.jpa.relationships;

import org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy;

/**
 * A copy policy designed to test copy policy annotations
 * By design, this is an empty subclass of InstantiationCopyPolicy since we will just
 * use this to test it is properly set.
 * @author tware
 *
 */
public class TestInstantiationCopyPolicy extends InstantiationCopyPolicy {
}
