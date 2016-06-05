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

package de.hasait.genesis.base.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import de.hasait.genesis.base.util.GenesisUtils;

/**
 *
 */
public final class JAnnotation {

	private final JTypeReference _type;
	private final List<JAssignmentStatement> _argAssignments = new ArrayList<JAssignmentStatement>();

	JAnnotation(final @Nonnull JTypeReference pType) {
		super();
		GenesisUtils.assertNotNull(pType);

		_type = pType;
	}

	public final void addArgAssignment(final @Nonnull String pTargetName, final @Nonnull AbstractJExpression pSourceExpression) {
		final JAssignmentStatement assignmentStatement = new JAssignmentStatement(pTargetName, pSourceExpression);
		_argAssignments.add(assignmentStatement);
	}

	@Nonnull
	public List<JAssignmentStatement> getArgAssignments() {
		return Collections.unmodifiableList(_argAssignments);
	}

	@Nonnull
	public JTypeReference getType() {
		return _type;
	}

}
