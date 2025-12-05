package CommonEnum;

public enum LockerSize {
    SMALL(1),
    MEDIUM(2),
    LARGE(3),
    EXTRA_LARGE(4);

    private final int priority;

    LockerSize(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public boolean canFit(LockerSize packageSize) {
        return this.priority >= packageSize.priority;
    }
}
