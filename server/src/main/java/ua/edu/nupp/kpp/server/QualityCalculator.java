package ua.edu.nupp.kpp.server;

public class QualityCalculator {
    public static double normalizeRatio(int achieved, int total) {
        if (achieved < 0 || total <= 0) {
            throw new IllegalArgumentException("Некоректні числові дані для нормування фактора");
        }
        double percent = ((double) achieved / total) * 100.0;
        double clamped = Math.max(0.0, Math.min(100.0, percent));
        return Math.round(clamped * 100.0) / 100.0;
    }

    public static double calculate(double f1, double f2, double f3) {
        checkRange(f1);
        checkRange(f2);
        checkRange(f3);
        double index = 0.40 * f1 + 0.35 * f2 + 0.25 * f3;
        return Math.round(index * 100.0) / 100.0;
    }

    private static void checkRange(double value) {
        if (value < 0.0 || value > 100.0) {
            throw new IllegalArgumentException("Фактор інтегрального показника має бути в діапазоні 0..100");
        }
    }
}
