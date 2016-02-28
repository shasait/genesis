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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import de.hasait.genesis.annotations.Genesis;
import de.hasait.genesis.processor.freemarker.FreemarkerModelWriter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

/**
 *
 */
@SupportedAnnotationTypes("de.hasait.genesis.annotations.Genesis")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({
		GenesisProcessor.OPTION___LOCATIONS
})
public class GenesisProcessor extends AbstractProcessor {

	public static final String OPTION___LOCATIONS = "genesis.locations";

	public static final String SCRIPT_LOCATIONS_SPLIT = ";";
	public static final String EXECUTE_PREFIX_CLASS = "class:";
	public static final String EXECUTE_PREFIX_SCRIPT_RESOURCE = "script:";

	private Configuration _configuration;

	@Override
	public synchronized void init(ProcessingEnvironment pProcessingEnv) {
		super.init(pProcessingEnv);

		_configuration = createConfiguration();
	}

	@Override
	public boolean process(Set<? extends TypeElement> pAnnotations, RoundEnvironment pRoundEnv) {

		for (TypeElement annotation : pAnnotations) {
			Set<? extends Element> annotatedElements;
			try {
				annotatedElements = pRoundEnv.getElementsAnnotatedWith(annotation);
			} catch (Throwable t) {
				printStackTrace(annotation, t);
				continue;
			}

			Consumer<Element> elementProcessor = _configuration.getElementProcessor(annotation.getQualifiedName().toString());

			for (Element annotatedElement : annotatedElements) {
				if (elementProcessor != null) {
					printNote(annotatedElement, "Processing: %s, %s", annotatedElement, annotation);

					try {
						elementProcessor.accept(annotatedElement);
					} catch (Throwable t) {
						printStackTrace(annotatedElement, t);
						break;
					}
				} else {
					printError(annotatedElement, "No element processor found: %s, %s", annotatedElement, annotation);
				}
			}

		}

		return true;
	}

	private Configuration createConfiguration() {
		Configuration configuration = new Configuration();
		configuration.addLocation("genesis");
		String rawLocations = processingEnv.getOptions().get(OPTION___LOCATIONS);
		if (rawLocations != null) {
			for (String location : rawLocations.split(SCRIPT_LOCATIONS_SPLIT)) {
				configuration.addLocation(location);
			}
		}

		configuration.registerElementProcessor(Genesis.class.getName(), this::processGenesis);

		configuration.setModelWriter(new FreemarkerModelWriter());

		return configuration;
	}

	private ScriptEngine determineScriptEngine(String pScriptFileExtension, ClassLoader pClassLoader) {
		ScriptEngine engine;
		NashornScriptEngineFactory nashornScriptEngineFactory = new NashornScriptEngineFactory();
		if (nashornScriptEngineFactory.getExtensions().contains(pScriptFileExtension)) {
			engine = nashornScriptEngineFactory.getScriptEngine(pClassLoader);
		} else {
			ScriptEngineManager factory = new ScriptEngineManager();
			engine = factory.getEngineByExtension(pScriptFileExtension);
		}
		return engine;
	}

	private void printError(Element pElement, String pFormat, Object... pArgs) {
		Util.printError(processingEnv.getMessager(), pElement, pFormat, pArgs);
	}

	private void printNote(Element pElement, String pFormat, Object... pArgs) {
		Util.printNote(processingEnv.getMessager(), pElement, pFormat, pArgs);
	}

	private void printStackTrace(Element pElement, Throwable pThrowable) {
		Util.printStackTrace(processingEnv.getMessager(), pElement, pThrowable);
	}

	private void processGenesis(Element pAnnotatedElement) {
		try {
			if (pAnnotatedElement.getKind() != ElementKind.CLASS) {
				return;
			}

			ClassLoader classLoader = getClass().getClassLoader();

			TypeElement classElement = (TypeElement) pAnnotatedElement;

			Genesis genesisAnnotation = classElement.getAnnotation(Genesis.class);
			String parameterExecute = genesisAnnotation.execute();
			if (Util.isBlank(parameterExecute)) {
				printError(classElement, "Parameter \"execute\" must not be blank");
				return;
			}

			GeneratorEnv generatorEnv = new GeneratorEnv(processingEnv, classElement, genesisAnnotation.args());

			if (parameterExecute.startsWith(EXECUTE_PREFIX_CLASS)) {
				String className = parameterExecute.substring(EXECUTE_PREFIX_CLASS.length());
				Class<?> delegateClass = classLoader.loadClass(className);
				delegateClass.getMethod("genesis", GeneratorEnv.class).invoke(null, generatorEnv);
			} else if (parameterExecute.startsWith(EXECUTE_PREFIX_SCRIPT_RESOURCE)) {
				String scriptResourcePath = parameterExecute.substring(EXECUTE_PREFIX_SCRIPT_RESOURCE.length());

				URL scriptFileURL = classLoader.getResource(scriptResourcePath);
				if (scriptFileURL == null) {
					for (String scriptLocationString : _configuration.getLocations()) {
						scriptFileURL = classLoader.getResource(scriptLocationString + "/" + scriptResourcePath);
						if (scriptFileURL != null) {
							break;
						}
					}
				}
				if (scriptFileURL == null) {
					printError(classElement, "Script \"%s\" not found", scriptResourcePath);
					return;
				}

				int indexOfDot = scriptResourcePath.indexOf('.');
				if (indexOfDot <= 1) {
					printError(classElement, "Script name \"%s\" must have an extension", scriptResourcePath);
					return;
				}

				String scriptFileExtension = scriptResourcePath.substring(indexOfDot + 1);

				ScriptEngine engine = determineScriptEngine(scriptFileExtension, classLoader);

				if (engine == null) {
					printError(classElement, "Script extension \"%s\" unsupported", scriptFileExtension);
					return;
				}

				try (InputStream scriptFileIn = scriptFileURL.openStream(); Reader scriptFileR = new InputStreamReader(scriptFileIn)) {
					engine.eval(scriptFileR);
					generatorEnv.setScriptFileURL(scriptFileURL);
					((Invocable) engine).invokeFunction("genesis", generatorEnv);
				}
			} else {
				printError(classElement, "Parameter \"execute\" invalid: %s", parameterExecute);
				return;
			}

			_configuration.getModelWriter().write(generatorEnv);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
