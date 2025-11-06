package threads;

import static functions.Functions.integral;
import functions.Function;
import functions.basic.Log;

public class SimpleGenerator implements Runnable {
    private Task task;

    public SimpleGenerator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " запущен");

        for (int i = 0; i < task.getTaskCount(); i++) {
            double logBase = 1 + Math.random() * 9;
            Function log = new Log(logBase);
            double leftX = Math.random() * 100;
            double rightX = 100 + Math.random() * 100;
            double step = Math.random();

            task.setFunction(log);
            task.setLeftX(leftX);
            task.setRightX(rightX);
            task.setStep(step);

            System.out.println(Thread.currentThread().getName() + " Source " +
                    "<" + task.getLeftX() + "> " +
                    "<" + task.getRightX() + "> " +
                    "<" + task.getStep() + "> ");


            try {
                Thread.sleep(10); // Даем время Integrator'у
            } catch (InterruptedException e) {
                break;
            }
        }
        task.setEnd(true);
        System.out.println(Thread.currentThread().getName() + " завершен");
    }
}