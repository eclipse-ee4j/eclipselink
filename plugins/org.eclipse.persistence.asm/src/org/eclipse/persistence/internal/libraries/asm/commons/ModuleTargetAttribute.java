// ASM: a very small and fast Java bytecode manipulation framework
// Copyright (c) 2000-2011 INRIA, France Telecom
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. Neither the name of the copyright holders nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.

package org.eclipse.persistence.internal.libraries.asm.commons;

import org.eclipse.persistence.internal.libraries.asm.Attribute;
import org.eclipse.persistence.internal.libraries.asm.ByteVector;
import org.eclipse.persistence.internal.libraries.asm.ClassReader;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.Label;

/**
 * ModuleTarget attribute. This attribute is specific to the OpenJDK and may change in the future.
 *
 * @author Remi Forax
 */
public final class ModuleTargetAttribute extends Attribute {
  public String platform;

  /**
   * Constructs an attribute with a platform name.
   *
   * @param platform the platform name on which the module can run.
   */
  public ModuleTargetAttribute(final String platform) {
    super("ModuleTarget");
    this.platform = platform;
  }

  /**
   * Constructs an empty attribute that can be used as prototype to be passed as argument of the method
   * {@link ClassReader#accept(org.eclipse.persistence.internal.libraries.asm.ClassVisitor, Attribute[], int)}.
   */
  public ModuleTargetAttribute() {
    this(null);
  }

  @Override
  protected Attribute read(
      ClassReader cr, int off, int len, char[] buf, int codeOff, Label[] labels) {
    String platform = cr.readUTF8(off, buf);
    return new ModuleTargetAttribute(platform);
  }

  @Override
  protected ByteVector write(ClassWriter cw, byte[] code, int len, int maxStack, int maxLocals) {
    ByteVector v = new ByteVector();
    int index = (platform == null) ? 0 : cw.newUTF8(platform);
    v.putShort(index);
    return v;
  }
}
