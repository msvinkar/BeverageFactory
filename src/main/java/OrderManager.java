import model.Ingredient;
import model.MenuItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static model.Ingredient.*;
import static model.MenuItem.*;

public class OrderManager {

    private static final Map<MenuItem, List<Ingredient>> MENU_ITEM_VS_EXCLUDABLE_INGREDIENTS = new HashMap<>();

    static {
        MENU_ITEM_VS_EXCLUDABLE_INGREDIENTS.put(CHAI, Arrays.asList(TEA, MILK, SUGAR, WATER));
        MENU_ITEM_VS_EXCLUDABLE_INGREDIENTS.put(MenuItem.COFFEE, Arrays.asList(Ingredient.COFFEE, MILK, SUGAR, WATER));
        MENU_ITEM_VS_EXCLUDABLE_INGREDIENTS.put(BANANASMOOTHIE, Arrays.asList(BANANA, MILK, SUGAR, WATER));
        MENU_ITEM_VS_EXCLUDABLE_INGREDIENTS.put(STRAWBERRYSHAKE, Arrays.asList(STRAWBERRY, MILK, SUGAR, WATER));
        MENU_ITEM_VS_EXCLUDABLE_INGREDIENTS.put(MOHITO, Arrays.asList(LEMON, SUGAR, WATER, SODA, MINT));
    }

    public double getTotalPrice(String[] orders) {
        try {
            return Arrays.stream(orders)
                    .mapToDouble(this::getFinalPrice)
                    .sum();
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalStateException("One of the orders was invalid. " + e.getMessage());
        }
    }

    public Float getFinalPrice(String order) {
        throwExceptionIfValueIsNull(order);
        throwExceptionIfOrderStringIsEmpty(order);
        String[] menuItemAndExclusions = getWithoutWhiteSpaces(order).split(",-");
        MenuItem menuItem = getMenuItem(menuItemAndExclusions);
        List<Ingredient> excludedIngredients = getExcludedIngredients(menuItemAndExclusions);
        throwExceptionIfAllTheIngredientsWereExcluded(menuItem, excludedIngredients);
        return calculatePrice(menuItem, excludedIngredients);
    }

    private void throwExceptionIfValueIsNull(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Null value found !");
        }
    }

    private void throwExceptionIfOrderStringIsEmpty(String order) {
        if (order.isEmpty()) {
            throw new IllegalArgumentException("Can not accept empty order !");
        }
    }

    private String getWithoutWhiteSpaces(String string) {
        return string.replace(" ", "");
    }

    private MenuItem getMenuItem(String[] menuItemAndExclusions) {
        try {
            return MenuItem.valueOf(menuItemAndExclusions[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No expected menu item found in the order !");
        }
    }

    private List<Ingredient> getExcludedIngredients(String[] menuItemAndExclusions) {
        try {
            String[] excludedIngredientNames = Arrays.copyOfRange(menuItemAndExclusions, 1, menuItemAndExclusions.length);
            return Arrays.stream(excludedIngredientNames)
                    .map(String::toUpperCase)
                    .map(Ingredient::valueOf)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid ingredients found ! " + e.getMessage());
        }
    }

    private boolean areAllIngredientsExcluded(MenuItem menuItem, List<Ingredient> excludedIngredients) {
        return excludedIngredients.containsAll(MENU_ITEM_VS_EXCLUDABLE_INGREDIENTS.get(menuItem));
    }

    private void throwExceptionIfAllTheIngredientsWereExcluded(MenuItem menuItem, List<Ingredient> excludedIngredients) {
        if (areAllIngredientsExcluded(menuItem, excludedIngredients)) {
            throw new IllegalStateException("Order can not be accepted as all of ingredients are excluded !");
        }
    }

    private float getAmountToReduce(List<Ingredient> excludedIngredients) {
        float amountToReduce = 0f;
        for (Ingredient ingredient : excludedIngredients) {
            amountToReduce = amountToReduce + ingredient.getPrice();
        }
        return amountToReduce;
    }

    private float calculatePrice(MenuItem menuItem, List<Ingredient> excludedIngredients) {
        if (excludedIngredients.isEmpty()) {
            return menuItem.getPrice();
        } else {
            return menuItem.getPrice() - getAmountToReduce(excludedIngredients);
        }
    }

}
