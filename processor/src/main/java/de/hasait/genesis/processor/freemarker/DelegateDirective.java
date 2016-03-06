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
import java.util.HashMap;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.DeepUnwrap;

/**
 *
 */
public class DelegateDirective implements TemplateDirectiveModel {

	@SuppressWarnings({
			"rawtypes",
			"unchecked"
	})
	@Override
	public void execute(final Environment env, final Map params, final TemplateModel[] loopVars, final TemplateDirectiveBody body)
			throws TemplateException, IOException {
		final Map delegateParams = new HashMap(params);
		final Object model = DeepUnwrap.unwrap((TemplateModel) delegateParams.remove("model"));
		final SimpleScalar template = (SimpleScalar) delegateParams.remove("template");
		final String templateName = template == null ? null : template.getAsString();
		if (templateName != null) {
			FreemarkerModelWriter.write(env.getConfiguration(), env.getOut(), templateName, model, delegateParams);
		} else {
			FreemarkerModelWriter.write(env.getConfiguration(), env.getOut(), model, delegateParams);
		}
	}

}
