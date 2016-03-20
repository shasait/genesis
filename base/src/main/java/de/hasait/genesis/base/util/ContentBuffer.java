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

package de.hasait.genesis.base.util;

/**
 * A smart {@link String} buffer for creating Text with indent, like sourceCode.
 */
public interface ContentBuffer<P extends ContentBuffer<?>> {

	/**
	 * a = append: Append text.
	 */
	P a(String pText);

	/**
	 * c = clear: Clear content (indent is not cleared).
	 */
	void c();

	/**
	 * ci = clear indent: Clear indent.
	 */
	void ci();

	/**
	 * @return The produced content so far.
	 */
	String getContent();

	/**
	 * @return The defaultIndent.
	 */
	String getDefaultIndent();

	/**
	 * @return The current indent.
	 */
	String getIndent();

	/**
	 * @return The newline.
	 */
	String getNewline();

	/**
	 * i = indent: Add defaultIndent to indent-stack.
	 */
	String i();

	/**
	 * i = indent: Add newIndent to indent-stack.
	 */
	String i(String pNewIndent);

	/**
	 * p = print: Append newline.
	 */
	void p();

	/**
	 * p = print: Append lines and end with newline. If lines contains newlines, these will be indented, too.
	 */
	void p(final String pLines);

	/**
	 * pi = print indent: {@link #p(String)} followed by {@link #i()}.
	 */
	void pi(String pLines);

	/**
	 * pi = print indent: {@link #p(String)} followed by {@link #i()}.
	 */
	void pi(String pLines, String pIndent);

	/**
	 * pni = print no indent: Append lines without handling of contained newlines.
	 */
	void pni(final String pLines);

	/**
	 * @param pDefaultIndent The default indent to set.
	 */
	void setDefaultIndent(final String pDefaultIndent);

	/**
	 * @param pNewline The newline to set.
	 */
	void setNewline(final String pNewline);

	/**
	 * u = unindent: Remove top from indent-stack.
	 */
	String u();

	/**
	 * up = unindent print: {@link #u()} followed by {@link #p(String)}.
	 */
	void up(String pLines);

	/**
	 * up = unindent print indent: {@link #u()} followed by {@link #pi(String)}.
	 */
	void upi(String pLines);

}
