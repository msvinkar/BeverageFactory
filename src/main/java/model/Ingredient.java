package model;

public enum Ingredient {
    COFFEE(3f),
    TEA(2f),
    BANANA(4f),
    STRAWBERRY(5f),
    LEMON(5.5f),
    MILK(1f),
    SUGAR(0.5f),
    SODA(0.5f),
    MINT(0.5f),
    WATER(0.5f);

    final float price;

    Ingredient(float price) {
        this.price = price;
    }

    public float getPrice() {
        return this.price;
    }

    public Ingredient get(String name) {
        return valueOf(name.toUpperCase());
    }
}
