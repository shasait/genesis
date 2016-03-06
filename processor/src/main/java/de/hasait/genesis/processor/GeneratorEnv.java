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

package de.hasait.genesis.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import de.hasait.genesis.processor.model.JModel;
import de.hasait.genesis.processor.util.GenesisUtils;

/**
 *
 */
public class GeneratorEnv {

	private static final String JAVA_LANG_PACKAGE_PREFIX = "java.lang.";

	private final ProcessingEnvironment _processingEnvironment;
	private final TypeElement _typeElement;
	private final String[] _scriptArgs;
	private final JModel _model;
	private URL _scriptFileURL;

	GeneratorEnv(final ProcessingEnvironment pProcessingEnvironment, final TypeElement pTypeElement, final String[] pScriptArgs) {
		super();

		_processingEnvironment = pProcessingEnvironment;
		_typeElement = pTypeElement;
		_scriptArgs = pScriptArgs;

		final PackageElement packageElement = GenesisUtils.findPackageElement(pTypeElement);
		final String qualifiedPackageName = packageElement == null ? null : packageElement.getQualifiedName().toString();
		final String className = pTypeElement.getSimpleName().toString();

		_model = new JModel(qualifiedPackageName, className);
	}

	public JavaFileObject createSourceFile(final String pName) throws IOException {
		return _processingEnvironment.getFiler().createSourceFile(pName, _typeElement);
	}

	public PrintWriter createSrcFileWithSuffix(final String pSuffix) throws IOException {
		final JavaFileObject srcFile = _processingEnvironment.getFiler().createSourceFile(_typeElement.getQualifiedName() + pSuffix);
		return new java.io.PrintWriter(srcFile.openWriter());
	}

	public JModel getModel() {
		return _model;
	}

	public String[] getScriptArgs() {
		return _scriptArgs;
	}

	public URL getScriptFileURL() {
		return _scriptFileURL;
	}

	public TypeElement getTypeElement() {
		return _typeElement;
	}

	public void printError(final String pFormat, final Object... pArgs) {
		GenesisUtils.printError(_processingEnvironment.getMessager(), _typeElement, pFormat, pArgs);
	}

	public void printNote(final String pFormat, final Object... pArgs) {
		GenesisUtils.printNote(_processingEnvironment.getMessager(), _typeElement, pFormat, pArgs);
	}

	public void printStackTrace(final Throwable pThrowable) {
		GenesisUtils.printStackTrace(_processingEnvironment.getMessager(), _typeElement, pThrowable);
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

	void setScriptFileURL(final URL pScriptFileURL) {
		_scriptFileURL = pScriptFileURL;
	}

}
