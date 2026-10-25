import java.util.Scanner;

public class EcommerceApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Product[] products = {

                new Product(1, "TV", 20000.0),
                new Product(2, "MONITOR", 5000.0),
                new Product(3, "PEN", 10.0),
                new Product(5, "WATCH", 3000.0),
                new Product(6, "JOYSTICK", 100.0),
                new Product(7, "KEYBOARD", 200.0),
                new Product(8, "PS4", 150.0),
                new Product(9, "LAPTOP", 50.0),
                new Product(10, "MOUSE", 100.0),
                new Product(11, "USB", 29.0),
                new Product(12, "SSD", 40.0),
                new Product(13, "MOTHERBOARD", 220.0),
                new Product(14, "HARDDISK", 120.0),
                new Product(15, "FAN", 10.0)


        };

        Cart cart = new Cart();

        while (true) {

            System.out.println("\nWelcome to Simple eCommerce!");
            System.out.println("1. View Products");
            System.out.println("2. Add Product to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Checkout");
            System.out.println("5. Exit");

            System.out.print("Choose an option: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                sc.next();
                continue;
            }

            int choice = sc.nextInt();

            switch (choice) {

                case 1:

                    System.out.println("\nAvailable Products:");

                    for (Product p : products) {
                        System.out.println(p.id + ". " + p.name + " - $" + p.price);
                    }

                    break;

                case 2:

                    System.out.print("Enter Product ID to add to cart: ");

                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid Product ID.");
                        sc.next();
                        break;
                    }

                    int pid = sc.nextInt();

                    System.out.print("Enter quantity: ");

                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid quantity.");
                        sc.next();
                        break;
                    }

                    int qty = sc.nextInt();

                    Product selected = null;

                    for (Product p : products) {

                        if (p.id == pid) {
                            selected = p;
                            break;
                        }
                    }

                    if (selected != null) {

                        cart.addToCart(selected, qty);

                    } else {

                        System.out.println("Product not found.");
                    }

                    break;

                case 3:

                    cart.viewCart();

                    break;

                case 4:

                    cart.checkout();

                    break;

                case 5:

                    System.out.println("Thank you for shopping. Goodbye!");
                    sc.close();
                    System.exit(0);

                default:

                    System.out.println("Invalid choice, try again.");
            }
        }
    }
}