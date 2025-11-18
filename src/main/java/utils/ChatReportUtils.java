package utils;

import org.testng.Reporter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatReportUtils {

    private static final List<String[]> currentTestRows = new ArrayList<>();

    /**
     * Log a result for AI test (question, expected, actual, similarity, pass/fail)
     */
    public static void logResultHtml(String question, String expected, String actual, Double similarity, boolean isPass) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String status = isPass ? "PASS" : "FAIL";

        // Null safety + escape HTML
        question = question != null ? escapeHtml(question) : "";
        expected = expected != null ? escapeHtml(expected) : "";
        actual = actual != null ? escapeHtml(actual) : "";
        String similarityStr = similarity != null ? String.format("%.2f", similarity) : "";

        currentTestRows.add(new String[]{timestamp, question, expected, actual, similarityStr, status});
    }

    /**
     * Returns HTML table for ExtentReports / TestNG
     */
    public static String getHtmlTable() {
        if (currentTestRows.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1' cellspacing='0' cellpadding='5' style='width:100%; border-collapse:collapse; font-size:12px;'>");
        
        // Header
        sb.append("<tr style='background-color:#f2f2f2;'>")
          .append("<th>Timestamp</th>")
          .append("<th>Question</th>")
          .append("<th>Expected</th>")
          .append("<th>Actual</th>")
          .append("<th>Similarity</th>")
          .append("<th>Status</th>")
          .append("</tr>");

        // Data rows
        for (String[] row : currentTestRows) {
            String color = "PASS".equalsIgnoreCase(row[5]) ? "#d4edda" : "#f8d7da"; // green/red
            sb.append("<tr style='background-color:").append(color).append(";'>");

            for (String col : row) {
                col = col != null ? col.replace("\n", "<br>") : "";
                sb.append("<td style='border:1px solid #ccc; padding:5px; vertical-align:top;'>").append(col).append("</td>");
            }
            sb.append("</tr>");
        }

        sb.append("</table>");

        // Log for TestNG emailable report
        Reporter.log("<br><b>Test Result Table:</b><br>" + sb.toString() + "<br>", true);

        return sb.toString();
    }

    /**
     * Clear the table for the next test
     */
    public static void resetHtmlTable() {
        currentTestRows.clear();
    }

    /**
     * Escape HTML special characters
     */
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
