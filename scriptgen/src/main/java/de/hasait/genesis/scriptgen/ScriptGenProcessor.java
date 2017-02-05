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

package de.hasait.genesis.scriptgen;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.lang3.StringUtils;

import de.hasait.genesis.base.AbstractGenesisProcessor;
import de.hasait.genesis.base.GeneratorEnv;

@SupportedOptions({
		ScriptGenProcessor.OPTION___LOCATIONS
})
public class ScriptGenProcessor extends AbstractGenesisProcessor {

	public static final String OPTION___LOCATIONS = "genesis.locations";

	public static final String SCRIPT_LOCATIONS_SPLIT = ";";

	private final List<String> _locations = new ArrayList<>();

	@Override
	public synchronized void init(final ProcessingEnvironment pProcessingEnv) {
		super.init(pProcessingEnv);

		_locations.add("genesis");
		final String rawLocations = processingEnv.getOptions().get(OPTION___LOCATIONS);
		if (rawLocations != null) {
			Collections.addAll(_locations, rawLocations.split(SCRIPT_LOCATIONS_SPLIT));
		}

		registerGenerator(ScriptGen.class, this::processScriptGen);
	}

	private ScriptEngine determineScriptEngine(final String pScriptFileExtension, final ClassLoader pClassLoader) {
		final ScriptEngine engine;
		final NashornScriptEngineFactory nashornScriptEngineFactory = new NashornScriptEngineFactory();
		if (nashornScriptEngineFactory.getExtensions().contains(pScriptFileExtension)) {
			engine = nashornScriptEngineFactory.getScriptEngine(pClassLoader);
		} else {
			final ScriptEngineManager factory = new ScriptEngineManager();
			engine = factory.getEngineByExtension(pScriptFileExtension);
		}
		return engine;
	}

	private void processScriptGen(final ScriptGen pScriptGenAnnotation, final GeneratorEnv pGeneratorEnv) throws Exception {
		final String scriptResourcePath = pScriptGenAnnotation.script();
		if (StringUtils.isBlank(scriptResourcePath)) {
			pGeneratorEnv.printError("Parameter \"script\" must not be blank");
			return;
		}

		final ClassLoader classLoader = getClass().getClassLoader();

		URL scriptFileURL = classLoader.getResource(scriptResourcePath);
		if (scriptFileURL == null) {
			for (final String scriptLocationString : _locations) {
				scriptFileURL = classLoader.getResource(scriptLocationString + "/" + scriptResourcePath);
				if (scriptFileURL != null) {
					break;
				}
			}
		}
		if (scriptFileURL == null) {
			pGeneratorEnv.printError("Script \"%s\" not found", scriptResourcePath);
			return;
		}

		final int indexOfDot = scriptResourcePath.indexOf('.');
		if (indexOfDot <= 1) {
			pGeneratorEnv.printError("Script name \"%s\" must have an extension", scriptResourcePath);
			return;
		}

		final String scriptFileExtension = scriptResourcePath.substring(indexOfDot + 1);

		final ScriptEngine engine = determineScriptEngine(scriptFileExtension, classLoader);

		if (engine == null) {
			pGeneratorEnv.printError("Script extension \"%s\" unsupported", scriptFileExtension);
			return;
		}

		try (InputStream scriptFileIn = scriptFileURL.openStream(); Reader scriptFileR = new InputStreamReader(scriptFileIn)) {
			engine.eval(scriptFileR);
			final ScriptEnv scriptEnv = new ScriptEnv(pGeneratorEnv, scriptFileURL, pScriptGenAnnotation.args());
			((Invocable) engine).invokeFunction("generate", scriptEnv);
		}
	}

}
