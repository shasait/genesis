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

package de.hasait.genesis.scriptgen.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import de.hasait.genesis.base.model.JClass;
import de.hasait.genesis.base.model.JModel;

/**
 *
 */
public class JModelTest {

	@Test
	public void testModel() {
		final JModel model = new JModel(getClass());
		final String qualifiedClassName = "foo.bar.Suffix";
		final JClass jClass = model.createClass(qualifiedClassName);

		assertNotNull(model.getCreatedTypes());
		assertEquals(1, model.getCreatedTypes().size());
		assertEquals(jClass, model.getCreatedTypes().iterator().next());

		assertEquals(qualifiedClassName, jClass.getType().getType().getQualifiedName());
	}


}
