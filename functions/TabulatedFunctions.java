package functions;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class TabulatedFunctions {

    private static TabulatedFunctionFactory tff = new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();

    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory factory) {
        TabulatedFunctions.tff = factory;
    }

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) throws IllegalArgumentException {
        return tff.createTabulatedFunction(leftX, rightX, pointsCount);
    }
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) throws IllegalArgumentException {
        return tff.createTabulatedFunction(leftX, rightX, values);
    }
    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] massiveOfpoints) throws IllegalArgumentException {
        return tff.createTabulatedFunction(massiveOfpoints);
    }

    // Рефлексия

    /**
     * Создает табулированную функцию через рефлексию по границам и количеству точек
     * @param fClass класс, реализующий TabulatedFunction
     * @param leftX левая граница
     * @param rightX правая граница
     * @param pointsCount количество точек
     * @return созданная табулированная функция
     */
    public static TabulatedFunction createTabulatedFunction(Class<?> fClass, double leftX, double rightX, int pointsCount) {
        try {
            // isAssignableFrom() проверяет, можно ли привести functionClass к TabulatedFunction
            if (!TabulatedFunction.class.isAssignableFrom(fClass)) {
                throw new IllegalArgumentException("Class must be realize TabulatedFunction interface!");
            }

            // Ищем конструктор с параметрами (double, double, int)
            java.lang.reflect.Constructor<?> constructor = fClass.getConstructor(double.class, double.class, int.class);
            // Вызывает найденный конструктор с переданными аргументами
            return (TabulatedFunction) constructor.newInstance(leftX, rightX, pointsCount);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Constructor (double, double, int) not found in class: " + fClass.getName(), e);
        } catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            throw new IllegalArgumentException("Cannot create instance of class: " + fClass.getName(), e);
        }
    }

    /**
     * Создает табулированную функцию через рефлексию по массивам X и Y значений
     * @param fClass класс, реализующий TabulatedFunction
     * @param leftX массив X значений
     * @param rightX массив Y значений
     * @return созданная табулированная функция
     */
    public static TabulatedFunction createTabulatedFunction(Class<?> fClass, double leftX, double rightX, double[] values) {
        try {
            if (!TabulatedFunction.class.isAssignableFrom(fClass)) {
                throw new IllegalArgumentException("Class must be realize TabulatedFunction interface!");
            }

            // Ищем конструктор с параметрами (double, double, values)
            java.lang.reflect.Constructor<?> constructor = fClass.getConstructor(double.class, double.class, double[].class);
            // Вызывает найденный конструктор с переданными аргументами
            return (TabulatedFunction) constructor.newInstance(leftX, rightX, values);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Constructor (double, double, double[]) not found in class: " + fClass.getName(), e);
        } catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            throw new IllegalArgumentException("Cannot create instance of class: " + fClass.getName(), e);
        }
    }

    /**
     * Создает табулированную функцию через рефлексию по массиву точек
     * @param fClass класс, реализующий TabulatedFunction
     * @param points массив точек функции
     * @return созданная табулированная функция
     */
    public static TabulatedFunction createTabulatedFunction(Class<?> fClass, FunctionPoint[] points) {
        try {
            if (!TabulatedFunction.class.isAssignableFrom(fClass)) {
                throw new IllegalArgumentException("Class must be realize TabulatedFunction interface!");
            }

            // Ищем конструктор с параметрами (points)
            java.lang.reflect.Constructor<?> constructor = fClass.getConstructor(FunctionPoint[].class);
            // Вызывает найденный конструктор с переданными аргументами
            return (TabulatedFunction) constructor.newInstance((Object) points);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Constructor (FunctionPoint[]) not found in class: " + fClass.getName(), e);
        } catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            throw new IllegalArgumentException("Cannot create instance of class: " + fClass.getName(), e);
        }
    }

    public static TabulatedFunction tabulate(Class<?> fClass, Function function, double leftX, double rightX, int pointsCount) {
        // Проверка входных параметров
        if (function == null) {
            throw new IllegalArgumentException("Function cannot be null");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("LeftX cannot be greater or equal to rightX");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Points count must be greater than 1");
        }

        // Проверка, что отрезок табулирования принадлежит области определения
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Tabulation interval is outside function domain");
        }

        // Создание табулированной функции
        TabulatedFunction tabulatedFunc = createTabulatedFunction(fClass, leftX, rightX, pointsCount);

        // Заполнение значений функции
        for (int i = 0; i < pointsCount; i++) {
            double x = tabulatedFunc.getPointX(i);
            double y = function.getFunctionValue(x);
            tabulatedFunc.setPointY(i, y);
        }

        return tabulatedFunc;
    }

    public static TabulatedFunction inputTabulatedFunction(Class<?> fClass, InputStream in) throws IOException, InappropriateFunctionPointException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!TabulatedFunction.class.isAssignableFrom(fClass)) {
            throw new IllegalArgumentException("Class must be realize TabulatedFunction interface!");
        }
        DataInputStream data = new DataInputStream(in);

        FunctionPoint[] points;
        try {
            int pCount = data.readInt();
            points = new FunctionPoint[pCount];
            for (int i = 0; i < pCount; i++) {
                points[i] = new FunctionPoint(data.readDouble(), data.readDouble());
            }
        } catch (IOException e) {
            throw new IOException("Have a problem with your flow!", e);
        }
        java.lang.reflect.Constructor<?> constructor = fClass.getConstructor(FunctionPoint[].class);
        return (TabulatedFunction) constructor.newInstance((Object) points);
    }

    public static TabulatedFunction readTabulatedFunction(Class<?> fClass, Reader in) throws IOException, InappropriateFunctionPointException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);

        try {
            if (!TabulatedFunction.class.isAssignableFrom(fClass)) {
                throw new IllegalArgumentException("Class must be realize TabulatedFunction interface!");
            }

            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) { // TT_NUMBER - токен являктся числом
                throw new IOException("Expected points count number");
            }
            int pointsCount = (int) tokenizer.nval;

            FunctionPoint[] points = new FunctionPoint[pointsCount];
            for (int i = 0; i < pointsCount; i++) {
                // Читаем x
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Expected x coordinate at point " + i);
                }
                double x = tokenizer.nval;

                // Читаем y
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Expected y coordinate at point " + i);
                }
                double y = tokenizer.nval;

                points[i] = new FunctionPoint(x, y);
            }

            java.lang.reflect.Constructor<?> constructor = fClass.getConstructor(FunctionPoint[].class);
            return (TabulatedFunction) constructor.newInstance((Object) points);

        } catch (IOException e) {
            throw new IOException("Have a problem with your flow!", e);
        } catch (IllegalArgumentException | NoSuchMethodException  | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Приватный конструктор класса TabulatedFunctions
     */
    private TabulatedFunctions() {}

    /**
     * Табулирует функцию на заданном отрезке
     * @param function исходная функция
     * @param leftX левая граница табулирования
     * @param rightX правая граница табулирования
     * @param pointsCount количество точек
     * @return табулированная функция
     * @throws IllegalArgumentException если границы некорректны или выходят за область определения
     */
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        // Проверка входных параметров
        if (function == null) {
            throw new IllegalArgumentException("Function cannot be null");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("LeftX cannot be greater or equal to rightX");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Points count must be greater than 1");
        }

        // Проверка, что отрезок табулирования принадлежит области определения
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Tabulation interval is outside function domain");
        }

        // Создание табулированной функции
        TabulatedFunction tabulatedFunc = TabulatedFunctions.tff.createTabulatedFunction(leftX, rightX, pointsCount);

        // Заполнение значений функции
        for (int i = 0; i < pointsCount; i++) {
            double x = tabulatedFunc.getPointX(i);
            double y = function.getFunctionValue(x);
            tabulatedFunc.setPointY(i, y);
        }

        return tabulatedFunc;
    }

    /**
     * Записывает табулированную функцию
     * @param function функция
     * @param out поток
     * @throws IOException если есть проблемы с потоком
     */
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out)
    throws IOException {

        // Закрытие потоков это ответственность вызывающего кода
        DataOutputStream data = new DataOutputStream(out); // DataOutputStream - поток обёртка
        try {
            int pCount = function.getPointsCount();
            data.writeInt(pCount);

            for (int i = 0; i < pCount; i++) {
                double x = function.getPointX(i);
                double y = function.getPointY(i);
                data.writeDouble(x);
                data.writeDouble(y);
            }

            data.flush(); // сбрасываем буфер
        } catch (IOException e) {
            throw new IOException("Have a problem with your flow!", e); // Пробрасываем исключение
        }
    }

    /**
     * Записывает табулированную функцию
     * @param in поток
     * @return ArrayTabulatedFunction из точек
     * @throws IOException если проблемы с файлом
     */
    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException, InappropriateFunctionPointException
    {
        DataInputStream data = new DataInputStream(in);

        FunctionPoint[] points;
        try {
            int pCount = data.readInt();
            points = new FunctionPoint[pCount];
            for (int i = 0; i < pCount; i++) {
                points[i] = new FunctionPoint(data.readDouble(), data.readDouble());
            }
        } catch (IOException e) {
            throw new IOException("Have a problem with your flow!", e);
        }

        return new ArrayTabulatedFunction(points);

    }

    /**
     * Запись функции в поток
     * @param function функция
     * @param out Поток
     * @throws IOException при некорректных данных в потоке
     */
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out)
            throws IOException {

        BufferedWriter bfWriter = new BufferedWriter(out);

        try {
            int pCount = function.getPointsCount();

            // ПРАВИЛЬНАЯ запись числа как строки
            bfWriter.write(String.valueOf(pCount));
            bfWriter.write(" ");

            for (int i = 0; i < pCount; i++) {
                double x = function.getPointX(i);
                double y = function.getPointY(i);

                // Записываем каждое значение отдельно с пробелами
                bfWriter.write(String.valueOf(x));
                bfWriter.write(" ");
                bfWriter.write(String.valueOf(y));

                // Добавляем пробел между точками (кроме последней)
                if (i < pCount - 1) {
                    bfWriter.write(" ");
                }
            }

            bfWriter.flush();

        } catch (IOException e) {
            throw new IOException("Error writing tabulated function to writer", e);
        }
    }

    /**
     *
     * @param in Поток
     * @return Табулированную функцию
     * @throws IOException если проблемы с потоком
     */
    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException, InappropriateFunctionPointException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);

        try {
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) { // TT_NUMBER - токен являктся числом
                throw new IOException("Expected points count number");
            }
            int pointsCount = (int) tokenizer.nval;

            FunctionPoint[] points = new FunctionPoint[pointsCount];
            for (int i = 0; i < pointsCount; i++) {
                // Читаем x
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Expected x coordinate at point " + i);
                }
                double x = tokenizer.nval;

                // Читаем y
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Expected y coordinate at point " + i);
                }
                double y = tokenizer.nval;

                points[i] = new FunctionPoint(x, y);
            }

            return new ArrayTabulatedFunction(points);

        } catch (IOException e) {
            throw new IOException("Have a problem with your flow!", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }
}