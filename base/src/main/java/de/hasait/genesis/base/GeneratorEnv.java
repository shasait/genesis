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

import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import de.hasait.genesis.base.model.JClass;
import de.hasait.genesis.base.model.JModel;
import de.hasait.genesis.base.util.GenesisUtils;

/**
 *
 */
public final class GeneratorEnv implements ModelWriterEnv {

	private static final String JAVA_LANG_PACKAGE_PREFIX = "java.lang.";

	private final ProcessingEnvironment _processingEnvironment;
	private final Element _annotatedElement;
	private final JModel _model;

	GeneratorEnv(final ProcessingEnvironment pProcessingEnvironment, final Element pAnnotatedElement) {
		super();

		_processingEnvironment = pProcessingEnvironment;
		_annotatedElement = pAnnotatedElement;

		_model = new JModel();
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

	public void printError(final String pFormat, final Object... pArgs) {
		GenesisUtils.printError(_processingEnvironment.getMessager(), _annotatedElement, pFormat, pArgs);
	}

	public void printNote(final String pFormat, final Object... pArgs) {
		GenesisUtils.printNote(_processingEnvironment.getMessager(), _annotatedElement, pFormat, pArgs);
	}

	public void printStackTrace(final Throwable pThrowable, final String pFormat, final Object... pArgs) {
		GenesisUtils.printStackTrace(_processingEnvironment.getMessager(), _annotatedElement, pThrowable, pFormat, pArgs);
	}

	public String typeMirrorToJavaSrc(final TypeMirror pTypeMirror) {
		switch (pTypeMirror.getKind()) {
			case BOOLEAN:
				return "Boolean.TYPE";
			case BYTE:
				return "Byte.TYPE";
			case SHORT:
				return "Short.TYPE";
			case INT:
				return "Integer.TYPE";
			case LONG:
				return "Long.TYPE";
			case FLOAT:
				return "Float.TYPE";
			case DOUBLE:
				return "Double.TYPE";
			case CHAR:
				return "Character.TYPE";
		}

		final TypeMirror erasuredType = _processingEnvironment.getTypeUtils().erasure(pTypeMirror);

		String typeString = erasuredType.toString();

		if (typeString.startsWith(JAVA_LANG_PACKAGE_PREFIX)) {
			typeString = typeString.substring(JAVA_LANG_PACKAGE_PREFIX.length());
		}

		return typeString + ".class";
	}

}
