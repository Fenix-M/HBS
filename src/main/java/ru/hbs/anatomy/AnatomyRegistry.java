package ru.hbs.anatomy;

import java.util.Arrays;
import java.util.List;

public final class AnatomyRegistry {
    private final List<AnatomyPart> parts = List.copyOf(Arrays.asList(AnatomyPart.values()));
    public List<AnatomyPart> all() { return parts; }
    public int size() { return parts.size(); }
}
