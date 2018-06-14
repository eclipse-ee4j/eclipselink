package org.eclipse.samples;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@RecursiveAnnotation
@RecursiveAnnotation2
public @interface RecursiveAnnotation {
}
