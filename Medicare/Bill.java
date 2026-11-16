/**
 * Bill class represents the billing record for a patient.
 *
 * Demonstrates:
 * - Encapsulation: Private member variables with public getters/setters.
 * - Business logic encapsulation (calculating total amount).
 */
public class Bill {

    private int billId;
    private int patientId;
    private double consultationCharge;
    private double additionalCharge;
    private double totalAmount;

    // Optional field for displaying patient's name on the bill
    private String patientName;

    /**
     * Default constructor
     */
    public Bill() {
    }

    /**
     * Parameterized constructor
     */
    public Bill(int billId, int patientId, double consultationCharge, double additionalCharge) {
        this.billId = billId;
        this.patientId = patientId;
        this.consultationCharge = consultationCharge;
        this.additionalCharge = additionalCharge;
        this.totalAmount = consultationCharge + additionalCharge;
    }

    /**
     * Parameterized constructor with all fields including total and patient name
     */
    public Bill(int billId, int patientId, String patientName,
                double consultationCharge, double additionalCharge, double totalAmount) {
        this.billId = billId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.consultationCharge = consultationCharge;
        this.additionalCharge = additionalCharge;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public double getConsultationCharge() {
        return consultationCharge;
    }

    public void setConsultationCharge(double consultationCharge) {
        this.consultationCharge = consultationCharge;
        calculateTotal();
    }

    public double getAdditionalCharge() {
        return additionalCharge;
    }

    public void setAdditionalCharge(double additionalCharge) {
        this.additionalCharge = additionalCharge;
        calculateTotal();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * Calculates the total amount by adding consultation and additional charges.
     */
    public void calculateTotal() {
        this.totalAmount = this.consultationCharge + this.additionalCharge;
    }

    /**
     * Displays a clean formatted invoice in the terminal.
     */
    public void printInvoice() {
        System.out.println("==================================================");
        System.out.println("            MEDICARE HOSPITAL - INVOICE           ");
        System.out.println("==================================================");
        System.out.printf(" Bill ID             : #%d\n", billId);
        System.out.printf(" Patient ID          : #%d\n", patientId);
        if (patientName != null && !patientName.isEmpty()) {
            System.out.printf(" Patient Name        : %s\n", patientName);
        }
        System.out.println("--------------------------------------------------");
        System.out.printf(" Consultation Charge : Rs. %10.2f\n", consultationCharge);
        System.out.printf(" Additional Charges  : Rs. %10.2f\n", additionalCharge);
        System.out.println("--------------------------------------------------");
        System.out.printf(" TOTAL AMOUNT DUE    : Rs. %10.2f\n", totalAmount);
        System.out.println("==================================================");
    }

    /**
     * Displays a single row in a summary table.
     */
    public void displaySummary() {
        String pName = (patientName != null) ? patientName : "ID: " + patientId;
        System.out.printf("| %-6d | %-20s (ID:%-2d) | Rs. %-9.2f | Rs. %-9.2f | Rs. %-9.2f |\n",
                billId, pName, patientId, consultationCharge, additionalCharge, totalAmount);
    }
}
