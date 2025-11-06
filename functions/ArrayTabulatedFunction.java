package functions;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public class ArrayTabulatedFunction implements TabulatedFunction {
    private FunctionPoint[] massiveOfPoints;
    private int amountOfElements;

    public ArrayTabulatedFunction() {}

    /**
     * Создает табличную функцию с равномерно распределенными точками
     * @param leftX левая граница области определения
     * @param rightX правая граница области определения
     * @param pointsCount количество точек
     */
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount)
            throws IllegalArgumentException {
        if (leftX >= rightX)
            throw new IllegalArgumentException("Left border must be less than right border!");
        if (pointsCount < 2)
            throw new IllegalArgumentException("Count of points must be at least 2!");

        this.amountOfElements = pointsCount;
        this.massiveOfPoints = new FunctionPoint[pointsCount];

        double distance = (rightX - leftX) / (amountOfElements - 1);
        for(int i = 0; i < amountOfElements; i++) {
            double x = leftX + i * distance;
            this.massiveOfPoints[i] = new FunctionPoint(x, 0);
        }
    }

    /**
     * Создает табличную функцию с равномерно распределенными точками и заданными значениями функции
     * @param leftX левая граница области определения
     * @param rightX правая граница области определения
     * @param values массив значений функции в точках
     */
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values)
            throws IllegalArgumentException {

        if (leftX >= rightX)
            throw new IllegalArgumentException("Left border must be less than right border!");
        if (values.length < 2)
            throw new IllegalArgumentException("Count of points must be at least 2!");

        this.amountOfElements = values.length;
        this.massiveOfPoints = new FunctionPoint[amountOfElements];

        double distance = (rightX - leftX) / (amountOfElements - 1);
        for(int i = 0; i < amountOfElements; i++) {
            double x = leftX + i * distance;
            this.massiveOfPoints[i] = new FunctionPoint(x, values[i]);
        }
    }

    /**
     * Создает табличную функцию с равномерно распределенными точками и заданными значениями функции
     * @param massiveOfPoints массив точек
     */
    public ArrayTabulatedFunction(FunctionPoint[] massiveOfPoints) throws IllegalArgumentException {
        this.amountOfElements = massiveOfPoints.length;
        if (amountOfElements < 2)
            throw new IllegalArgumentException("Massive length must be greater than 2!");

        for (int i = 1; i < amountOfElements; i++) {
            if (massiveOfPoints[i].getX() < massiveOfPoints[i-1].getX()) {
                throw new IllegalArgumentException("IllegalArgumentException");
            }
        }

        this.massiveOfPoints = new FunctionPoint[amountOfElements];
        for (int i = 0; i < amountOfElements; i++) {
            this.massiveOfPoints[i] = new FunctionPoint(massiveOfPoints[i]);
        }

    }

    /**
     * Возвращает левую границу области определения функции
     * @return значение левой границы области определения
     */
    public double getLeftDomainBorder() {
        return this.massiveOfPoints[0].getX();
    }

    /**
     * Возвращает правую границу области определения функции
     * @return значение правой границы области определения
     */
    public double getRightDomainBorder() {
        return this.massiveOfPoints[this.amountOfElements - 1].getX();
    }

    /**
     * Вычисляет значение функции в заданной точке с помощью линейной интерполяции
     * @param x координата, в которой вычисляется значение функции
     * @return значение функции в точке x или Double.NaN, если x вне области определения
     */
    public double getFunctionValue(double x) {
        // Поиск интервала, содержащего x
        for (int i = 0; i < amountOfElements - 1; i++) {
            double x1 = massiveOfPoints[i].getX();
            double x2 = massiveOfPoints[i + 1].getX();

            if (x >= x1 && x <= x2) {
                // Линейная интерполяция между точками i и i+1
                double y1 = massiveOfPoints[i].getY();
                double y2 = massiveOfPoints[i + 1].getY();

                // Уравнение прямой: y = y1 + (y2 - y1) * (x - x1) / (x2 - x1)
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }

        // Пограничный случай
        if (x == massiveOfPoints[amountOfElements - 1].getX()) {
            return massiveOfPoints[amountOfElements - 1].getY();
        }

        return Double.NaN;
    }

    /**
     * Возвращает количество точек в табличной функции
     * @return количество точек
     */
    public int getPointsCount() {
        return this.amountOfElements;
    }

    /**
     * Возвращает копию точки по указанному индексу
     * @param index индекс точки (от 0 до pointsCount-1)
     * @return копия объекта FunctionPoint
     */
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= this.amountOfElements)
            throw new FunctionPointIndexOutOfBoundsException();
        return new FunctionPoint(massiveOfPoints[index].getX(), massiveOfPoints[index].getY());
    }

    /**
     * Заменяет точку по указанному индексу на новую
     * @param index индекс заменяемой точки
     * @param point новая точка (не может быть null)
     */
    public void setPoint(int index, FunctionPoint point)
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        if (index < 0 || index >= amountOfElements || point == null)
            throw new FunctionPointIndexOutOfBoundsException();
        if ((index > 0 && point.getX() <= massiveOfPoints[index - 1].getX()) ||
                (index < amountOfElements - 1 && point.getX() >= massiveOfPoints[index + 1].getX()))
            throw new InappropriateFunctionPointException();
        massiveOfPoints[index] = new FunctionPoint(point.getX(), point.getY());
    }

    /**
     * Возвращает координату X точки по указанному индексу
     * @param index индекс точки
     * @return координата X точки
     */
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= this.amountOfElements)
            throw new FunctionPointIndexOutOfBoundsException();
        return this.massiveOfPoints[index].getX();
    }

    /**
     * Устанавливает новую координату X для точки по указанному индексу
     * @param index индекс точки
     * @param x новая координата X
     */
    public void setPointX(int index, double x)
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        if (index < 0 || index >= amountOfElements) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        if ((index > 0 && x <= massiveOfPoints[index - 1].getX()) ||
                (index < amountOfElements - 1 && x >= massiveOfPoints[index + 1].getX())) {
            throw new InappropriateFunctionPointException();
        }
        massiveOfPoints[index] = new FunctionPoint(x, massiveOfPoints[index].getY());
    }

    /**
     * Возвращает координату Y (значение функции) точки по указанному индексу
     * @param index индекс точки
     * @return координата Y точки
     */
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= this.amountOfElements)
            throw new FunctionPointIndexOutOfBoundsException();
        return this.massiveOfPoints[index].getY();
    }

    /**
     * Устанавливает новое значение функции (координату Y) для точки по указанному индексу
     * @param index индекс точки
     * @param y новое значение функции
     */
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= amountOfElements) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        massiveOfPoints[index] = new FunctionPoint(massiveOfPoints[index].getX(), y);
    }

    /**
     * Удаляет точку по индексу
     * @param index индекс удаляемой точки
     */
    public void deletePoint(int index)
            throws FunctionPointIndexOutOfBoundsException, IllegalStateException {
        if (index < 0 || index >= amountOfElements) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        if (amountOfElements < 3) {
            throw new IllegalStateException("Cannot delete point: minimum 3 points required");
        }

        // Сдвигаем все элементы после удаляемого влево
        for (int i = index; i < amountOfElements - 1; i++) {
            massiveOfPoints[i] = massiveOfPoints[i + 1];
        }
        massiveOfPoints[amountOfElements - 1] = null;
        amountOfElements--;
    }

    /**
     * Добавляет точку по индексу
     * @param point новая точка
     */
    public void addPoint(FunctionPoint point)
            throws InappropriateFunctionPointException, IllegalArgumentException {
        if (point == null) {
            throw new IllegalArgumentException("Point cannot be null");
        }

        // Увеличиваем массив при необходимости
        if (amountOfElements == massiveOfPoints.length) {
            FunctionPoint[] newPoints = new FunctionPoint[massiveOfPoints.length + 1];
            for (int i = 0; i < amountOfElements; i++) {
                newPoints[i] = massiveOfPoints[i];
            }
            massiveOfPoints = newPoints;
        }

        // Ищем место для вставки
        int pos = 0;
        while (pos < amountOfElements && massiveOfPoints[pos].getX() < point.getX()) {
            pos++;
        }

        // Проверка на корректность вставки
        if ((pos > 0 && Math.abs(massiveOfPoints[pos-1].getX() - point.getX()) < 1e-10) ||
                (pos < amountOfElements && Math.abs(massiveOfPoints[pos].getX() - point.getX()) < 1e-10)) {
            throw new InappropriateFunctionPointException();
        }

        // Сдвигаем элементы вправо
        for (int i = amountOfElements; i > pos; i--) {
            massiveOfPoints[i] = massiveOfPoints[i - 1];
        }

        // Вставляем новую точку
        massiveOfPoints[pos] = new FunctionPoint(point.getX(), point.getY());
        amountOfElements++;
    }


    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // Сериализуем только данные, а не всю структуру массива
        out.writeInt(amountOfElements);

        // Сохраняем координаты X и Y в отдельные массивы
        double[] xValues = new double[amountOfElements];
        double[] yValues = new double[amountOfElements];

        for (int i = 0; i < amountOfElements; i++) {
            xValues[i] = massiveOfPoints[i].getX();
            yValues[i] = massiveOfPoints[i].getY();
        }

        out.writeObject(xValues);
        out.writeObject(yValues);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // Восстанавливаем данные
        amountOfElements = in.readInt();
        double[] xValues = (double[]) in.readObject();
        double[] yValues = (double[]) in.readObject();

        // Восстанавливаем массив точек
        massiveOfPoints = new FunctionPoint[amountOfElements];
        for (int i = 0; i < amountOfElements; i++) {
            massiveOfPoints[i] = new FunctionPoint(xValues[i], yValues[i]);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(); // mutable class который позволяет работать со строками не создавая новые объекты
        sb.append("{");
        for (int i = 0; i < this.amountOfElements; i++) {
            sb.append(massiveOfPoints[i].toString());
            if (i < amountOfElements - 1) { // Не ставим запятую после последнего элемента
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString(); // Преобразовали наш стрингБилдер в строку
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof TabulatedFunction)) return false;

        // Общие проверки для любого TabulatedFunction
        if (this.getPointsCount() != ((TabulatedFunction) obj).getPointsCount()) return false;

        // Оптимизация для того же класса
        if (obj instanceof ArrayTabulatedFunction) {
            for (int i = 0; i < amountOfElements; i++) {
                if (!this.massiveOfPoints[i].equals(((ArrayTabulatedFunction) obj).massiveOfPoints[i]))
                    return false;
            }
        } else {
            // Общий случай для других реализаций TabulatedFunction
            for (int i = 0; i < this.getPointsCount(); i++) {
                if (!this.getPoint(i).equals(((TabulatedFunction) obj).getPoint(i)))
                    return false;
            }
        }
        return true;
    }


    @Override
    public int hashCode() {
        int hash = 17;
        for (int i = 0; i < amountOfElements; i++) {
            hash = 31 * hash + massiveOfPoints[i].hashCode(); // Копия джавовской реализации
        }
        return hash;
    }

    @Override
    public Object clone() {
        FunctionPoint[] clonedPoints = new FunctionPoint[this.amountOfElements];
        for (int i = 0; i < this.amountOfElements; i++) {
            clonedPoints[i] = (FunctionPoint) this.massiveOfPoints[i].clone();
        }
        try {
            return new ArrayTabulatedFunction(clonedPoints);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private int index;
            @Override
            public boolean hasNext() {
                return index < amountOfElements;
            }

            @Override
            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("NoSuchElement!");
                }
                return new FunctionPoint(massiveOfPoints[index++]);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("You cannot do remove operation!");
            }
        };
    }

    public static class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {

        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) throws IllegalArgumentException {
            return new ArrayTabulatedFunction(leftX, rightX, pointsCount);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) throws IllegalArgumentException {
            return new ArrayTabulatedFunction(leftX, rightX, values);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) throws IllegalArgumentException {
            return new ArrayTabulatedFunction(points);
        }
    }


}