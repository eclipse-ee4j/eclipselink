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

import org.eclipse.persistence.internal.libraries.asm.AnnotationVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;
import org.eclipse.persistence.internal.libraries.asm.RecordComponentVisitor;
import org.eclipse.persistence.internal.libraries.asm.TypePath;

/**
 * A {@link RecordComponentVisitor} that remaps types with a {@link Remapper}.
 *
 * @author Remi Forax
 */
public class RecordComponentRemapper extends RecordComponentVisitor {

  /** The remapper used to remap the types in the visited field. */
  protected final Remapper remapper;

  /**
   * Constructs a new {@link RecordComponentRemapper}. <i>Subclasses must not use this
   * constructor</i>. Instead, they must use the {@link
   * #RecordComponentRemapper(int,RecordComponentVisitor,Remapper)} version.
   *
   * @param recordComponentVisitor the record component visitor this remapper must delegate to.
   * @param remapper the remapper to use to remap the types in the visited record component.
   */
  public RecordComponentRemapper(
      final RecordComponentVisitor recordComponentVisitor, final Remapper remapper) {
    this(/* latest api = */ Opcodes.ASM7, recordComponentVisitor, remapper);
  }

  /**
   * Constructs a new {@link RecordComponentRemapper}.
   *
   * @param api the ASM API version supported by this remapper. Must be {@link
   *     org.eclipse.persistence.internal.libraries.asm.Opcodes#ASM8_EXPERIMENTAL}.
   * @param recordComponentVisitor the record component visitor this remapper must delegate to.
   * @param remapper the remapper to use to remap the types in the visited record component.
   */
  protected RecordComponentRemapper(
      final int api, final RecordComponentVisitor recordComponentVisitor, final Remapper remapper) {
    super(api, recordComponentVisitor);
    this.remapper = remapper;
  }

  @Override
  public AnnotationVisitor visitAnnotationExperimental(
      final String descriptor, final boolean visible) {
    AnnotationVisitor annotationVisitor =
        super.visitAnnotationExperimental(remapper.mapDesc(descriptor), visible);
    return annotationVisitor == null ? null : createAnnotationRemapper(annotationVisitor);
  }

  @Override
  public AnnotationVisitor visitTypeAnnotationExperimental(
      final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
    AnnotationVisitor annotationVisitor =
        super.visitTypeAnnotationExperimental(
            typeRef, typePath, remapper.mapDesc(descriptor), visible);
    return annotationVisitor == null ? null : createAnnotationRemapper(annotationVisitor);
  }

  /**
   * Constructs a new remapper for annotations. The default implementation of this method returns a
   * new {@link AnnotationRemapper}.
   *
   * @param annotationVisitor the AnnotationVisitor the remapper must delegate to.
   * @return the newly created remapper.
   */
  protected AnnotationVisitor createAnnotationRemapper(final AnnotationVisitor annotationVisitor) {
    return new AnnotationRemapper(api, annotationVisitor, remapper);
  }
}
