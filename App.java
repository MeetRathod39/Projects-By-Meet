import java.sql.*;
import java.util.*;

public class App {
    private static Connection conn;
    public class EcommerceDataStructures {
        // HashMap for storing user data in memory
        private static HashMap<String, String> userCredentials = new HashMap<>();
        private static HashMap<Integer, String> products = new HashMap<>();
        private static ArrayList<String> cart = new ArrayList<>();
        private static Queue<String> orderQueue = new LinkedList<>();
        
        // Example of adding products to HashMap
        static {
            products.put(1, "Laptop - $1000");
            products.put(2, "Smartphone - $500");
            products.put(3, "Tablet - $300");
        }
    
        public static HashMap<String, String> getUserCredentials() {
            return userCredentials;
        }
    
        public static HashMap<Integer, String> getProducts() {
            return products;
        }
    
        public static ArrayList<String> getCart() {
            return cart;
        }
    
        public static Queue<String> getOrderQueue() {
            return orderQueue;
        }
    }

    public static void main(String[] args) {
        try {
            conn = DBConnection.getConnection();
            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            while (running) {
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Browse Products");
                System.out.println("4. Add to Cart");
                System.out.println("5. View Cart");
                System.out.println("6. Place Order");
                System.out.println("7. View Order History");
                System.out.println("8. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> register(scanner);
                    case 2 -> login(scanner);
                    case 3 -> browseProducts();
                    case 4 -> addToCart(scanner);
                    case 5 -> viewCart();
                    case 6 -> placeOrder();
                    case 7 -> viewOrderHistory();
                    case 8 -> {
                        running = false;
                        System.out.println("Exiting the system. Goodbye!");
                    }
                    default -> System.out.println("Invalid choice, please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void register(Scanner scanner) throws SQLException {
        System.out.print("Enter Name: ");
        String name = scanner.next();
        System.out.print("Enter Email: ");
        String email = scanner.next();
        System.out.print("Enter Password: ");
        String password = scanner.next();

        // Add user credentials to the HashMap
        EcommerceDataStructures.getUserCredentials().put(email, password);

        // Insert user details into the database
        String sql = "INSERT INTO Users (Name, Email, Password) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setString(2, email);
        stmt.setString(3, password);
        stmt.executeUpdate();
        System.out.println("Registration successful!");
    }

    private static void login(Scanner scanner) {
        System.out.print("Enter Email: ");
        String email = scanner.next();
        System.out.print("Enter Password: ");
        String password = scanner.next();

        if (EcommerceDataStructures.getUserCredentials().containsKey(email) &&
            EcommerceDataStructures.getUserCredentials().get(email).equals(password)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials, please try again.");
        }
    }

    private static void browseProducts() throws SQLException {
        HashMap<Integer, String> products = EcommerceDataStructures.getProducts();

        for (int id : products.keySet()) {
            System.out.println(id + ": " + products.get(id));
        }
    }

    private static void addToCart(Scanner scanner) {
        System.out.print("Enter Product ID to add to cart: ");
        int productId = scanner.nextInt();

        HashMap<Integer, String> products = EcommerceDataStructures.getProducts();

        if (products.containsKey(productId)) {
            EcommerceDataStructures.getCart().add(products.get(productId));
            System.out.println("Product added to cart!");
        } else {
            System.out.println("Product ID not found.");
        }
    }

    private static void viewCart() {
        ArrayList<String> cart = EcommerceDataStructures.getCart();

        for (String item : cart) {
            System.out.println(item);
        }
    }

    private static void placeOrder() throws SQLException {
        ArrayList<String> cart = EcommerceDataStructures.getCart();

        if (!cart.isEmpty()) {
            for (String item : cart) {
                // Add the item to the order queue
                EcommerceDataStructures.getOrderQueue().add(item);

                // Insert the order into the database (assuming userID = 1 for simplicity)
                String sql = "INSERT INTO Orders (UserID, ProductID, Quantity, OrderDate) VALUES (1, ?, 1, NOW())";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, EcommerceDataStructures.getProducts().keySet().stream()
                        .filter(key -> EcommerceDataStructures.getProducts().get(key).equals(item))
                        .findFirst().orElse(0));
                stmt.executeUpdate();
            }

            cart.clear();
            System.out.println("Order placed successfully!");
        } else {
            System.out.println("Your cart is empty.");
        }
    }

    private static void viewOrderHistory() throws SQLException {
        String sql = "SELECT * FROM Orders INNER JOIN Products ON Orders.ProductID = Products.ProductID WHERE UserID = 1";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            System.out.println(rs.getInt("OrderID") + ": " + rs.getString("Name") +
                    " - Quantity: " + rs.getInt("Quantity") + " - Date: " + rs.getDate("OrderDate"));
        }
    }
}