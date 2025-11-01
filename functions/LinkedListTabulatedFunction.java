package functions;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class LinkedListTabulatedFunction implements TabulatedFunction {


    /**
     * Вложенный класс элемента связного списка.
     * Представляет узел списка, содержащий точку функции и ссылки на соседние узлы.
     * Реализует инкапсуляцию через приватные поля с геттерами/сеттерами.
     */
    private static class FunctionNode {
        FunctionPoint point;
        FunctionNode next;
        FunctionNode prev;

        /**
         * Конструктор узла с заданной точкой функции
         * @param point точка функции для хранения в узле
         */
        FunctionNode(FunctionPoint point) {
            this.point = point;
        }

        /**
         * @param point устанавливает точку функции для узла
         */
        public void setPoint(FunctionPoint point) {
            this.point = point;
        }

        /**
         *@param next устанавливает ссылку на следующий узел
         */
        public void setNext(FunctionNode next) {
            this.next = next;
        }

        /**
         @param prev устанавливает ссылку на предыдущий узел
         */
        public void setPrev(FunctionNode prev) {
            this.prev = prev;
        }

        /**
         @return точку функции, хранящуюся в узле
         */
        public FunctionPoint getPoint() {
            return point;
        }

        /**
         @return ссылку на следующий узел в списке
         */
        public FunctionNode getNext() {
            return next;
        }

        /**
         @return ссылку на предыдущий узел в списке
         */
        public FunctionNode getPrev() {
            return prev;
        }
    }

    private transient FunctionNode head = new FunctionNode(null);

    {
        head.setPrev(head);
        head.setNext(head);
    }

    private int count;

    /**
     * Конструктор по умолчанию
     */
    public LinkedListTabulatedFunction() {}

    /**
     * Добавляет новый узел в конец списка.
     * Создает новый узел с заданной точкой и вставляет его перед головой (в циклическом списке).
     *
     * @param point точка функции для добавления
     * @return ссылка на созданный узел
     */
    FunctionNode addNodeToTail(FunctionPoint point) {
        FunctionNode newNode = new FunctionNode(point);

        FunctionNode tail = head.getPrev(); // текущий хвост

        // Вставляем newNode между tail и head
        newNode.setPrev(tail);
        newNode.setNext(head);
        tail.setNext(newNode);
        head.setPrev(newNode);

        count++;
        return newNode;
    }

    /**
     * Возвращает узел списка по указанному индексу.
     * Оптимизирует доступ к элементам: для элементов в первой половине списка
     * обход начинается с головы, для элементов во второй половине - с хвоста.
     * Нумерация элементов начинается с 0 (голова списка не учитывается).
     *
     * @param index индекс узла (от 0 до count-1)
     * @return ссылка на узел по указанному индексу
     * @throws FunctionPointIndexOutOfBoundsException если индекс вне диапазона [0, count-1]
     */
    FunctionNode getNodeByIndex(int index)
            throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= count) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        FunctionNode current;

        if (index < count / 2) {
            // Двигаемся от головы вперед
            current = head.getNext(); // начинаем с 1ого значащего элемента
            for(int i = 0; i < index; i++) {
                current = current.getNext();
            }
        }
        else {
            // Двигаемся от хвоста назад
            current = head.getPrev(); // начинаем с последнего элемента
            for(int i = count - 1; i > index; i--) {
                current = current.getPrev();
            }
        }

        return current;
    }

    /**
     * Добавляет новый узел в указанную позицию списка.
     * Вставляет узел перед элементом с указанным индексом.
     * Если индекс равен количеству элементов, узел добавляется в конец списка.
     *
     * @param index позиция для вставки (от 0 до count)
     * @param point точка функции для добавления
     * @return ссылка на созданный узел
     * @throws FunctionPointIndexOutOfBoundsException если индекс вне диапазона [0, count]
     */
    FunctionNode addNodeByIndex(int index, FunctionPoint point)
            throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index > count) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        if (index == count) {
            return addNodeToTail(point);
        }

        FunctionNode targetNode = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode(point);

        newNode.setPrev(targetNode.getPrev());
        newNode.setNext(targetNode);
        targetNode.getPrev().setNext(newNode);
        targetNode.setPrev(newNode);

        count++;
        return newNode;
    }

    /**
     * Удаляет узел из списка по указанному индексу.
     * Корректно обновляет связи соседних узлов после удаления.
     *
     * @param index удаляемого узла (от 0 до count-1)
     * @return ссылка на удаленный узел
     * @throws FunctionPointIndexOutOfBoundsException если индекс вне диапазона [0, count-1]
     */
    FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= count) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        FunctionNode targetNode = getNodeByIndex(index);

        targetNode.getPrev().setNext(targetNode.getNext());
        targetNode.getNext().setPrev(targetNode.getPrev());

        count--;
        return targetNode;
    }

    /**
     * @param leftX
     * @param rightX
     * @param pointsCount
     * @throws IllegalArgumentException
     */
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount)
            throws IllegalArgumentException {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Left border must be bigger than right");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Count of points must be greater than 2!");
        }

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            addNodeToTail(new FunctionPoint(x, 0));
        }
    }

    /**
     * @param leftX
     * @param rightX
     * @param values
     * @throws IllegalArgumentException
     */
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values)
            throws IllegalArgumentException {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Left border must be bigger than right");
        }
        if (values == null || values.length < 2) {
            throw new IllegalArgumentException("Count of points must be greater than 2!");
        }

        int pointsCount = values.length;
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            addNodeToTail(new FunctionPoint(x, values[i]));
        }
    }


    /**
     * @param points массив точек
     * @throws IllegalArgumentException исключение массива меньше 2
     * @throws InappropriateFunctionPointException исключение некорректно заданных значений точек в массиве
     */
    public LinkedListTabulatedFunction(FunctionPoint[] points)
            throws IllegalArgumentException, InappropriateFunctionPointException {
        if (points == null) throw new IllegalArgumentException("Points array cannot be null");
        if (points.length < 2) throw new IllegalArgumentException("Massive must be greater than 2!");

        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX()) {
                throw new InappropriateFunctionPointException();
            }
        }

        for (int i = 0; i < points.length; i++) {
            addNodeToTail(new FunctionPoint(points[i]));
        }
    }
    public double getLeftDomainBorder() {
        return head.getNext().getPoint().getX(); // Прямой доступ к первому элементу через голову списка
    }

    public double getRightDomainBorder() {
        return head.getPrev().getPoint().getX();
    }


    public double getFunctionValue(double x) {

        double leftBorder = getLeftDomainBorder();
        double rightBorder = getRightDomainBorder();

        // Если x вне области определения
        if (x < leftBorder || x > rightBorder) {
            return Double.NaN;
        }

        // Если x точно совпадает с одной из точек
        FunctionNode current = head.getNext();
        while (current != head) {
            if (Math.abs(current.getPoint().getX() - x) < 1e-10) {
                return current.getPoint().getY();
            }
            current = current.getNext();
        }

        // Линейная интерполяция между точками
        current = head.getNext();
        while (current != head && current.getNext() != head) {
            double x1 = current.getPoint().getX();
            double x2 = current.getNext().getPoint().getX();

            if (x >= x1 && x <= x2) {
                double y1 = current.getPoint().getY();
                double y2 = current.getNext().getPoint().getY();

                // Формула линейной интерполяции
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            current = current.getNext();
        }

        return Double.NaN;
    }

    public int getPointsCount() {
        return count;
    }

    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= count)
            throw new FunctionPointIndexOutOfBoundsException();
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.getPoint());
    }

    public void setPoint(int index, FunctionPoint point)
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        if (point == null) {
            throw new IllegalArgumentException("Not be == null!");
        }
        if (index < 0 || index >= count) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        if (count > 1) {
            if (index > 0) {
                double prevX = getNodeByIndex(index - 1).getPoint().getX();
                if (point.getX() <= prevX) {
                    throw new InappropriateFunctionPointException();
                }
            }
            if (index < count - 1) {
                double nextX = getNodeByIndex(index + 1).getPoint().getX();
                if (point.getX() >= nextX) {
                    throw new InappropriateFunctionPointException();
                }
            }
        }

        FunctionNode node = getNodeByIndex(index);
        node.setPoint(new FunctionPoint(point));
    }

    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= count)
            throw new FunctionPointIndexOutOfBoundsException();
        FunctionNode node = getNodeByIndex(index);
        return node.getPoint().getX();
    }

    public void setPointX(int index, double x)
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        if (index < 0 || index >= count) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        // Проверка упорядоченности
        if ((index > 0 && x <= getNodeByIndex(index - 1).getPoint().getX()) ||
                (index < count - 1 && x >= getNodeByIndex(index + 1).getPoint().getX())) {
            throw new InappropriateFunctionPointException();
        }

        getNodeByIndex(index).getPoint().setX(x);
    }

    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= count)
            throw new FunctionPointIndexOutOfBoundsException();
        FunctionNode node = getNodeByIndex(index);
        return node.getPoint().getY();
    }

    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= count) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        getNodeByIndex(index).getPoint().setY(y);
    }

    public void deletePoint(int index)
            throws FunctionPointIndexOutOfBoundsException, IllegalStateException{
        if (index < 0 || index >= count) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        if (count < 3) {
            throw new IllegalStateException("Cannot delete point: minimum 3 points required");
        }
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        if (point == null) {
            throw new IllegalArgumentException("Point cannot be null");
        }

        // Проверка на дупликат
        FunctionNode current = head.getNext();
        while (current != head) {
            if (Math.abs(current.getPoint().getX() - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException();
            }
            current = current.getNext();
        }

        // Поиск позиции для вставки с сохранением упорядоченности
        current = head.getNext();
        int insertIndex = 0;

        while (current != head && current.getPoint().getX() < point.getX()) {
            current = current.getNext();
            insertIndex++;
        }

        addNodeByIndex(insertIndex, point);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(count);

        // Сохраняем все точки в массивы
        double[] xValues = new double[count];
        double[] yValues = new double[count];

        FunctionNode current = head.getNext();
        for (int i = 0; i < count; i++) {
            xValues[i] = current.getPoint().getX();
            yValues[i] = current.getPoint().getY();
            current = current.getNext();
        }

        out.writeObject(xValues);
        out.writeObject(yValues);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // Восстанавливаем данные и перестраиваем связный список
        count = in.readInt();
        double[] xValues = (double[]) in.readObject();
        double[] yValues = (double[]) in.readObject();

        // Перестраиваем связный список
        head = new FunctionNode(null);
        head.setPrev(head);
        head.setNext(head);

        for (int i = 0; i < count; i++) {
            addNodeToTail(new FunctionPoint(xValues[i], yValues[i]));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < this.getPointsCount(); i++) {
            // Получаем точку из узла
            FunctionPoint point = this.getNodeByIndex(i).getPoint();
            sb.append(point.toString());
            if (i < this.getPointsCount() - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof TabulatedFunction)) return false;

        if (this.getPointsCount() != ((TabulatedFunction) obj).getPointsCount()) return false;

        if (obj instanceof LinkedListTabulatedFunction) {
            for (int i = 0; i < getPointsCount(); i++) {
                if (!this.getPoint(i).equals(((LinkedListTabulatedFunction) obj).getPoint(i)))
                    return false;
            }
        } else {
            for (int i = 0; i < this.getPointsCount(); i++) {
                if (!this.getPoint(i).equals(((TabulatedFunction) obj).getPoint(i)))
                    return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        // Может быть любым положительным числом кроме нуля, лучше всего взять небольшое простое число 11,13,17,...
        int hash = 17; // Простое число, которое помогает избегать коллизии
        FunctionNode curr = head.getNext();
        while (curr != head) {
            hash = 31 * hash + curr.getPoint().hashCode(); // Умножаем для распределения хэш-значений
            curr = curr.getNext(); // Распределение значений важно, так как при маленьких значениях можем получить одинаковый хэш
        }
        return hash;
    }

    @Override
    public Object clone() {
        FunctionPoint[] massOfPoints = new FunctionPoint[this.getPointsCount()];
        FunctionNode curr = head.getNext();

        for (int i = 0; curr != head; i++) {
            FunctionPoint original = curr.getPoint();
            massOfPoints[i] = new FunctionPoint(original.getX(), original.getY());
            curr = curr.getNext();
        }

        try {
            return new LinkedListTabulatedFunction(massOfPoints);
        } catch (InappropriateFunctionPointException e) {
            throw new RuntimeException("Clone failed", e);
        }
    }
}