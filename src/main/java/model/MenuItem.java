package model;

public enum MenuItem {
    COFFEE(5f),
    CHAI(4f),
    BANANASMOOTHIE(6f),
    STRAWBERRYSHAKE(7f),
    MOHITO(7.5f);

    private final float price;

    MenuItem(float price) {
        this.price = price;
    }

    public float getPrice() {
        return this.price;
    }

}
