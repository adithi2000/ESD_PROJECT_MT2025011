/**
 * Formats a numeric value into a localized Indian Rupee (INR) currency string.
 * This ensures the output includes the ₹ symbol and follows standard Indian formatting 
 * (e.g., using lakhs/crores grouping, or standard international grouping depending 
 * on the environment's specific implementation of 'en-IN').
 * * @param amount The number to be formatted (e.g., 149500).
 * @returns The formatted string (e.g., "₹1,49,500.00").
 */
const formatCurrency = (amount: number): string => {
    // Check if the amount is valid; return a placeholder if not.
    if (amount === null || isNaN(amount)) {
        return "₹0.00";
    }

    // Use Intl.NumberFormat for robust, localized currency formatting.
    // 'en-IN' ensures the currency symbol (INR) and the optional local grouping.
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        // Optional: Ensure two decimal places for currency, even if 0.
        minimumFractionDigits: 2, 
        maximumFractionDigits: 2, 
    }).format(amount);
};

// Example usage of how you would integrate this into your table row:
/*
<td>{formatCurrency(record.grossSalary)}</td>
<td className="text-danger">({formatCurrency(record.deductions)})</td>
*/

export default formatCurrency;