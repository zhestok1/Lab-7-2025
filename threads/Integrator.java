package threads;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Integrator extends Thread {
    private Task task;
    private Semaphore semaphore;

    public Integrator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        System.out.println(getName() + " запущен");
        int processed = 0;

        while (processed < task.getTaskCount() && !isInterrupted()) {
            try {
                // Используем tryAcquire с таймаутом
                if (semaphore.tryAcquire(100, TimeUnit.MILLISECONDS)) {
                    try {
                        // Проверяем прерывание после захвата семафора
                        if (isInterrupted()) {
                            break;
                        }

                        if (task.getFunction() != null) {
                            double integralValue = functions.Functions.integral(
                                    task.getFunction(),
                                    task.getLeftX(),
                                    task.getRightX(),
                                    task.getStep()
                            );

                            System.out.println(getName() + " Result " +
                                    "<" + task.getLeftX() + "> " +
                                    "<" + task.getRightX() + "> " +
                                    "<" + task.getStep() + "> " +
                                    "<" + integralValue + ">");

                            processed++;
                            task.setFunction(null);
                        }
                    } finally {
                        semaphore.release();
                    }
                } else {
                    // Проверяем прерывание
                    if (isInterrupted()) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                System.out.println(getName() + " был прерван");
                break;
            } catch (Exception e) {
                System.out.println(getName() + " Error: " + e.getMessage());
                processed++; // Увеличиваем счетчик даже при ошибке
            }
        }
        System.out.println(getName() + " завершен");
    }
}