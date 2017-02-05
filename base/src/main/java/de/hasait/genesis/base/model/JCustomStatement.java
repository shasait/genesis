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

package de.hasait.genesis.base.model;

import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public final class JCustomStatement extends AbstractJStatement implements JSrcSupported {

	private String _customCode;

	JCustomStatement() {
		super();
	}

	public final String getCustomCode() {
		return _customCode;
	}

	public final void setCustomCode(final String pCustomCode) {
		_customCode = pCustomCode;
	}

	@Override
	public String toSrc(final SrcContext pContext) {
		return StringUtils.defaultString(_customCode);
	}

}
