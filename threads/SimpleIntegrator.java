package threads;

import static functions.Functions.integral;

public class SimpleIntegrator implements Runnable {
    private Task task;

    public SimpleIntegrator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " запущен");
        int processed = 0;

        while (!task.isEnd() || processed < task.getTaskCount()) {
            if (task.getFunction() != null) {
                try {
                    double integralValue = integral(task.getFunction(),
                            task.getLeftX(), task.getRightX(), task.getStep());

                    System.out.println(Thread.currentThread().getName() + " Result " +
                            "<" + task.getLeftX() + "> " +
                            "<" + task.getRightX() + "> " +
                            "<" + task.getStep() + "> " +
                            "<" + integralValue + ">");

                    processed++;
                    task.setFunction(null);

                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName() + " Error: " + e.getMessage());
                    processed++;
                }
            }


            try {
                Thread.sleep(1); // Даем время Generator'у
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println(Thread.currentThread().getName() + " завершен. Обработано: " + processed);
    }
}