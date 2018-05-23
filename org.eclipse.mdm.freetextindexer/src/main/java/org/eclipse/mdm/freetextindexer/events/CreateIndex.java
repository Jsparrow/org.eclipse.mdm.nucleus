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
