package ru.hbs.model;

public final class PartCondition {
    private final double integrity;
    private final double functionality;
    public PartCondition(double integrity, double functionality) {
        this.integrity = clamp(integrity);
        this.functionality = clamp(functionality);
    }
    public static PartCondition healthy() { return new PartCondition(100, 100); }
    public double integrity() { return integrity; }
    public double functionality() { return functionality; }
    private static double clamp(double value) { return Math.max(0, Math.min(100, value)); }
}
