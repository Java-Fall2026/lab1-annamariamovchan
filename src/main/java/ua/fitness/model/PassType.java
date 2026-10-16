package ua.fitness.model;

public enum PassType {
    STANDARD(1.0),
    PREMIUM(1.5),
    VIP(2.0),
    STUDENT(0.75);

    private final double priceFactor;

    PassType(double priceFactor) {
        this.priceFactor = priceFactor;
    }

    public double getPriceFactor() {
        return priceFactor;
    }
}