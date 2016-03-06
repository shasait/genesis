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

package de.hasait.genesis.processor.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class JModelTest {

	@Test
	public void testModel() {
		final JModel model = new JModel("bar.foo", "SomeClass");
		final JClass jClass = model.createRelativeClass("Suffix");

		assertNotNull(model.getCreatedTypes());
		assertEquals(1, model.getCreatedTypes().size());
		assertEquals(jClass, model.getCreatedTypes().iterator().next());

		assertEquals("bar.foo.SomeClassSuffix", jClass.getType().getType().getQualifiedName());
	}

}
