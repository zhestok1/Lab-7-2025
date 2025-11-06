package threads;
import java.util.concurrent.Semaphore;

public class Generator extends Thread {
    private Task task;
    private Semaphore semaphore;

    public Generator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < task.getTaskCount() && !isInterrupted(); i++) {
                //запрашиваем разрешение на запись
                semaphore.acquire();

                //генерируем случайные параметры
                double base = 1 + Math.random() * 9; // от 1 до 10
                double left = Math.random() * 100;   // от 0 до 100
                double right = 100 + Math.random() * 100; // от 100 до 200
                double step = Math.random();         // от 0 до 1

                //устанавливаем параметры в задание
                task.setFunction(new functions.basic.Log(base));
                task.setLeftX(left);
                task.setRightX(right);
                task.setStep(step);

                System.out.println(Thread.currentThread().getName() + " Source " +
                        "<" + task.getLeftX() + "> " +
                        "<" + task.getRightX() + "> " +
                        "<" + task.getStep() + "> ");

                //освобождаем семафор для чтения
                semaphore.release();
            }
        } catch (InterruptedException e) {
            System.out.println("Generator interrupted");
        }
    }
}