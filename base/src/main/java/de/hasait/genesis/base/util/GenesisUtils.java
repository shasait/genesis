/*
 * Copyright (C) 2017 by Sebastian Hasait (sebastian at hasait dot de)
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

package de.hasait.genesis.base.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public final class GenesisUtils {

	private static final List<String> GETTER_PREFIXES = Collections.unmodifiableList(Arrays.asList("get", "is"));
	private static final String SETTER_PREFIX = "set";

	public static void assertNotNull(final Object pValue) {
		if (pValue == null) {
			throw new NullPointerException();
		}
	}

	public static void assertTrue(final boolean pTrue) {
		if (!pTrue) {
			throw new RuntimeException();
		}
	}

	public static String camelCaseToUpperUnderscore(final String pInput) {
		if (StringUtils.isEmpty(pInput)) {
			return pInput;
		}

		final Pattern camelPattern = Pattern.compile("\\p{Lower}\\p{Upper}");
		final Matcher camelMatcher = camelPattern.matcher(pInput);

		final StringBuilder result = new StringBuilder();

		int current = 0;
		while (camelMatcher.find(current)) {
			final int split = camelMatcher.start() + 1;
			result.append(pInput.substring(current, split).toUpperCase());
			result.append('_');
			current = split;
		}
		result.append(pInput.substring(current).toUpperCase());

		return result.toString();
	}

	public static String determinePropertyNameFromAccessor(final Element pElement) {
		for (final String prefix : GETTER_PREFIXES) {
			if (isGetter(pElement, prefix)) {
				return extractPropertyNameFromAccessor(pElement.getSimpleName().toString(), prefix);
			}
		}

		if (isSetter(pElement, SETTER_PREFIX)) {
			return extractPropertyNameFromAccessor(pElement.getSimpleName().toString(), SETTER_PREFIX);
		}

		return null;
	}

	public static TypeMirror determinePropertyTypeFromAccessor(final Element pElement) {
		if (isGetter(pElement)) {
			final ExecutableElement element = (ExecutableElement) pElement;
			return element.getReturnType();
		}

		if (isSetter(pElement)) {
			final ExecutableElement element = (ExecutableElement) pElement;
			return element.getParameters().get(0).asType();
		}

		return null;
	}

	public static String extractPropertyNameFromAccessor(final String pAccessorName, final String pPrefix) {
		final String upperCamelCase = pAccessorName.substring(pPrefix.length());
		return firstLetterToLowercase(upperCamelCase);
	}

	public static PackageElement findPackageElement(final Element pElement) {
		Element currentElement = pElement;
		while (currentElement != null && !(currentElement instanceof PackageElement)) {
			currentElement = currentElement.getEnclosingElement();
		}

		return (PackageElement) currentElement;
	}

	public static String firstLetterToLowercase(final String pInput) {
		return pInput.substring(0, 1).toLowerCase() + pInput.substring(1);
	}

	public static boolean isGetter(final Element pElement) {
		for (final String prefix : GETTER_PREFIXES) {
			if (isGetter(pElement, prefix)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isGetter(final Element pElement, final String pPrefix) {
		GenesisUtils.assertNotNull(pPrefix);

		if (isPublicMemberMethod(pElement)) {
			final ExecutableElement element = (ExecutableElement) pElement;
			final String simpleName = element.getSimpleName().toString();
			return simpleName.startsWith(pPrefix) && simpleName.length() > pPrefix.length() //
					&& element.getReturnType() != null && element.getParameters().isEmpty() //
					;
		}

		return false;
	}

	public static boolean isPublicMemberMethod(final Element pElement) {
		if (pElement != null && pElement.getKind() == ElementKind.METHOD) {
			final Set<Modifier> modifiers = pElement.getModifiers();
			return modifiers.contains(Modifier.PUBLIC) && !modifiers.contains(Modifier.STATIC);
		}

		return false;
	}

	public static boolean isSetter(final Element pElement) {
		return isSetter(pElement, SETTER_PREFIX);
	}

	public static boolean isSetter(final Element pElement, final String pPrefix) {
		GenesisUtils.assertNotNull(pPrefix);

		if (isPublicMemberMethod(pElement)) {
			final ExecutableElement element = (ExecutableElement) pElement;
			final String simpleName = element.getSimpleName().toString();

			return simpleName.startsWith(pPrefix) && simpleName.length() > pPrefix.length() //
					&& element.getParameters().size() == 1 // no return type check to support fluent setters
					;
		}

		return false;
	}

	public static void printError(final Messager pMessager, final Element pElement, final String pFormat, final Object... pArgs) {
		final String message = String.format(pFormat, pArgs);
		pMessager.printMessage(Diagnostic.Kind.ERROR, message, pElement);
	}

	public static void printNote(final Messager pMessager, final Element pElement, final String pFormat, final Object... pArgs) {
		final String message = String.format(pFormat, pArgs);
		pMessager.printMessage(Diagnostic.Kind.NOTE, message, pElement);
	}

	public static void printStackTrace(final Messager pMessager, final Element pElement, final Throwable pThrowable, final String pFormat, final Object... pArgs) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		final String message = String.format(pFormat, pArgs);
		pw.println(message);
		pThrowable.printStackTrace(pw);
		pw.flush();
		pMessager.printMessage(Diagnostic.Kind.ERROR, sw.toString(), pElement);
	}

	public static String readFileToString(final File pFile) throws IOException {
		return pFile != null && pFile.isFile() ? FileUtils.readFileToString(pFile) : null;
	}

	public static boolean writeIfNonWhitespaceChanged(final String pContent, final File pFile) throws IOException {
		final String currentContent = readFileToString(pFile);
		final String content = StringUtils.defaultString(pContent);
		if (currentContent != null) {
			if (reduceWhitespacesToSpace(content).equals(reduceWhitespacesToSpace(currentContent))) {
				return false;
			}
		}
		FileUtils.write(pFile, content);
		return true;
	}

	private static String reduceWhitespacesToSpace(final String pString) {
		String result = pString;
		result = result.replaceAll("\\s+", " "); //$NON-NLS-1$ //$NON-NLS-2$
		return result;
	}


	private GenesisUtils() {
		super();
	}
}
