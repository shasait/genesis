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

package de.hasait.genesis.scriptgen;

import java.net.URL;

import de.hasait.genesis.base.GeneratorEnv;

/**
 *
 */
public final class ScriptEnv {

	private final GeneratorEnv _generatorEnv;
	private final URL _scriptFileURL;
	private final String[] _scriptArgs;

	ScriptEnv(final GeneratorEnv pGeneratorEnv, final URL pScriptFileURL, final String[] pScriptArgs) {
		super();

		_generatorEnv = pGeneratorEnv;
		_scriptFileURL = pScriptFileURL;
		_scriptArgs = pScriptArgs;
	}

	public GeneratorEnv getGeneratorEnv() {
		return _generatorEnv;
	}

	public String[] getScriptArgs() {
		return _scriptArgs;
	}

	public URL getScriptFileURL() {
		return _scriptFileURL;
	}

}
