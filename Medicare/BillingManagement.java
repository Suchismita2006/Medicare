import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * BillingManagement handles bill generation, calculation, and invoice viewing.
 *
 * Demonstrates:
 * - Business logic encapsulation (consultation + additional charges = total)
 * - JDBC PreparedStatement usage
 * - ArrayList for temporary storage of bills
 * - Formatted terminal reporting/invoice display
 */
public class BillingManagement {

    private Scanner scanner;

    public BillingManagement(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Billing Management Sub-Menu Loop
     */
    public void billingMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("=========================================");
            System.out.println("           BILLING MANAGEMENT            ");
            System.out.println("=========================================");
            System.out.println("1. Generate Bill");
            System.out.println("2. View All Bills");
            System.out.println("3. Search Bill by Patient ID");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    generateBill();
                    break;
                case "2":
                    viewAllBills();
                    break;
                case "3":
                    searchBillByPatientId();
                    break;
                case "4":
                    back = true;
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("[!] Invalid choice! Please enter a number between 1 and 4.");
            }
        }
    }

    /**
     * Generates a patient bill, calculates total, saves to database, and displays terminal invoice.
     */
    public void generateBill() {
        System.out.println("\n--- Generate Patient Bill ---");

        // 1. Verify Patient ID
        int patientId = readInt("Enter Patient ID: ", 1, Integer.MAX_VALUE);
        Patient patient = PatientManagement.getPatientById(patientId);
        if (patient == null) {
            System.out.println("[!] Patient with ID " + patientId + " does not exist.");
            return;
        }

        System.out.println("-> Billing for Patient: " + patient.getName() + " (Disease: " + patient.getDisease() + ")");

        // 2. Consultation Charges
        double consultationCharge = readDouble("Enter Consultation Charges (Rs.): ", 0.0, 1000000.0);

        // 3. Additional Charges (Medicines, Tests, Nursing, etc.)
        double additionalCharge = readDouble("Enter Additional Charges (Medicines/Tests/Room) (Rs.): ", 0.0, 1000000.0);

        // 4. Calculate Total using Bill object logic
        Bill bill = new Bill(0, patientId, consultationCharge, additionalCharge);
        bill.setPatientName(patient.getName());

        String sql = "INSERT INTO bills (patient_id, consultation_charge, additional_charge, total_amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, patientId);
            pstmt.setDouble(2, bill.getConsultationCharge());
            pstmt.setDouble(3, bill.getAdditionalCharge());
            pstmt.setDouble(4, bill.getTotalAmount());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        bill.setBillId(rs.getInt(1));
                    }
                }
                System.out.println("\n[✓] Bill generated and saved successfully!\n");
                // Print beautiful terminal invoice
                bill.printInvoice();
            } else {
                System.out.println("\n[!] Failed to save bill.");
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error during bill generation: " + e.getMessage());
        }
    }

    /**
     * Views all bills generated in the hospital system.
     */
    public void viewAllBills() {
        System.out.println("\n--- All Hospital Bills ---");

        String sql = "SELECT b.bill_id, b.patient_id, p.name AS patient_name, " +
                "b.consultation_charge, b.additional_charge, b.total_amount " +
                "FROM bills b " +
                "JOIN patients p ON b.patient_id = p.patient_id " +
                "ORDER BY b.bill_id ASC";

        ArrayList<Bill> billList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Bill bill = new Bill(
                        rs.getInt("bill_id"),
                        rs.getInt("patient_id"),
                        rs.getString("patient_name"),
                        rs.getDouble("consultation_charge"),
                        rs.getDouble("additional_charge"),
                        rs.getDouble("total_amount")
                );
                billList.add(bill);
            }

            if (billList.isEmpty()) {
                System.out.println("No billing records found in the database.");
                return;
            }

            printBillTableHeader();
            double grandTotal = 0.0;
            for (Bill b : billList) {
                b.displaySummary();
                grandTotal += b.getTotalAmount();
            }
            printBillTableFooter();
            System.out.printf("Total Invoices: %d | Grand Revenue: Rs. %.2f\n", billList.size(), grandTotal);

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error while retrieving bills: " + e.getMessage());
        }
    }

    /**
     * Searches and displays bills for a specific patient ID.
     */
    public void searchBillByPatientId() {
        System.out.println("\n--- Search Bills by Patient ID ---");
        int patientId = readInt("Enter Patient ID to search bills: ", 1, Integer.MAX_VALUE);

        Patient patient = PatientManagement.getPatientById(patientId);
        if (patient == null) {
            System.out.println("[!] Patient with ID " + patientId + " does not exist.");
            return;
        }

        String sql = "SELECT b.bill_id, b.patient_id, p.name AS patient_name, " +
                "b.consultation_charge, b.additional_charge, b.total_amount " +
                "FROM bills b " +
                "JOIN patients p ON b.patient_id = p.patient_id " +
                "WHERE b.patient_id = ? " +
                "ORDER BY b.bill_id ASC";

        ArrayList<Bill> patientBills = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bill bill = new Bill(
                            rs.getInt("bill_id"),
                            rs.getInt("patient_id"),
                            rs.getString("patient_name"),
                            rs.getDouble("consultation_charge"),
                            rs.getDouble("additional_charge"),
                            rs.getDouble("total_amount")
                    );
                    patientBills.add(bill);
                }
            }

            if (patientBills.isEmpty()) {
                System.out.println("\n[!] No bills found for patient: " + patient.getName() + " (ID: " + patientId + ")");
                return;
            }

            System.out.println("\n[✓] Found " + patientBills.size() + " bill(s) for " + patient.getName() + ":");
            for (Bill b : patientBills) {
                b.printInvoice();
                System.out.println();
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error during bill search: " + e.getMessage());
        }
    }

    // Table printing helpers

    private void printBillTableHeader() {
        System.out.println("+--------+----------------------------+---------------+---------------+---------------+");
        System.out.println("| Bill ID| Patient (ID)               | Consult Fee   | Addl Charges  | Total Amount  |");
        System.out.println("+--------+----------------------------+---------------+---------------+---------------+");
    }

    private void printBillTableFooter() {
        System.out.println("+--------+----------------------------+---------------+---------------+---------------+");
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("[!] Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input! Please enter a valid integer.");
            }
        }
    }

    private double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("[!] Please enter a value between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input! Please enter a valid decimal number.");
            }
        }
    }
}
