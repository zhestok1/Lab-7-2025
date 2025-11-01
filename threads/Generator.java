package threads;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Generator extends Thread {
    private Task task;
    private Semaphore semaphore;

    public Generator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        System.out.println(getName() + " запущен");

        for (int i = 0; i < task.getTaskCount() && !isInterrupted(); i++) {
            try {
                // Используем tryAcquire с таймаутом
                if (semaphore.tryAcquire(100, TimeUnit.MILLISECONDS)) {
                    try {
                        // Проверяем прерывание после захвата семафора
                        if (isInterrupted()) {
                            break;
                        }

                        double logBase = 1 + Math.random() * 9;
                        functions.Function log = new functions.basic.Log(logBase);
                        double leftX = Math.random() * 100;
                        double rightX = 100 + Math.random() * 100;
                        double step = Math.random();

                        task.setFunction(log);
                        task.setLeftX(leftX);
                        task.setRightX(rightX);
                        task.setStep(step);

                        System.out.println(getName() + " Source " +
                                "<" + task.getLeftX() + "> " +
                                "<" + task.getRightX() + "> " +
                                "<" + task.getStep() + "> ");

                    } finally {
                        semaphore.release();
                    }
                } else {
                    // Таймаут - проверяем прерывание
                    if (isInterrupted()) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                System.out.println(getName() + " был прерван");
                break;
            } catch (Exception e) {
                System.out.println(getName() + " Error: " + e.getMessage());
            }
        }
        System.out.println(getName() + " завершен");
    }
}