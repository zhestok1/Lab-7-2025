package functions;

import functions.meta.*;

public class Functions {

    /**
     * Приватный конструктор класса Functions
     */
    private Functions()  {}

    /**
     * Статический метод создания объекта типа Shift
     * @param f функция
     * @param shiftX сдвиг по х
     * @param shiftY сдвиг по y
     * @return объект
     */
    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    /**
     * Статический метод создания объекта типа Scale
     * @param f функция
     * @param scaleX сдвиг по х
     * @param scaleY сдвиг по y
     * @return объект
     */
    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }

    /**
     * Статический метод создания объекта типа Power
     * @param f функция
     * @param power степень
     * @return объект
     */
    public static Function power(Function f, double power) {
        return new Power(f, power);
    }

    /**
     * Статический метод создания объекта типа Sum
     * @param f1 функция 1
     * @param f2 функция 2
     * @return объект
     */
    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }

    /**
     * Статический метод создания объекта типа Mult
     * @param f1 функция 1
     * @param f2 функция 2
     * @return объект
     */
    public static Function mult(Function f1, Function f2) {
        return new Mult(f1, f2);
    }

    /**
     * Статический метод создания объекта типа Composition
     * @param f1 функция 1
     * @param f2 функция 2
     * @return объект
     */
    public static Function composition(Function f1, Function f2) {
        return new Composition(f1, f2);
    }

    /**
     * Вычисление интеграла функции
     * @param f переданная функция
     * @param leftX левая граница
     * @param rightX правая граница
     * @param step шаг
     * @return значение интеграла
     */
    public static double integral(Function f, double leftX, double rightX, double step) {
        if (f.getLeftDomainBorder() > leftX) throw new IllegalArgumentException("Incorrect left border!");
        if (f.getRightDomainBorder() < rightX) throw new IllegalArgumentException("Incorrect right domain border!");
        if (leftX > rightX) throw new IllegalArgumentException("Incorrect function!");
        if (step <= 0) throw new IllegalArgumentException("Step must be greater than 0!");

        if (leftX == rightX)
            return 0;

        double integralValue = 0;
        double currentX = leftX;

        while(currentX < rightX) {
            double nextX = Math.min(currentX + step, rightX); // Отлавливаем промежуток последнего шага, чтобы не вылезти за границы
            double currentStep = nextX - currentX; // Текущий шаг

            double f1 = f.getFunctionValue(currentX);
            double f2 = f.getFunctionValue(nextX);
            integralValue += (f1 + f2) * currentStep / 2.0; // Вычисление интеграла при помощи площадей трапеции

            currentX = nextX;
        }

        return integralValue;

    }
}
