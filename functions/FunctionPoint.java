package functions;

public class FunctionPoint {
    private double x;
    private double y;


    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }


    FunctionPoint(FunctionPoint point) {
        this.x = point.getX();
        this.y = point.getY();
    }

    FunctionPoint() {
        this.x = 0;
        this.y = 0;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + this.x + "; " + this.y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Возвращаем true, если у нас тот же объект (т.к. проверяем ссылки)
        if (obj == null || this.getClass() != obj.getClass()) return false; // Разные типы => разные объекты

        return Double.compare(this.x, ((FunctionPoint) obj).x) == 0 // compare возвращает нуль, если координаты равны
                && Double.compare(this.y, ((FunctionPoint) obj).y) == 0; // в противном случае вернёт другую константу

    }

    @Override
    public int hashCode() {
        return Double.hashCode(x) ^ Double.hashCode(y);
    }

    @Override
    public Object clone() {
        return new FunctionPoint(this.x, this.y); // Просто создаём объект
    }
}
