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

package de.hasait.genesis.processor.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.tools.JavaFileObject;

import de.hasait.genesis.processor.GeneratorEnv;
import de.hasait.genesis.processor.ModelWriter;
import de.hasait.genesis.processor.model.AbstractJType;
import de.hasait.genesis.processor.util.GenesisUtils;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 *
 */
public class FreemarkerModelWriter implements ModelWriter {

	static void write(final Configuration pConfiguration, final Writer pWriter, final Object pModel, final Map pParams)
			throws IOException, TemplateException {
		write(pConfiguration, pWriter, pModel.getClass().getSimpleName() + ".ftl", pModel, pParams);
	}

	static void write(final Configuration pConfiguration, final Writer pWriter, final String pTemplateName, final Object pModel, final Map pParams)
			throws IOException, TemplateException {
		final Template template = pConfiguration.getTemplate(pTemplateName);
		template.process(new Context(pModel, pParams), pWriter);
	}

	private final Configuration _configuration;

	public FreemarkerModelWriter() {
		super();

		_configuration = new Configuration(Configuration.VERSION_2_3_23);
		_configuration.setDefaultEncoding("UTF-8");
		_configuration.setSharedVariable("delegate", new DelegateDirective());
		final ClassTemplateLoader templateLoader = new ClassTemplateLoader(getClass(), "/genesis/ftl/");
		_configuration.setTemplateLoader(new HeaderFilterTemplateLoader(templateLoader));
	}

	@Override
	public void write(final GeneratorEnv pGeneratorEnv) throws Exception {
		GenesisUtils.assertNotNull(pGeneratorEnv);

		for (final AbstractJType<?> jType : pGeneratorEnv.getModel().getCreatedTypes()) {
			pGeneratorEnv.printNote("New Type: %s", jType);
			final JavaFileObject sourceFile = pGeneratorEnv.createSourceFile(jType.getType().getType().getQualifiedName());
			try (Writer writer = sourceFile.openWriter()) {
				write(_configuration, writer, jType, null);
			}
		}
	}

	public static class Context {

		private final Object _model;
		private final Map _params;

		public Context(final Object pModel, final Map pParams) {
			_model = pModel;
			_params = pParams;
		}

		public Object getModel() {
			return _model;
		}

		public Map getParams() {
			return _params;
		}
	}

}
