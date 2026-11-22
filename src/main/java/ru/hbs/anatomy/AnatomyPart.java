package ru.hbs.anatomy;

public enum AnatomyPart {
    HEAD("Голова", PartType.REGION), BRAIN("Головной мозг", PartType.ORGAN),
    LEFT_EYE("Левый глаз", PartType.ORGAN), RIGHT_EYE("Правый глаз", PartType.ORGAN),
    LEFT_EAR("Левое ухо", PartType.ORGAN), RIGHT_EAR("Правое ухо", PartType.ORGAN),
    NECK("Шея", PartType.REGION), CERVICAL_SPINE("Шейный отдел позвоночника", PartType.BONE),
    SPINAL_CORD("Спинной мозг", PartType.NERVE),
    CHEST("Грудная клетка", PartType.REGION), HEART("Сердце", PartType.ORGAN),
    LEFT_LUNG("Левое лёгкое", PartType.ORGAN), RIGHT_LUNG("Правое лёгкое", PartType.ORGAN),
    TRACHEA("Трахея", PartType.ORGAN), LEFT_RIBS("Левые рёбра", PartType.BONE),
    RIGHT_RIBS("Правые рёбра", PartType.BONE),
    ABDOMEN("Живот", PartType.REGION), LIVER("Печень", PartType.ORGAN),
    STOMACH("Желудок", PartType.ORGAN), PANCREAS("Поджелудочная железа", PartType.ORGAN),
    SMALL_INTESTINE("Тонкий кишечник", PartType.ORGAN),
    LARGE_INTESTINE("Толстый кишечник", PartType.ORGAN),
    LEFT_KIDNEY("Левая почка", PartType.ORGAN), RIGHT_KIDNEY("Правая почка", PartType.ORGAN),
    BLADDER("Мочевой пузырь", PartType.ORGAN), PELVIS("Таз", PartType.BONE),
    LEFT_ARM("Левая рука", PartType.LIMB), RIGHT_ARM("Правая рука", PartType.LIMB),
    LEFT_LEG("Левая нога", PartType.LIMB), RIGHT_LEG("Правая нога", PartType.LIMB),
    LEFT_HUMERUS("Левая плечевая кость", PartType.BONE),
    RIGHT_HUMERUS("Правая плечевая кость", PartType.BONE),
    LEFT_FEMUR("Левая бедренная кость", PartType.BONE),
    RIGHT_FEMUR("Правая бедренная кость", PartType.BONE),
    AORTA("Аорта", PartType.VESSEL), CERVICAL_VESSELS("Сосуды шеи", PartType.VESSEL),
    THORACIC_VESSELS("Сосуды грудной клетки", PartType.VESSEL),
    ABDOMINAL_VESSELS("Сосуды живота", PartType.VESSEL),
    LEFT_ARM_MUSCLES("Мышцы левой руки", PartType.MUSCLE),
    RIGHT_ARM_MUSCLES("Мышцы правой руки", PartType.MUSCLE),
    LEFT_LEG_MUSCLES("Мышцы левой ноги", PartType.MUSCLE),
    RIGHT_LEG_MUSCLES("Мышцы правой ноги", PartType.MUSCLE),
    TORSO_MUSCLES("Мышцы корпуса", PartType.MUSCLE),
    LEFT_ARM_NERVES("Нервы левой руки", PartType.NERVE),
    RIGHT_ARM_NERVES("Нервы правой руки", PartType.NERVE),
    LEFT_LEG_NERVES("Нервы левой ноги", PartType.NERVE),
    RIGHT_LEG_NERVES("Нервы правой ноги", PartType.NERVE);

    private final String displayName;
    private final PartType type;
    AnatomyPart(String displayName, PartType type) {
        this.displayName = displayName;
        this.type = type;
    }
    public String displayName() { return displayName; }
    public PartType type() { return type; }
}
