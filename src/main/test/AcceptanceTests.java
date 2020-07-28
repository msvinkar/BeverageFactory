import model.Ingredient;
import model.MenuItem;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class AcceptanceTests {

    private OrderManager orderManager = new OrderManager();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void orderMustHaveAtLeastOneMenuItem_failsIfOrderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Null value found !");
        orderManager.getFinalPrice(null);
    }

    @Test
    public void orderMustHaveAtLeastOneMenuItem_failsIfOrderIsEmpty() {
        String order = "";
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Can not accept empty order !");
        orderManager.getFinalPrice(order);
    }

    @Test
    public void orderMustHaveAtLeastOneMenuItem_failsIfOrderHasNoItemPresent() {
        String order = "randomString";
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No expected menu item found in the order !");
        orderManager.getFinalPrice(order);
    }

    @Test
    public void orderMustHaveAtLeastOneMenuItem_passesIfAtLeastOneItemIsPresentInTheOrder() {
        String order = "CHAI";
        orderManager.getFinalPrice(order);
    }

    @Test
    public void orderCanExistWithoutExclusion() {
        String order = "COFFEE";
        orderManager.getFinalPrice(order);
    }

    @Test
    public void orderCannotHaveAllTheIngredientsExcludedForAMenuItem_showsInvalidOrderMessage() {
        String order = "Chai, -Tea, -Milk, -Sugar, -Water";
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Order can not be accepted as all of ingredients are excluded !");
        orderManager.getFinalPrice(order);
    }

    @Test
    public void whenOrderHasInvalidIngredientsInExclusions_showErrorMessage() {
        String order = "Chai, -Tea, -InvalidIngredient";
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Invalid ingredients found !");
        expectedException.expectMessage("InvalidIngredient".toUpperCase());
        orderManager.getFinalPrice(order);
    }

    @Test
    public void showMenuItemPriceWhenOrderDoesNotHaveAnyExclusions() {
        String order = "Banana Smoothie";
        float actualPrice = orderManager.getFinalPrice(order);
        assertEquals(MenuItem.BANANASMOOTHIE.getPrice(), actualPrice, 0);
    }

    @Test
    public void showSubtractedAmountFromOriginalMenuItemPriceWhenOrderHasExclusions() {
        String order = "Strawberry Shake, -sugar, -Water";
        float expectedPrice = MenuItem.STRAWBERRYSHAKE.getPrice() - Ingredient.SUGAR.getPrice() - Ingredient.WATER.getPrice();
        float actualPrice = orderManager.getFinalPrice(order);
        assertEquals(expectedPrice, actualPrice, 0);
    }

    @Test
    public void showMenuItemPricesWhenMultipleValidOrdersArePlacedAtOnce() {
        String[] orders = new String[]{"Strawberry Shake, -sugar, -Water", "Banana Smoothie"};
        float expectedPriceForStrawberryShake = MenuItem.STRAWBERRYSHAKE.getPrice() - Ingredient.SUGAR.getPrice() - Ingredient.WATER.getPrice();
        double expectedPrice = expectedPriceForStrawberryShake + MenuItem.BANANASMOOTHIE.getPrice();
        double actualPrice = orderManager.getTotalPrice(orders);
        assertEquals(expectedPrice, actualPrice, 0);
    }

    @Test
    public void showMenuItemPricesWhenMultipleOrdersArePlacedAtOnceButSomeOfThemAreInvalid() {
        String invalidOrder = "Chai, -Tea, -Milk, -Sugar, -Water";
        String[] order = new String[]{"Strawberry Shake, -sugar, -Water", "Banana Smoothie", invalidOrder};
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("One of the orders was invalid. ");
        expectedException.expectMessage("Order can not be accepted as all of ingredients are excluded !");
        orderManager.getTotalPrice(order);
    }

}
