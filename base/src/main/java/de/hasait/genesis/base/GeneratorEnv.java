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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.tools.JavaFileObject;

import de.hasait.genesis.base.model.JClass;
import de.hasait.genesis.base.model.JModel;
import de.hasait.genesis.base.model.JTypeArgument;
import de.hasait.genesis.base.model.JTypeReference;
import de.hasait.genesis.base.model.JTypeUsage;
import de.hasait.genesis.base.util.GenesisUtils;

/**
 *
 */
public final class GeneratorEnv implements ModelWriterEnv {

	private final ProcessingEnvironment _processingEnvironment;
	private final RoundEnvironment _roundEnvironment;
	private final Element _annotatedElement;
	private final JModel _model;

	GeneratorEnv(final Class<? extends AbstractGenesisProcessor> pProcessorClass, final ProcessingEnvironment pProcessingEnvironment, final RoundEnvironment pRoundEnvironment, final Element pAnnotatedElement) {
		super();

		_processingEnvironment = pProcessingEnvironment;
		_roundEnvironment = pRoundEnvironment;
		_annotatedElement = pAnnotatedElement;

		_model = new JModel(pProcessorClass);
	}

	@Nonnull
	@Override
	public PrintWriter createJavaSrcFile(@Nonnull final String pQualifiedName) throws IOException {
		return new java.io.PrintWriter(createJavaSrcFileObject(pQualifiedName).openWriter());
	}

	@Nonnull
	public JavaFileObject createJavaSrcFileObject(@Nonnull final String pQualifiedName) throws IOException {
		return _processingEnvironment.getFiler().createSourceFile(pQualifiedName, _annotatedElement);
	}

	public JClass createRelativeClass(@Nonnull final String pSuffix) {
		GenesisUtils.assertNotNull(pSuffix);
		GenesisUtils.assertTrue(_annotatedElement instanceof TypeElement);

		final TypeElement typeElement = (TypeElement) _annotatedElement;
		return _model.createClass(typeElement.getQualifiedName() + pSuffix);
	}

	@Nonnull
	public PrintWriter createRelativeJavaSrcFile(@Nonnull final String pSuffix) throws IOException {
		GenesisUtils.assertNotNull(pSuffix);
		GenesisUtils.assertTrue(_annotatedElement instanceof TypeElement);

		final TypeElement typeElement = (TypeElement) _annotatedElement;
		final JavaFileObject srcFile = _processingEnvironment.getFiler().createSourceFile(typeElement.getQualifiedName() + pSuffix);
		return new java.io.PrintWriter(srcFile.openWriter());
	}

	public Element getAnnotatedElement() {
		return _annotatedElement;
	}

	public JModel getModel() {
		return _model;
	}

	/**
	 * @see RoundEnvironment#errorRaised()
	 */
	public boolean isErrorRaised() {
		return _roundEnvironment.errorRaised();
	}

	/**
	 * @see RoundEnvironment#processingOver()
	 */
	public boolean isProcessingOver() {
		return _roundEnvironment.processingOver();
	}

	public void printError(final String pFormat, final Object... pArgs) {
		GenesisUtils.printError(_processingEnvironment.getMessager(), _annotatedElement, pFormat, pArgs);
	}

	public void printNote(final String pFormat, final Object... pArgs) {
		GenesisUtils.printNote(_processingEnvironment.getMessager(), _annotatedElement, pFormat, pArgs);
	}

	public void printStackTrace(final Throwable pThrowable, final String pFormat, final Object... pArgs) {
		GenesisUtils.printStackTrace(_processingEnvironment.getMessager(), _annotatedElement, pThrowable, pFormat, pArgs);
	}

	@Nonnull
	public JTypeUsage typeMirrorToJTypeUsage(@Nonnull final TypeMirror pTypeMirror) {
		return typeMirrorToJTypeUsage(pTypeMirror, null);
	}

	@Nonnull
	private JTypeArgument typeMirrorToJTypeArgument(@Nonnull final TypeMirror pTypeMirror) {
		GenesisUtils.assertNotNull(pTypeMirror);

		final TypeKind kind = pTypeMirror.getKind();

		if (kind == TypeKind.WILDCARD) {
			final WildcardType wildcardType = (WildcardType) pTypeMirror;
			if (wildcardType.getExtendsBound() != null) {
				return JTypeArgument.createExtends(typeMirrorToJTypeUsage(wildcardType.getExtendsBound()));
			}
			if (wildcardType.getSuperBound() != null) {
				return JTypeArgument.createSuper(typeMirrorToJTypeUsage(wildcardType.getSuperBound()));
			}
			return JTypeArgument.createAny();
		}

		return JTypeArgument.createExact(typeMirrorToJTypeUsage(pTypeMirror));
	}

	@Nonnull
	private JTypeReference typeMirrorToJTypeReference(@Nonnull final TypeMirror pTypeMirror) {
		GenesisUtils.assertNotNull(pTypeMirror);

		final TypeKind kind = pTypeMirror.getKind();

		if (kind.isPrimitive()) {
			switch (kind) {
				case BOOLEAN:
					return JTypeReference.BOOLEAN;
				case BYTE:
					return JTypeReference.BYTE;
				case SHORT:
					return JTypeReference.SHORT;
				case INT:
					return JTypeReference.INT;
				case LONG:
					return JTypeReference.LONG;
				case FLOAT:
					return JTypeReference.FLOAT;
				case DOUBLE:
					return JTypeReference.DOUBLE;
				case CHAR:
					return JTypeReference.CHAR;
			}
		} else if (kind == TypeKind.DECLARED) {
			final DeclaredType declaredType = (DeclaredType) pTypeMirror;
			final TypeElement typeElement = (TypeElement) declaredType.asElement();
			return _model.createOrGetTypeReference(typeElement.getQualifiedName().toString());
		}

		throw new RuntimeException("Unsupported TypeMirror kind: " + kind);
	}

	@Nonnull
	private JTypeUsage typeMirrorToJTypeUsage(@Nonnull final TypeMirror pTypeMirror, final List<Integer> pArrayDimensions) {
		GenesisUtils.assertNotNull(pTypeMirror);

		final TypeKind kind = pTypeMirror.getKind();

		if (kind == TypeKind.ARRAY) {
			final ArrayType arrayType = (ArrayType) pTypeMirror;
			final List<Integer> arrayDimensions = pArrayDimensions != null ? pArrayDimensions : new ArrayList<Integer>();
			arrayDimensions.add(null);
			return typeMirrorToJTypeUsage(arrayType.getComponentType(), arrayDimensions);
		}

		final JTypeReference typeReference = typeMirrorToJTypeReference(pTypeMirror);
		final JTypeUsage typeUsage = new JTypeUsage(typeReference);
		if (pArrayDimensions != null) {
			for (final Integer dimension : pArrayDimensions) {
				typeUsage.addArrayDimension(dimension);
			}
		}

		if (kind == TypeKind.DECLARED) {
			final DeclaredType declaredType = (DeclaredType) pTypeMirror;
			for (final TypeMirror argument : declaredType.getTypeArguments()) {
				typeUsage.addArgument(typeMirrorToJTypeArgument(argument));
			}
		}

		return typeUsage;
	}

}
