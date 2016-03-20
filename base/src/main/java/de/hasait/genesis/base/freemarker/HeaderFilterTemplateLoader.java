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

package de.hasait.genesis.base.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Pattern;

import de.hasait.genesis.base.util.GenesisUtils;
import freemarker.cache.TemplateLoader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Filter header and empty lines after it.
 */
public class HeaderFilterTemplateLoader implements TemplateLoader {

	public static final Pattern FILTER_PATTERN = Pattern.compile("\\A<#--.*?-->\\s*", Pattern.DOTALL);
	private final TemplateLoader _delegate;

	public HeaderFilterTemplateLoader(final TemplateLoader pDelegate) {
		super();

		GenesisUtils.assertNotNull(pDelegate);
		_delegate = pDelegate;
	}

	@Override
	public void closeTemplateSource(final Object templateSource) throws IOException {
		// already closed
	}

	@Override
	public Object findTemplateSource(final String name) throws IOException {
		return _delegate.findTemplateSource(name);
	}

	@Override
	public long getLastModified(final Object templateSource) {
		return _delegate.getLastModified(templateSource);
	}

	@Override
	public Reader getReader(final Object templateSource, final String encoding) throws IOException {
		final Reader reader = _delegate.getReader(templateSource, encoding);
		try {
			String templateContent = IOUtils.toString(reader);
			templateContent = FILTER_PATTERN.matcher(templateContent).replaceFirst(StringUtils.EMPTY);
			return new StringReader(templateContent);
		} finally {
			_delegate.closeTemplateSource(templateSource);
		}
	}

}
