package ru.hbs.model;

import ru.hbs.anatomy.AnatomyPart;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public final class BodyProfile {
    private final UUID playerId;
    private final Map<AnatomyPart, PartCondition> parts;
    private final double bloodPercent, oxygenPercent, consciousnessPercent;

    public BodyProfile(UUID playerId, Map<AnatomyPart, PartCondition> parts,
                       double blood, double oxygen, double consciousness) {
        this.playerId = playerId;
        EnumMap<AnatomyPart, PartCondition> copy = new EnumMap<>(AnatomyPart.class);
        copy.putAll(parts);
        this.parts = Collections.unmodifiableMap(copy);
        this.bloodPercent = clamp(blood);
        this.oxygenPercent = clamp(oxygen);
        this.consciousnessPercent = clamp(consciousness);
    }
    public static BodyProfile healthy(UUID id, Iterable<AnatomyPart> anatomy) {
        EnumMap<AnatomyPart, PartCondition> parts = new EnumMap<>(AnatomyPart.class);
        for (AnatomyPart part : anatomy) parts.put(part, PartCondition.healthy());
        return new BodyProfile(id, parts, 100, 100, 100);
    }
    public UUID playerId() { return playerId; }
    public Map<AnatomyPart, PartCondition> parts() { return parts; }
    public double bloodPercent() { return bloodPercent; }
    public double oxygenPercent() { return oxygenPercent; }
    public double consciousnessPercent() { return consciousnessPercent; }
    private static double clamp(double value) { return Math.max(0, Math.min(100, value)); }
}
