/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.eclipse.persistence.internal.libraries.asm.util;

import org.eclipse.persistence.internal.libraries.asm.ModuleVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;

/**
 * @author Remi Forax
 */
public final class CheckModuleAdapter extends ModuleVisitor {
    private boolean end;

    public CheckModuleAdapter(final ModuleVisitor mv) {
        super(Opcodes.ASM6, mv);
    }

    @Override
    public void visitRequire(String module, int access) {
        checkEnd();
        if (module == null) {
            throw new IllegalArgumentException("require cannot be null");
        }
        super.visitRequire(module, access);
    }
    
    @Override
    public void visitExport(String packaze, String... modules) {
        checkEnd();
        if (packaze == null) {
            throw new IllegalArgumentException("require cannot be null");
        }
        if (modules != null) {
            for(int i = 0; i < modules.length; i++) {
                if (modules[i] == null) {
                    throw new IllegalArgumentException("to at index " + i + " cannot be null");
                }
            }
        }
        super.visitExport(packaze, modules);
    }
    
    @Override
    public void visitUse(String service) {
        checkEnd();
        CheckMethodAdapter.checkInternalName(service, "service");
        super.visitUse(service);
    }
    
    @Override
    public void visitProvide(String service, String impl) {
        checkEnd();
        CheckMethodAdapter.checkInternalName(service, "service");
        CheckMethodAdapter.checkInternalName(impl, "impl");
        super.visitProvide(service, impl);
    }
    
    @Override
    public void visitEnd() {
        checkEnd();
        end = true;
        super.visitEnd();
    }

    private void checkEnd() {
        if (end) {
            throw new IllegalStateException(
                    "Cannot call a visit method after visitEnd has been called");
        }
    }
}
