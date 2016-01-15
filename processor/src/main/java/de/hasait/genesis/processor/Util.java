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

/**
 *
 */
public final class Util {

	public static final String EMPTY_STRING = "";
	private static final List<String> GETTER_PREFIXES = Collections.unmodifiableList(Arrays.asList("get", "is"));
	private static final String SETTER_PREFIX = "set";

	public static void assertNotNull(Object pValue) {
		if (pValue == null) {
			throw new NullPointerException();
		}
	}

	public static void assertTrue(boolean pTrue) {
		if (!pTrue) {
			throw new RuntimeException();
		}
	}

	public static String camelCaseToUpperUnderscore(String pInput) {
		if (isEmpty(pInput)) {
			return pInput;
		}

		Pattern camelPattern = Pattern.compile("\\p{Lower}\\p{Upper}");
		Matcher camelMatcher = camelPattern.matcher(pInput);

		StringBuilder result = new StringBuilder();

		int current = 0;
		while (camelMatcher.find(current)) {
			int split = camelMatcher.start() + 1;
			result.append(pInput.substring(current, split).toUpperCase());
			result.append('_');
			current = split;
		}
		result.append(pInput.substring(current).toUpperCase());

		return result.toString();
	}

	public static String determinePropertyNameFromAccessor(Element pElement) {
		for (String prefix : GETTER_PREFIXES) {
			if (isGetter(pElement, prefix)) {
				return extractPropertyNameFromAccessor(pElement.getSimpleName().toString(), prefix);
			}
		}

		if (isSetter(pElement, SETTER_PREFIX)) {
			return extractPropertyNameFromAccessor(pElement.getSimpleName().toString(), SETTER_PREFIX);
		}

		return null;
	}

	public static TypeMirror determinePropertyTypeFromAccessor(Element pElement) {
		if (isGetter(pElement)) {
			ExecutableElement element = (ExecutableElement) pElement;
			return element.getReturnType();
		}

		if (isSetter(pElement)) {
			ExecutableElement element = (ExecutableElement) pElement;
			return element.getParameters().get(0).asType();
		}

		return null;
	}

	public static String extractPropertyNameFromAccessor(String pAccessorName, String pPrefix) {
		String upperCamelCase = pAccessorName.substring(pPrefix.length());
		return firstLetterToLowercase(upperCamelCase);
	}

	public static PackageElement findPackageElement(Element pElement) {
		Element currentElement = pElement;
		while (currentElement != null && !(currentElement instanceof PackageElement)) {
			currentElement = currentElement.getEnclosingElement();
		}

		return (PackageElement) currentElement;
	}

	public static String firstLetterToLowercase(String pInput) {
		return pInput.substring(0, 1).toLowerCase() + pInput.substring(1);
	}

	public static boolean isBlank(String pInput) {
		return pInput == null || EMPTY_STRING.equals(pInput.trim());
	}

	public static boolean isEmpty(String pInput) {
		return pInput == null || EMPTY_STRING.equals(pInput);
	}

	public static boolean isGetter(Element pElement) {
		for (String prefix : GETTER_PREFIXES) {
			if (isGetter(pElement, prefix)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isGetter(Element pElement, String pPrefix) {
		Util.assertNotNull(pPrefix);

		if (isPublicMemberMethod(pElement)) {
			ExecutableElement element = (ExecutableElement) pElement;
			String simpleName = element.getSimpleName().toString();
			return simpleName.startsWith(pPrefix) && simpleName.length() > pPrefix.length() //
					&& element.getReturnType() != null && element.getParameters().isEmpty() //
					;
		}

		return false;
	}

	public static boolean isPublicMemberMethod(Element pElement) {
		if (pElement != null && pElement.getKind() == ElementKind.METHOD) {
			Set<Modifier> modifiers = pElement.getModifiers();
			return modifiers.contains(Modifier.PUBLIC) && !modifiers.contains(Modifier.STATIC);
		}

		return false;
	}

	public static boolean isSetter(Element pElement) {
		return isSetter(pElement, SETTER_PREFIX);
	}

	public static boolean isSetter(Element pElement, String pPrefix) {
		Util.assertNotNull(pPrefix);

		if (isPublicMemberMethod(pElement)) {
			ExecutableElement element = (ExecutableElement) pElement;
			String simpleName = element.getSimpleName().toString();

			return simpleName.startsWith(pPrefix) && simpleName.length() > pPrefix.length() //
					&& element.getParameters().size() == 1 // no return type check to support fluent setters
					;
		}

		return false;
	}


	public static void printError(Messager pMessager, Element pElement, String pFormat, Object... pArgs) {
		String message = String.format(pFormat, pArgs);
		pMessager.printMessage(Diagnostic.Kind.ERROR, message, pElement);
	}

	public static void printNote(Messager pMessager, Element pElement, String pFormat, Object... pArgs) {
		String message = String.format(pFormat, pArgs);
		pMessager.printMessage(Diagnostic.Kind.NOTE, message, pElement);
	}

	public static void printStackTrace(Messager pMessager, Element pElement, Throwable pThrowable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pThrowable.printStackTrace(pw);
		pw.flush();
		String stackTrace = sw.toString();
		String message = String.format("Unexpected throwable in %s: %s", GenesisProcessor.class.getSimpleName(), stackTrace);
		pMessager.printMessage(Diagnostic.Kind.ERROR, message, pElement);
	}

	private Util() {
		super();
	}
}
