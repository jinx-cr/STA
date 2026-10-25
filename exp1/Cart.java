import java.util.ArrayList;
import java.util.List;

public class Cart {

    List<CartItem> items = new ArrayList<>();

    public void addToCart(Product product, int quantity) {

        if (quantity <= 0) {
            System.out.println("Quantity should be greater than zero.");
            return;
        }

        items.add(new CartItem(product, quantity));
        System.out.println(quantity + " x " + product.name + " added to cart.");
    }

    public void viewCart() {

        System.out.println("\nYour Cart:");

        if (items.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        double total = 0;

        for (CartItem item : items) {

            System.out.println(item.product.name + " x "
                    + item.quantity + " = $" + item.getTotalPrice());

            total += item.getTotalPrice();
        }

        System.out.println("---------------------------");
        System.out.println("Total: $" + total);
    }

    public void checkout() {

        if (items.isEmpty()) {
            System.out.println("Cart is empty. Nothing to checkout.");
            return;
        }

        double total = 0;

        for (CartItem item : items) {
            total += item.getTotalPrice();
        }

        System.out.println("Checkout complete.");
        System.out.println("Total amount paid: $" + total);

        items.clear();
    }
}