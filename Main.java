import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String[] filePaths = {"path1.json", "path2.json"};

        for (String filePath : filePaths) {
            processFile(filePath);
        }
    }

    private static void processFile(String filePath) {
        String jsonString = readJsonFile(filePath);
        
        if (jsonString != null) {
            int n = Integer.parseInt(extractJsonValue(jsonString, "\"n\":", ","));
            int k = Integer.parseInt(extractJsonValue(jsonString, "\"k\":", "}"));

            System.out.println("Processing file: " + filePath);
            System.out.println("Keys: n = " + n + ", k = " + k);

            double[][] points = new double[n][2];

            for (int i = 1; i <= n; i++) {
                String key = Integer.toString(i);
                String base = extractJsonValue(jsonString, "\"" + key + "\":{\"base\":\"", "\"");
                String value = extractJsonValue(jsonString, "\"" + key + "\":{\"value\":\"", "\"");

                if (base != null && value != null) {
                    double x = Double.parseDouble(base);
                    double y = Double.parseDouble(value);
                    points[i - 1][0] = x;
                    points[i - 1][1] = y;
                    System.out.println("Key " + key + ": Point (" + x + ", " + y + ")");
                }
            }

            int degree = 2;
            double[] coefficients = findPolynomialCoefficients(points, degree);

            System.out.println("Polynomial coefficients:");
            for (double coefficient : coefficients) {
                System.out.print(coefficient + " ");
            }
            System.out.println();
        }
    }

    private static String readJsonFile(String filePath) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extractJsonValue(String jsonString, String key, String delimiter) {
        int start = jsonString.indexOf(key) + key.length();
        if (start == -1) return null;
        int end = jsonString.indexOf(delimiter, start);
        return jsonString.substring(start, end).trim();
    }

    public static double[] findPolynomialCoefficients(double[][] points, int degree) {
        int n = points.length;
        double[][] matrix = new double[degree + 1][degree + 1];
        double[] result = new double[degree + 1];

        for (int i = 0; i <= degree; i++) {
            for (int j = 0; j <= degree; j++) {
                matrix[i][j] = 0;
                for (int k = 0; k < n; k++) {
                    matrix[i][j] += Math.pow(points[k][0], i + j);
                }
            }

            result[i] = 0;
            for (int k = 0; k < n; k++) {
                result[i] += points[k][1] * Math.pow(points[k][0], i);
            }
        }

        return gaussianElimination(matrix, result);
    }

    public static double[] gaussianElimination(double[][] matrix, double[] result) {
        int n = result.length;

        for (int i = 0; i < n; i++) {
            int maxRow = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(matrix[j][i]) > Math.abs(matrix[maxRow][i])) {
                    maxRow = j;
                }
            }

            double[] temp = matrix[i];
            matrix[i] = matrix[maxRow];
            matrix[maxRow] = temp;
            double tempResult = result[i];
            result[i] = result[maxRow];
            result[maxRow] = tempResult;

            for (int j = i + 1; j < n; j++) {
                double factor = matrix[j][i] / matrix[i][i];
                for (int k = i; k < n; k++) {
                    matrix[j][k] -= factor * matrix[i][k];
                }
                result[j] -= factor * result[i];
            }
        }

        double[] solution = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            solution[i] = result[i] / matrix[i][i];
            for (int j = i - 1; j >= 0; j--) {
                result[j] -= matrix[j][i] * solution[i];
            }
        }

        return solution;
    }
}
