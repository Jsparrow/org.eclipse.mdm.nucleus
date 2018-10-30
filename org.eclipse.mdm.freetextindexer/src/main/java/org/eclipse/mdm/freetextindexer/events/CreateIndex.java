/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/

package org.eclipse.mdm.freetextindexer.events;

import com.google.common.collect.ImmutableList;
import org.eclipse.mdm.api.base.model.Entity;

import java.util.Objects;

public class CreateIndex {
    private final String sourceName;
    private final ImmutableList<Class<? extends Entity>> entitiesToIndex;

    @SafeVarargs
    public CreateIndex(String sourceName, Class<? extends Entity> ... entitiesToIndex) {
        this.sourceName = Objects.requireNonNull(sourceName, "Source name may never be null!");
        this.entitiesToIndex = ImmutableList.copyOf(entitiesToIndex);
    }

    public ImmutableList<Class<? extends Entity>> getEntitiesToIndex() {
        return entitiesToIndex;
    }


    public String getSourceName() {
        return sourceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateIndex that = (CreateIndex) o;
        return Objects.equals(sourceName, that.sourceName) &&
                Objects.equals(entitiesToIndex, that.entitiesToIndex);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sourceName, entitiesToIndex);
    }
}
