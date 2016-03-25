/*
 * Copyright (C) 2016 by Sebastian Hasait (sebastian at hasait dot de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hasait.genesis.base;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import de.hasait.genesis.base.freemarker.FreemarkerModelWriter;
import de.hasait.genesis.base.util.GenesisUtils;

/**
 *
 */
public class AbstractGenesisProcessor extends AbstractProcessor {

	private final Map<String, Generator<?>> _generators = new HashMap<String, Generator<?>>();

	private ModelWriter _modelWriter;

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return _generators.keySet();
	}

	@Override
	public synchronized void init(final ProcessingEnvironment pProcessingEnv) {
		super.init(pProcessingEnv);

		setModelWriter(new FreemarkerModelWriter());
	}

	@Override
	public final boolean process(final Set<? extends TypeElement> pAnnotationTypeElements, final RoundEnvironment pRoundEnv) {
		for (final TypeElement annotationTypeElement : pAnnotationTypeElements) {
			process(annotationTypeElement, pRoundEnv);
		}

		return true;
	}

	protected final Generator<?> getGenerator(final String pQualifiedAnnotationName) {
		return _generators.get(pQualifiedAnnotationName);
	}

	protected final ModelWriter getModelWriter() {
		return _modelWriter;
	}

	protected final void printError(final Element pElement, final String pFormat, final Object... pArgs) {
		GenesisUtils.printError(processingEnv.getMessager(), pElement, pFormat, pArgs);
	}

	protected final void printNote(final Element pElement, final String pFormat, final Object... pArgs) {
		GenesisUtils.printNote(processingEnv.getMessager(), pElement, pFormat, pArgs);
	}

	protected final void printStackTrace(final Element pElement, final Throwable pThrowable, final String pFormat, final Object... pArgs) {
		GenesisUtils.printStackTrace(processingEnv.getMessager(), pElement, pThrowable, pFormat, pArgs);
	}

	protected final <A extends Annotation> void registerGenerator(final Class<A> pAnnotationType, final Generator<A> pGenerator) {
		_generators.put(pAnnotationType.getName(), pGenerator);
	}

	protected final void setModelWriter(final ModelWriter pModelWriter) {
		_modelWriter = pModelWriter;
	}

	@SuppressWarnings("unchecked")
	private <A extends Annotation> void process(final TypeElement pAnnotationTypeElement, final RoundEnvironment pRoundEnv) {
		final String qualifiedAnnotationName = pAnnotationTypeElement.getQualifiedName().toString();
		final Generator<A> generator = (Generator<A>) getGenerator(qualifiedAnnotationName);

		if (generator != null) {
			final Class<A> annotationType;
			try {
				annotationType = (Class<A>) Class.forName(qualifiedAnnotationName);
			} catch (final Throwable t) {
				printStackTrace(pAnnotationTypeElement, t, "Unexpected");
				return;
			}

			final Set<? extends Element> annotatedElements;
			try {
				annotatedElements = pRoundEnv.getElementsAnnotatedWith(pAnnotationTypeElement);
			} catch (final Throwable t) {
				printStackTrace(pAnnotationTypeElement, t, "Unexpected");
				return;
			}

			for (final Element annotatedElement : annotatedElements) {
				printNote(annotatedElement, "Processing: %s, %s", annotatedElement, pAnnotationTypeElement);

				try {
					final GeneratorEnv generatorEnv = new GeneratorEnv(processingEnv, annotatedElement);
					final A annotation = annotatedElement.getAnnotation(annotationType);
					generator.generate(annotation, generatorEnv);
					getModelWriter().write(generatorEnv.getModel(), generatorEnv);
				} catch (final Throwable t) {
					printStackTrace(annotatedElement, t, "Unexpected");
					break;
				}
			}
		}
	}

}
