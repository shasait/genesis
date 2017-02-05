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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 */
public abstract class JSrcExpression extends AbstractJExpression implements JSrcSupported {

	@Nonnull
	public static JSrcExpression classValue(final @Nonnull JTypeUsage pType) {
		return new JSrcExpression() {

			@Override
			public String toSrc(final SrcContext pContext) {
				return pType.toSrc(pContext) + ".class";
			}

		};
	}

	@Nonnull
	public static JSrcExpression classValue(final @Nonnull JTypeReference pType) {
		return new JSrcExpression() {

			@Override
			public String toSrc(final SrcContext pContext) {
				return pType.toSrc(pContext) + ".class";
			}

		};
	}

	@Nonnull
	public static JSrcExpression customCode(final @Nonnull String pCode) {
		return new JSrcExpression() {

			@Override
			public String toSrc(final SrcContext pContext) {
				return pCode;
			}

		};
	}

	@Nonnull
	public static JSrcExpression stringValue(final @Nullable String pValue) {
		final String code;
		if (pValue == null) {
			code = "null";
		} else {
			code = "\"" + StringEscapeUtils.escapeJava(pValue) + "\"";
		}

		return customCode(code);
	}

	private JSrcExpression() {
		super();
	}

}
