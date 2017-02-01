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
package org.eclipse.persistence.internal.libraries.asm.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.libraries.asm.ClassVisitor;
import org.eclipse.persistence.internal.libraries.asm.ModuleVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;

/**
 * A node that represents a module declaration.
 * 
 * @author Remi Forax
 */
public class ModuleNode extends ModuleVisitor {
    /**
     * A list of modules can are required by the current module.
     * May be <tt>null</tt>.
     */
    public List<ModuleRequireNode> requires;
    
    /**
     * A list of packages that are exported by the current module.
     * May be <tt>null</tt>.
     */
    public List<ModuleExportNode> exports;
    
    /**
     * A list of classes in their internal forms that are used
     * as a service by the current module. May be <tt>null</tt>.
     */
    public List<String> uses;
   
    /**
     * A list of services along with their implementations provided
     * by the current module. May be <tt>null</tt>.
     */
    public List<ModuleProvideNode> provides;

    public ModuleNode() {
        super(Opcodes.ASM6);
    }
    
    public ModuleNode(final int api,
      List<ModuleRequireNode> requires,
      List<ModuleExportNode> exports,
      List<String> uses,
      List<ModuleProvideNode> provides) {
        super(Opcodes.ASM6);
        this.requires = requires;
        this.exports = exports;
        this.uses = uses;
        this.provides = provides;
        if (getClass() != ModuleNode.class) {
            throw new IllegalStateException();
        }
    }
    
    @Override
    public void visitRequire(String module, int access) {
        if (requires == null) {
            requires = new ArrayList<ModuleRequireNode>(5);
        }
        requires.add(new ModuleRequireNode(module, access));
    }
    
    @Override
    public void visitExport(String packaze, String... modules) {
        if (exports == null) {
            exports = new ArrayList<ModuleExportNode>(5);
        }
        List<String> moduleList = null;
        if (modules != null) {
            moduleList = new ArrayList<String>(modules.length);
            for(int i = 0; i < modules.length; i++) {
                moduleList.add(modules[i]);
            }
        }
        exports.add(new ModuleExportNode(packaze, moduleList));
    }
    
    @Override
    public void visitUse(String service) {
        if (uses == null) {
            uses = new ArrayList<String>(5);
        }
        uses.add(service);
    }
    
    @Override
    public void visitProvide(String service, String impl) {
        if (provides == null) {
            provides = new ArrayList<ModuleProvideNode>(5);
        }
        provides.add(new ModuleProvideNode(service, impl));
    }
    
    @Override
    public void visitEnd() {
    }
    
    public void accept(final ClassVisitor cv) {
        ModuleVisitor mv = cv.visitModule();
        if (mv == null) {
            return;
        }
        if (requires != null) {
            for(int i = 0; i < requires.size(); i++) {
                requires.get(i).accept(mv);
            }
        }
        if (exports != null) {
            for(int i = 0; i < exports.size(); i++) {
                exports.get(i).accept(mv);
            }
        }
        if (uses != null) {
            for(int i = 0; i < uses.size(); i++) {
                mv.visitUse(uses.get(i));
            }
        }
        if (provides != null) {
            for(int i = 0; i < provides.size(); i++) {
                provides.get(i).accept(mv);
            }
        }
    }
}
