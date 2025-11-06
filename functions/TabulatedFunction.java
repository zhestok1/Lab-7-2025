package functions;


import java.io.Externalizable;

public interface TabulatedFunction extends Function, Cloneable, Externalizable, Iterable<FunctionPoint> {

    /**
     * Возвращает количество точек в табличной функции
     * @return количество точек
     */
    int getPointsCount();

    /**
     * Возвращает копию точки по указанному индексу
     * @param index индекс точки (от 0 до pointsCount-1)
     * @return копия объекта FunctionPoint
     */
    FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException;

    /**
     * Заменяет точку по указанному индексу на новую
     * @param index индекс заменяемой точки
     * @param point новая точка (не может быть null)
     */
    void setPoint(int index, FunctionPoint point)
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;

    /**
     * Возвращает координату X точки по указанному индексу
     * @param index индекс точки
     * @return координата X точки
     */
    double getPointX(int index) throws FunctionPointIndexOutOfBoundsException;

    /**
     * Устанавливает новую координату X для точки по указанному индексу
     * @param index индекс точки
     * @param x новая координата X
     */
    void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;

    /**
     * Возвращает координату Y (значение функции) точки по указанному индексу
     * @param index индекс точки
     * @return координата Y точки
     */
    double getPointY(int index) throws FunctionPointIndexOutOfBoundsException;

    /**
     * Устанавливает новое значение функции (координату Y) для точки по указанному индексу
     * @param index индекс точки
     * @param y новое значение функции
     */
    void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException;

    /**
     * Удаляет точку по индексу
     * @param index индекс удаляемой точки
     */
    void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException;

    /**
     * Добавляет точку по индексу
     * @param point новая точка
     */
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;

    /**
     * Клонирует объект
     * @return клон объекта
     */
    public Object clone();
}

