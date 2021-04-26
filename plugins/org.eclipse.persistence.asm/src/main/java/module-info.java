/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
//      Oracle - initial API and implementation
module org.eclipse.persistence.asm {

    requires java.logging;

    exports org.eclipse.persistence.internal.libraries.asm;
    exports org.eclipse.persistence.internal.libraries.asm.commons;
    exports org.eclipse.persistence.internal.libraries.asm.signature;
    exports org.eclipse.persistence.internal.libraries.asm.tree;
    exports org.eclipse.persistence.internal.libraries.asm.tree.analysis;
}
