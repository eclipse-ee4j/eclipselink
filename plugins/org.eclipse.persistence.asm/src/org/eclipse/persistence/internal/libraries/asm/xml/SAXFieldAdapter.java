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
package org.eclipse.persistence.internal.libraries.asm.xml;

import org.eclipse.persistence.internal.libraries.asm.AnnotationVisitor;
import org.eclipse.persistence.internal.libraries.asm.FieldVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;
import org.eclipse.persistence.internal.libraries.asm.TypePath;
import org.xml.sax.Attributes;

/**
 * SAXFieldAdapter
 *
 * @deprecated This class is no longer maintained, will not support new Java features, and will
 *     eventually be deleted. Use the asm or asm.tree API instead.
 * @author Eugene Kuleshov
 */
@Deprecated
public final class SAXFieldAdapter extends FieldVisitor {

  SAXAdapter sa;

  public SAXFieldAdapter(final SAXAdapter sa, final Attributes att) {
    super(Opcodes.ASM6);
    this.sa = sa;
    sa.addStart("field", att);
  }

  @Override
  public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
    return new SAXAnnotationAdapter(sa, "annotation", visible ? 1 : -1, null, desc);
  }

  @Override
  public AnnotationVisitor visitTypeAnnotation(
      int typeRef, TypePath typePath, String desc, boolean visible) {
    return new SAXAnnotationAdapter(
        sa, "typeAnnotation", visible ? 1 : -1, null, desc, typeRef, typePath);
  }

  @Override
  public void visitEnd() {
    sa.addEnd("field");
  }
}
