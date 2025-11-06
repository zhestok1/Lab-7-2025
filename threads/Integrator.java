package threads;
import java.util.concurrent.Semaphore;
import functions.Functions;

public class Integrator extends Thread {
    private Task task;
    private Semaphore semaphore;

    public Integrator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < task.getTaskCount() && !isInterrupted(); i++) {
                //запрашиваем разрешение на чтение
                semaphore.acquire();

                //получаем параметры задания
                double left = task.getLeftX();
                double right = task.getRightX();
                double step = task.getStep();

                //вычисляем интеграл и выводим результат
                double result = Functions.integral(task.getFunction(), left, right, step);
                System.out.println(Thread.currentThread().getName() + " Result " +
                        "<" + task.getLeftX() + "> " +
                        "<" + task.getRightX() + "> " +
                        "<" + task.getStep() + "> " +
                        "<" + result + ">");

                //освобождаем семафор для записи
                semaphore.release();
            }
        }
        catch (InterruptedException e) {System.out.println("Integrator interrupted");}
        catch (Exception e) {System.out.println("Integration error: " + e.getMessage());}
    }
}