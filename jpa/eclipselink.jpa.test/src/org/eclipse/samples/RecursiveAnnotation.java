package org.eclipse.samples;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@RecursiveAnnotation2
@RecursiveAnnotation
public @interface RecursiveAnnotation {
}
