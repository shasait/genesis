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

package de.hasait.genesis.processor.util;

public class JavaContentBuffer extends AbstractContentBuffer<JavaContentBuffer> {

	public static final String TOKEN_RB_OPEN = "("; //$NON-NLS-1$
	public static final String TOKEN_RB_CLOSE = ")"; //$NON-NLS-1$
	public static final String TOKEN_CB_OPEN = "{"; //$NON-NLS-1$
	public static final String TOKEN_CB_CLOSE = "}"; //$NON-NLS-1$
	public static final String TOKEN_SPACE = " "; //$NON-NLS-1$
	public static final String TOKEN_ASSIGN = "="; //$NON-NLS-1$
	public static final String TOKEN_EQ = "=="; //$NON-NLS-1$
	public static final String TOKEN_NEQ = "!="; //$NON-NLS-1$
	public static final String TOKEN_SC = ";"; //$NON-NLS-1$
	public static final String TOKEN_C = ","; //$NON-NLS-1$
	public static final String TOKEN_IF = "if"; //$NON-NLS-1$
	public static final String TOKEN_FINAL = "final"; //$NON-NLS-1$
	public static final String TOKEN_RETURN = "return"; //$NON-NLS-1$
	public static final String TOKEN_THIS = "this"; //$NON-NLS-1$
	public static final String TOKEN_NULL = "null"; //$NON-NLS-1$
	public static final String TOKEN_PACKAGE = "package"; //$NON-NLS-1$
	public static final String TOKEN_IMPORT = "import"; //$NON-NLS-1$
	public static final String TOKEN_PUBLIC = "public"; //$NON-NLS-1$
	public static final String TOKEN_PRIVATE = "private"; //$NON-NLS-1$
	public static final String TOKEN_CLASS = "class"; //$NON-NLS-1$
	public static final String TOKEN_EXTENDS = "extends"; //$NON-NLS-1$
	public static final String TOKEN_IMPLEMENTS = "implements"; //$NON-NLS-1$
	public static final String TOKEN_ABSTRACT = "abstract"; //$NON-NLS-1$
	public static final String TOKEN_VOID = "void"; //$NON-NLS-1$
	public static final String TOKEN_SUPER = "super"; //$NON-NLS-1$

	public JavaContentBuffer() {
		super();
	}

	public JavaContentBuffer(final String pDefaultIndent) {
		super(pDefaultIndent);
	}

	public JavaContentBuffer(final String pDefaultIndent, final String pNewline) {
		super(pDefaultIndent, pNewline);
	}

	public final JavaContentBuffer aPUBLIC() {
		return a(TOKEN_PUBLIC);
	}

	public final JavaContentBuffer aSPACE() {
		return a(TOKEN_SPACE);
	}

	public final JavaContentBuffer aVOID() {
		return a(TOKEN_VOID);
	}

	public final void assignValue(final String pVariableName, final String pExpression) {
		a(pVariableName).a(TOKEN_SPACE).a(TOKEN_ASSIGN).a(TOKEN_SPACE).a(pExpression).p(TOKEN_SC);
	}

	public final void blockCommentEnd() {
		blockCommentEnd(""); //$NON-NLS-1$
	}

	public final void blockCommentEnd(final String lines) {
		up(lines + " */"); //$NON-NLS-1$
	}

	public final void blockCommentStart() {
		pi("/*", " * "); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public final void blockCommentStart(final String lines) {
		pi("/* " + lines, " * "); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public final void blockEnd() {
		up(TOKEN_CB_CLOSE);
	}

	public final void blockStart() {
		pi(TOKEN_CB_OPEN);
	}

	public final void declareLocalVariable(final boolean pFinal, final String pType, final String pName) {
		if (pFinal) {
			a(TOKEN_FINAL).a(TOKEN_SPACE);
		}
		a(pType).a(TOKEN_SPACE).a(pName).p(TOKEN_SC);
	}

	public final void ifBlockStart(final String... pTokens) {
		a(TOKEN_IF).a(TOKEN_SPACE).a(TOKEN_RB_OPEN);
		for (final String token : pTokens) {
			a(token);
		}
		a(TOKEN_RB_CLOSE).a(TOKEN_SPACE).pi(TOKEN_CB_OPEN);
	}

	public final void ifIsBlockStart(final String pExpression1, final String pExpression2) {
		ifBlockStart(pExpression1, TOKEN_SPACE, TOKEN_EQ, TOKEN_SPACE, pExpression2);
	}

	public final void ifIsNotBlockStart(final String pExpression1, final String pExpression2) {
		ifBlockStart(pExpression1, TOKEN_SPACE, TOKEN_NEQ, TOKEN_SPACE, pExpression2);
	}

	public final void javaDocCommentEnd(final String lines) {
		up(lines + " */"); //$NON-NLS-1$
	}

	public final void javaDocCommentStart(final String lines) {
		pi("/** " + lines, " * "); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public final void javaDocEnd() {
		javaDocCommentEnd(""); //$NON-NLS-1$
	}

	public final void javaDocStart() {
		pi("/**", " * "); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public final void pSC() {
		p(TOKEN_SC);
	}

	public final void returnStatement(final String pExpression) {
		a(TOKEN_RETURN);
		if (pExpression != null) {
			a(TOKEN_SPACE).a(pExpression);
		}
		p(TOKEN_SC);
	}

}
