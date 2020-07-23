import model.Ingredient;
import model.MenuItem;

import java.util.*;
import java.util.stream.Collectors;

public class OrderManager {

    private static final Map<MenuItem, List<Ingredient>> MENU_ITEM_VS_EXCLUDABLE_INGREDIENTS = new HashMap<>();

    static {
        List<Ingredient> milkSugarAndWater = Collections.unmodifiableList(Arrays.asList(Ingredient.MILK, Ingredient.SUGAR, Ingredient.WATER));
        MENU_ITEM_VS_EXCLUDABLE_INGREDIENTS.put(MenuItem.CHAI, milkSugarAndWater);
        MENU_ITEM_VS_EXCLUDABLE_INGREDIENTS.put(MenuItem.COFFEE, milkSugarAndWater);
        MENU_ITEM_VS_EXCLUDABLE_INGREDIENTS.put(MenuItem.BANANASMOOTHIE, milkSugarAndWater);
        MENU_ITEM_VS_EXCLUDABLE_INGREDIENTS.put(MenuItem.STRAWBERRYSHAKE, milkSugarAndWater);
        MENU_ITEM_VS_EXCLUDABLE_INGREDIENTS.put(MenuItem.MOHITO, Arrays.asList(Ingredient.SUGAR, Ingredient.WATER, Ingredient.SODA, Ingredient.MINT));
    }

    public float getFinalPrice(String order) {
        throwExceptionIfValueIsNull(order);
        throwExceptionIfOrderStringIsEmpty(order);
        String[] menuItemAndExclusions = getWithoutWhiteSpaces(order).split(",-");
        MenuItem menuItem = getMenuItem(menuItemAndExclusions[0]);
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

    private MenuItem getMenuItem(String menuItemAndExclusion) {
        try {
            return MenuItem.valueOf(menuItemAndExclusion.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No expected menu item found in the order !");
        }
    }

    private List<Ingredient> getExcludedIngredients(String[] menuItemWithExclusions) {
        try {
            return Arrays.stream(menuItemWithExclusions)
                    .skip(1)        // skips menu item
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
