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

import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import de.hasait.genesis.processor.model.AbstractJType;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 *
 */
public class ModelWriter {

	private final freemarker.template.Configuration _configuration;
	private final ProcessingEnvironment _processingEnvironment;

	public ModelWriter(ProcessingEnvironment pProcessingEnvironment) {
		super();
		_processingEnvironment = pProcessingEnvironment;

		_configuration = new Configuration(Configuration.VERSION_2_3_23);
		_configuration.setDefaultEncoding("UTF-8");
		_configuration.setClassForTemplateLoading(ModelWriter.class, "/genesis/ftl/");
	}

	public void write(GeneratorEnv pGeneratorEnv) throws Exception {
		Util.assertNotNull(pGeneratorEnv);

		for (AbstractJType<?> jType : pGeneratorEnv.getModel().getCreatedTypes()) {
			Util.printNote(_processingEnvironment.getMessager(), pGeneratorEnv.getTypeElement(), "New Type: %s", jType);
			JavaFileObject sourceFile = _processingEnvironment.getFiler().createSourceFile(jType.getType().getType().getQualifiedName());
			try (Writer writer = sourceFile.openWriter()) {
				write(writer, jType);
			}
		}
	}

	private void write(Writer pWriter, Object pObject) throws Exception {
		Template template = _configuration.getTemplate(pObject.getClass().getSimpleName() + ".ftl");
		template.process(pObject, pWriter);
	}

}
