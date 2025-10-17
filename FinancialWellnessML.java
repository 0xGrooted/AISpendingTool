import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;


// Simplified categories focused on spending analysis
// See the README 1.1 For explanation of categories and flexibility.

enum SpendingCategory {
    HOUSING_FIXED,       // Rent, Mortgage (Low Flexibility)
    UTILITIES_FIXED,     // Bills, Internet (Low Flexibility)
    TRANSPORT_ESSENTIAL, // Fuel, Bus/Train Pass (Medium Flexibility)
    GROCERIES_FOOD,      // Supermarket spending (Medium Flexibility)
    EATING_OUT_COFFEE,   // Restaurants, Fast Food, Cafes (High Flexibility)
    SUBSCRIPTIONS,       // Netflix, Hostinger (Medium Flexibility)
    SHOPPING_LUXURY,     // Vinted, Amazon, Luxury items (Highest Flexibility)
    DEBT_REPAYMENT,      // Credit Card payments (Low Flexibility)
    MISCELLANEOUS        // General small items
}

// Transaction Class (Money In or Money Out)
// 1.2 IN ReadMe.md

class Transaction {
    private double amount; // Positive for income, negative for expense
    private LocalDate date;
    private String description;
    private SpendingCategory category;

    public Transaction(double amount, LocalDate date, String description, SpendingCategory category) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category = category;
    }

    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public SpendingCategory getCategory() { return category; }

    @Override
    public String toString() {
        String sign = amount >= 0 ? "+" : "";
        return String.format("%s: %s£%.2f - %s [%s]",
                date, sign, Math.abs(amount), description, category);
    }
}

// Result cclass
// 1.3
class SavingsOpportunity {
    private SpendingCategory category;
    private double totalSpent;
    private long transactionCount;
    private double priorityScore;
    private String rationale;

    public SavingsOpportunity(SpendingCategory category, double totalSpent, long transactionCount, double score, String rationale) {
        this.category = category;
        this.totalSpent = totalSpent;
        this.transactionCount = transactionCount;
        this.priorityScore = score;
        this.rationale = rationale;
    }

    public SpendingCategory getCategory() { return category; }
    public double getTotalSpent() { return totalSpent; }
    public double getPriorityScore() { return priorityScore; }
    public long getTransactionCount() { return transactionCount; }
    public String getRationale() { return rationale; }
}


// --- WEIGHTED SCORING ANALYZER CLASS ---

/**
 * This class uses a simple scoring system to rate spending categories
 * based on how easy or important it is to save money in that area.
 *
 * It combines three factors: Discretion, Frequency, and Magnitude.
 * See the ReadMe for this in more depth.
 */

 //2.1
class WeightedScoringAnalyzer {
    // Fixed weights used in the discussed formula
    private static final double WEIGHT_FREQUENCY = 0.4; // How important frequency is (high)
    private static final double WEIGHT_MAGNITUDE = 0.3; // How important total amount spent is (medium)
    private static final double WEIGHT_DISCRETION = 0.5; // How important flexibility/choice is (highest)
    private static final double BASELINE_ADJUSTMENT = 0.1; // A small starting bonus for the score
    
    // Discretionary Factor 
    // 2.2
    private static final Map<SpendingCategory, Double> DISCRETIONARY_INPUT = new HashMap<>();
    
    static {
        // Higher number means it's more optional, then better for saving.
        DISCRETIONARY_INPUT.put(SpendingCategory.SHOPPING_LUXURY, 1.5); // Very optional
        DISCRETIONARY_INPUT.put(SpendingCategory.EATING_OUT_COFFEE, 1.3); // Highly optional
        DISCRETIONARY_INPUT.put(SpendingCategory.SUBSCRIPTIONS, 1.1);
        DISCRETIONARY_INPUT.put(SpendingCategory.GROCERIES_FOOD, 0.9);
        DISCRETIONARY_INPUT.put(SpendingCategory.TRANSPORT_ESSENTIAL, 0.7);
        DISCRETIONARY_INPUT.put(SpendingCategory.UTILITIES_FIXED, 0.5);
        DISCRETIONARY_INPUT.put(SpendingCategory.DEBT_REPAYMENT, 0.4);
        DISCRETIONARY_INPUT.put(SpendingCategory.HOUSING_FIXED, 0.3); // Not optional
        DISCRETIONARY_INPUT.put(SpendingCategory.MISCELLANEOUS, 0.8);
    }

    // 2.3
    public List<SavingsOpportunity> runAnalysis(List<Transaction> transactions) {
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  SIMPLE WEIGHTED SCORING SYSTEM - RUNTIME ANALYSIS");
        System.out.println("=".repeat(70));
        
        // Find all the negative transactions e.g I spent £15 a week on McDonalds a week :D

        List<Transaction> expenses = transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .collect(Collectors.toList());

        if (expenses.isEmpty()) {
            return Collections.emptyList();
        }

        // How many times did you spend in each category (Frequency)?
        Map<SpendingCategory, Long> categoryCounts = expenses.stream()
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.counting()));

        // How much money did you spend in total for each category (Magnitude)?
        Map<SpendingCategory, Double> categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
        
        // Find the maximum count and total spent so we can compare everything on a fair scale (Normalization)
        double maxCount = categoryCounts.values().stream().mapToLong(l -> l).max().orElse(1);
        double maxTotal = categoryTotals.values().stream().mapToDouble(d -> Math.abs(d)).max().orElse(1);

        System.out.println("\n[1] INPUT GATHERING: Feature Extraction and Scaling");
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("  Total Transactions Analyzed: %d expenses.\n", expenses.size());
        System.out.println("  We calculate three factors for each category:");
        System.out.println("    - Frequency (How often you spend)");
        System.out.println("    - Magnitude (How much you spend in total)");
        System.out.println("    - Discretion (How optional the spending is)");
        System.out.println("  Scaling: Frequency and Magnitude are scaled to a 0-1 range (Normalization):");
        System.out.printf("    - Max Frequency (Used for scaling): %.0f transactions\n", maxCount);
        System.out.printf("    - Max Magnitude (Used for scaling): £%.2f total spent\n", maxTotal);
        
        // exPLAIN SCoring to the user.
        
        System.out.println("\n[2] SCORING MECHANISM: Detailed Calculation Trace");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("  The Savings Priority Score (SPS) is calculated as a weighted sum:");
        System.out.println("  SPS = (Freq * 0.4) + (Mag * 0.3) + (Disc * 0.5) + 0.1");
        System.out.printf("  Weights: Freq=%.1f, Mag=%.1f, Disc=%.1f. Starting Value=%.1f\n", 
                           WEIGHT_FREQUENCY, WEIGHT_MAGNITUDE, WEIGHT_DISCRETION, BASELINE_ADJUSTMENT);
        System.out.println("\n  --- Feature Inputs Trace (Calculated Per Category) ---");
        System.out.printf(" %-25s | %-12s | %-12s | %-12s | %-8s\n", 
            "CATEGORY", "SCALED FREQ", "SCALED MAG", "DISCRETION", "SPS SCORE");
        System.out.println("-".repeat(70));

        // 2.4
        
        List<SavingsOpportunity> opportunities = new ArrayList<>();

        for (SpendingCategory category : categoryTotals.keySet()) {
            double total = Math.abs(categoryTotals.get(category));
            long count = categoryCounts.getOrDefault(category, 0L);
            double discretionaryInput = DISCRETIONARY_INPUT.getOrDefault(category, 0.5);

            // Scale features to a 0-1 range
            double normalizedFrequency = count / maxCount;
            double normalizedMagnitude = total / maxTotal;
            
            // Compute Weighted Sum (The core calculation)
            double priorityScore = (normalizedFrequency * WEIGHT_FREQUENCY) +
                                  (normalizedMagnitude * WEIGHT_MAGNITUDE) +
                                  (discretionaryInput * WEIGHT_DISCRETION) +
                                  BASELINE_ADJUSTMENT;
            
            // Print detailed trace for this category
            System.out.printf(" %-25s | %-12.4f | %-12.4f | %-12.2f | %-8.4f\n", 
                category, normalizedFrequency, normalizedMagnitude, discretionaryInput, priorityScore);
            
            String rationale = generateRationale(category, normalizedFrequency, discretionaryInput);

            opportunities.add(new SavingsOpportunity(category, total, count, priorityScore, rationale));
        }

        System.out.println("\n  --- Scoring Rationale ---");
        System.out.println("  The highest scores mean these areas are the easiest and most effective places to cut spending.");
        System.out.println("  This is because we give the most importance (weight) to **Discretion (0.5)** and **Frequency (0.4)**.");

        // Sort by the calculated score (descending)
        opportunities.sort(Comparator.comparingDouble(SavingsOpportunity::getPriorityScore).reversed());
        
        return opportunities;
    }

    private String generateRationale(SpendingCategory category, double normalizedFrequency, double discretionaryInput) {
        if (discretionaryInput >= 1.3 && normalizedFrequency > 0.3) {
            return "High Impact. Frequent, non-essential spending. Excellent target for immediate cuts.";
        }
        if (discretionaryInput >= 1.0 && normalizedFrequency < 0.3) {
            return "Good Potential. High discretionary weight, but fewer occurrences. Target the largest single expenses.";
        }
        if (discretionaryInput < 0.7) {
            return "Low Priority. This is a core, essential expense. Focus on refinancing or changing providers long-term.";
        }
        if (category == SpendingCategory.GROCERIES_FOOD && normalizedFrequency > 0.5) {
            return "High Frequency. Try reducing impulse buys at the supermarket and optimize bulk purchasing.";
        }
        return "Standard Review. Review this category for smaller, non-recurring leaks.";
    }
}



public class FinancialWellnessML {
    // We use a simple scoring system to analyze spending patterns.
    private static WeightedScoringAnalyzer analyzer = new WeightedScoringAnalyzer();

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║  My Bank Account spending over the past month :)  ║");
        System.out.println("║  Identifies Top Savings Opportunities             ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");

        // Load the massive sample data immediately
        List<Transaction> transactions = loadMassiveSampleData();

        // Run the ML analysis
        List<SavingsOpportunity> opportunities = analyzer.runAnalysis(transactions);

        System.out.println("\n[3] FINAL REPORT: Prioritized Savings Opportunities (SPS 0-1+ range)");
        System.out.println("----------------------------------------------------------------------");
        
        if (opportunities.isEmpty()) {
            System.out.println("No expenses found to analyze.");
        } else {
            System.out.printf(" %-4s | %-25s | %-10s | %-12s | %s\n", 
                "RANK", "CATEGORY", "SPENT (£)", "SCORE", "RATIONALE");
            System.out.println("-".repeat(70));
            
            int rank = 1;
            for (SavingsOpportunity op : opportunities) {
                System.out.printf(" %-4d | %-25s | %-10.2f | %-12.3f | %s\n", 
                    rank++, op.getCategory(), op.getTotalSpent(), op.getPriorityScore(), op.getRationale());
            }
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("ACTIONABLE INSIGHT: Focus on the top 3 ranked categories. These areas have");
        System.out.println("the best combination of being highly discretionary and frequently used.");
    }

    private static List<Transaction> loadMassiveSampleData() {
        System.out.println("\n✓ Loading 40+ simulated Nationwide transactions...");
        
        List<Transaction> transactions = new ArrayList<>();
        LocalDate baseDate = LocalDate.of(2025, Month.OCTOBER, 17); 

        transactions.add(new Transaction(4500.00, baseDate.minusDays(17), "MONTHLY PAYCHECK", SpendingCategory.HOUSING_FIXED));
        transactions.add(new Transaction(199.06, baseDate.minusDays(15), "Bank credit SOUTH EASTERN - HS", SpendingCategory.MISCELLANEOUS));
        transactions.add(new Transaction(600.00, baseDate.minusDays(6), "Bank credit Trading 212 Limited", SpendingCategory.MISCELLANEOUS));
        transactions.add(new Transaction(78.36, baseDate.minusDays(1), "Bank credit SOUTH EASTERN - HS", SpendingCategory.MISCELLANEOUS));

        transactions.add(new Transaction(-45.99, baseDate.minusDays(0), "Amazon", SpendingCategory.SHOPPING_LUXURY));
        transactions.add(new Transaction(-79.01, baseDate.minusDays(8), "KACEY NESBITT BUY SOMETHING NICE", SpendingCategory.SHOPPING_LUXURY));
        transactions.add(new Transaction(-35.77, baseDate.minusDays(10), "Vinted", SpendingCategory.SHOPPING_LUXURY));
        transactions.add(new Transaction(-18.55, baseDate.minusDays(9), "Vinted", SpendingCategory.SHOPPING_LUXURY));
        transactions.add(new Transaction(-8.94, baseDate.minusDays(10), "Vinted", SpendingCategory.SHOPPING_LUXURY));
        transactions.add(new Transaction(-6.67, baseDate.minusDays(9), "Vinted", SpendingCategory.SHOPPING_LUXURY));
        transactions.add(new Transaction(-9.10, baseDate.minusDays(4), "Vinted", SpendingCategory.SHOPPING_LUXURY));
        transactions.add(new Transaction(-11.99, baseDate.minusDays(20), "Omniplex Cinema", SpendingCategory.SHOPPING_LUXURY));
        transactions.add(new Transaction(-3.69, baseDate.minusDays(9), "WHSmith", SpendingCategory.SHOPPING_LUXURY));

        transactions.add(new Transaction(-15.90, baseDate.minusDays(0), "McDonald's", SpendingCategory.EATING_OUT_COFFEE));
        transactions.add(new Transaction(-15.40, baseDate.minusDays(7), "McDonald's", SpendingCategory.EATING_OUT_COFFEE));
        transactions.add(new Transaction(-15.90, baseDate.minusDays(11), "McDonald's", SpendingCategory.EATING_OUT_COFFEE));
        transactions.add(new Transaction(-15.40, baseDate.minusDays(26), "McDonald's", SpendingCategory.EATING_OUT_COFFEE));
        transactions.add(new Transaction(-11.52, baseDate.minusDays(9), "JD Wetherspoon", SpendingCategory.EATING_OUT_COFFEE));
        transactions.add(new Transaction(-7.70, baseDate.minusDays(9), "Costa Coffee", SpendingCategory.EATING_OUT_COFFEE));
        transactions.add(new Transaction(-10.70, baseDate.minusDays(11), "Johndorys.c John Dory", SpendingCategory.EATING_OUT_COFFEE));
        
        
        transactions.add(new Transaction(-3.85, baseDate.minusDays(0), "Tesco", SpendingCategory.GROCERIES_FOOD));
        transactions.add(new Transaction(-4.33, baseDate.minusDays(0), "ASDA STORES LTD 4290", SpendingCategory.GROCERIES_FOOD));
        transactions.add(new Transaction(-7.00, baseDate.minusDays(3), "Tesco", SpendingCategory.GROCERIES_FOOD));
        transactions.add(new Transaction(-6.35, baseDate.minusDays(6), "Tesco", SpendingCategory.GROCERIES_FOOD));
        transactions.add(new Transaction(-5.95, baseDate.minusDays(7), "Tesco", SpendingCategory.GROCERIES_FOOD));
        transactions.add(new Transaction(-11.35, baseDate.minusDays(10), "Tesco", SpendingCategory.GROCERIES_FOOD));
        transactions.add(new Transaction(-4.60, baseDate.minusDays(10), "Tesco", SpendingCategory.GROCERIES_FOOD));
        transactions.add(new Transaction(-6.35, baseDate.minusDays(13), "Tesco", SpendingCategory.GROCERIES_FOOD));
        transactions.add(new Transaction(-6.35, baseDate.minusDays(24), "Tesco", SpendingCategory.GROCERIES_FOOD));
        
        transactions.add(new Transaction(-2.50, baseDate.minusDays(0), "Translink", SpendingCategory.TRANSPORT_ESSENTIAL));
        transactions.add(new Transaction(-2.50, baseDate.minusDays(2), "Translink", SpendingCategory.TRANSPORT_ESSENTIAL));
        transactions.add(new Transaction(-2.50, baseDate.minusDays(4), "Translink", SpendingCategory.TRANSPORT_ESSENTIAL));
        transactions.add(new Transaction(-2.50, baseDate.minusDays(7), "Translink", SpendingCategory.TRANSPORT_ESSENTIAL));
        transactions.add(new Transaction(-2.50, baseDate.minusDays(10), "Translink", SpendingCategory.TRANSPORT_ESSENTIAL));
        transactions.add(new Transaction(-2.50, baseDate.minusDays(15), "Translink", SpendingCategory.TRANSPORT_ESSENTIAL));
        transactions.add(new Transaction(-2.50, baseDate.minusDays(24), "Translink", SpendingCategory.TRANSPORT_ESSENTIAL));
        transactions.add(new Transaction(-20.07, baseDate.minusDays(9), "Uber", SpendingCategory.TRANSPORT_ESSENTIAL));
        transactions.add(new Transaction(-8.00, baseDate.minusDays(10), "JAMES BUCKLE UBER", SpendingCategory.TRANSPORT_ESSENTIAL));


        transactions.add(new Transaction(-37.98, baseDate.minusDays(18), "Hostinger (Web Hosting)", SpendingCategory.SUBSCRIPTIONS));
        transactions.add(new Transaction(-432.22, baseDate.minusDays(0), "MEMBER CREDIT CARD", SpendingCategory.DEBT_REPAYMENT));


        transactions.add(new Transaction(-1200.00, baseDate.minusDays(2), "Rent Payment", SpendingCategory.HOUSING_FIXED));
        transactions.add(new Transaction(-85.00, baseDate.minusDays(10), "Electric Bill", SpendingCategory.UTILITIES_FIXED));
        transactions.add(new Transaction(-50.00, baseDate.minusDays(5), "Phone Bill", SpendingCategory.UTILITIES_FIXED));


        transactions.add(new Transaction(-14.84, baseDate.minusDays(23), "Hendersons Comber", SpendingCategory.MISCELLANEOUS));
        transactions.add(new Transaction(-7.10, baseDate.minusDays(18), "Hendersons Comber", SpendingCategory.MISCELLANEOUS));
        transactions.add(new Transaction(-43.36, baseDate.minusDays(15), "Hendersons Comber", SpendingCategory.MISCELLANEOUS));
        transactions.add(new Transaction(-2.75, baseDate.minusDays(9), "Broderick Vending", SpendingCategory.MISCELLANEOUS));
        transactions.add(new Transaction(-0.99, baseDate.minusDays(10), "Apple", SpendingCategory.MISCELLANEOUS));

        System.out.printf("  Total Transactions Loaded: %d\n", transactions.size());
        return transactions;
    }
}
