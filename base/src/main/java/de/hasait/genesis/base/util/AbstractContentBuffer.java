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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

public class AbstractContentBuffer<P extends ContentBuffer<?>> implements ContentBuffer<P> {

	private static final String DEFAULT_DEFAULT_INDENT = "\t";
	private static final String DEFAULT_NEWLINE = "\n";

	private final LinkedList<String> _indents = new LinkedList<>();

	private String _newline;
	private String _defaultIndent;
	private String _indent;
	private boolean _indentNeeded;

	private StringBuffer _buffer = new StringBuffer();

	public AbstractContentBuffer() {
		this(DEFAULT_DEFAULT_INDENT); //$NON-NLS-1$
	}

	public AbstractContentBuffer(final String pDefaultIndent) {
		this(pDefaultIndent, DEFAULT_NEWLINE); //$NON-NLS-1$
	}

	public AbstractContentBuffer(final String pDefaultIndent, final String pNewline) {
		super();

		setNewline(pNewline);
		setDefaultIndent(pDefaultIndent);
		updateIndent();
		_indentNeeded = true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final P a(final String pText) {
		bi();
		_buffer.append(pText);
		return (P) this;
	}

	@Override
	public final void c() {
		_buffer = new StringBuffer();
	}

	@Override
	public final void ci() {
		_indents.clear();
		updateIndent();
	}

	@Override
	public final String getContent() {
		return _buffer.toString();
	}

	@Override
	public final String getDefaultIndent() {
		return _defaultIndent;
	}

	@Override
	public final String getIndent() {
		return _indent;
	}

	@Override
	public final String getNewline() {
		return _newline;
	}

	@Override
	public final String i() {
		return i(_defaultIndent);
	}

	@Override
	public final String i(final String pCustomIndent) {
		_indents.addLast(pCustomIndent);
		updateIndent();
		return _indent;
	}

	@Override
	public final void p() {
		p(""); //$NON-NLS-1$
	}

	@Override
	public final void p(final String pLines) {
		if (pLines.length() == 0) {
			// a("");
			bn();
		} else {
			final BufferedReader lineReader = new BufferedReader(new StringReader(pLines));
			String line;
			try {
				while ((line = lineReader.readLine()) != null) {
					a(line);
					bn();
				}
			} catch (final IOException e) {
				// can not occur
			}
		}
	}

	@Override
	public final void pi(final String pLines) {
		pi(pLines, _defaultIndent);
	}

	@Override
	public final void pi(final String pLines, final String pIndent) {
		p(pLines);
		i(pIndent);
	}

	@Override
	public void pni(final String pLines) {
		if (pLines == null || pLines.length() == 0) {
			bn();
		} else {
			final BufferedReader lineReader = new BufferedReader(new StringReader(pLines));
			String line;
			try {
				while ((line = lineReader.readLine()) != null) {
					_buffer.append(line);
					bn();
				}
			} catch (final IOException e) {
				// can not occur
			}
		}
	}

	@Override
	public final void setDefaultIndent(final String pDefaultIndent) {
		if (pDefaultIndent == null) {
			throw new IllegalArgumentException("defaultIndent == null"); //$NON-NLS-1$
		}

		_defaultIndent = pDefaultIndent;
	}

	/**
	 * @param pNewline the newline to set
	 */
	@Override
	public final void setNewline(final String pNewline) {
		if (pNewline == null) {
			throw new IllegalArgumentException("newline == null"); //$NON-NLS-1$
		}

		_newline = pNewline;
	}

	@Override
	public String toString() {
		return getContent();
	}

	@Override
	public final String u() {
		final String oldIndent = _indents.removeLast();
		updateIndent();
		return oldIndent;
	}

	@Override
	public final void up(final String pLines) {
		u();
		p(pLines);
	}

	@Override
	public final void upi(final String pLines) {
		u();
		pi(pLines);
	}

	protected final void bi() {
		if (_indentNeeded) {
			_buffer.append(_indent);
			_indentNeeded = false;
		}
	}

	protected final void bn() {
		_buffer.append(_newline);
		_indentNeeded = true;
	}

	protected final void updateIndent() {
		final StringBuilder indent = new StringBuilder();
		_indents.forEach(indent::append);
		_indent = indent.toString();
	}

}
