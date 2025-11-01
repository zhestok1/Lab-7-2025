import functions.*;
import functions.basic.Exp;
import functions.basic.Log;
import threads.*;


import java.io.*;
import java.util.concurrent.Semaphore;

import static functions.Functions.integral;

public class Main {
    public static void main(String[] args) throws InappropriateFunctionPointException, IOException {

        Function exp = new Exp();
        double theoryIntegral = Math.E - 1; // Значение интеграла от exp на [0,1]

        double[] steps = {0.1, 0.01, 0.001, 0.0001 };

        System.out.println("Шаг\t\tИнтеграл\t\tПогрешность");
        System.out.println("----------------------------------------");

        for (double step : steps) {
            double myIntegral = integral(exp, 0, 1, step);
            double error = Math.abs(myIntegral - theoryIntegral);

            System.out.printf("%.6f\t%.8f\t%.2e", step, myIntegral, error);

            if (error < 1e-7) {
                System.out.println("  <- Точность достигнута");
                break;
            } else {
                System.out.println();
            }
        }

        nonThread();
        simpleThread();
        complicatedThreads();

    }

    public static void nonThread() {
        System.out.println("------------NonThread Выполняется------------");

        Task task = new Task(100);
        for (int i = 0; i < task.getTaskCount(); i++) {
            double logBase = 1 + Math.random() * 9; // [1, 10)
            Function log = new Log(logBase);
            double leftX = Math.random() * 100;
            double rightX = 100 + Math.random() * 100;
            double step = Math.random();

            task.setFunction(log);
            task.setLeftX(leftX);
            task.setRightX(rightX);
            task.setStep(step);

            System.out.println("Source " + "<" + task.getLeftX() + "> " +
                    "<" + task.getRightX() + "> " + "<" + task.getStep() + "> ");

            try {
                double integralValue = integral(log, leftX, rightX, step);
                System.out.println("Result " + "<" + task.getLeftX() + "> " +
                        "<" + task.getRightX() + "> " + "<" + task.getStep() + "> " + "<" + integralValue + ">");
            } catch (Exception e) {
                System.out.println("Error!" + e.getMessage());
            }
            System.out.println();
        }
    }

    public static void simpleThread() {
        System.out.println("========== SimpleThreads Выполняется ==========");

        Task task = new Task(100);

        Thread generator = new Thread(new SimpleGenerator(task), "SimpleGenerator");
        Thread integrator = new Thread(new SimpleIntegrator(task), "SimpleIntegrator");

        generator.start();
        integrator.start();

        try {
            generator.join(100);
            integrator.join(100);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("========== SimpleThreads Завершен ==========");
    }

    public static void complicatedThreads() {
        System.out.println("========== ComplicatedThreads Выполняется ==========");

        Task task = new Task(100);
        Semaphore semaphore = new Semaphore(1);

        Generator generator = new Generator(task, semaphore);
        Integrator integrator = new Integrator(task, semaphore);

        // Установка приоритетов (экспериментируем)
        generator.setPriority(Thread.MAX_PRIORITY);     // Высший приоритет
        integrator.setPriority(Thread.NORM_PRIORITY);   // Обычный приоритет

        System.out.println("Приоритет Generator: " + generator.getPriority());
        System.out.println("Приоритет Integrator: " + integrator.getPriority());

        generator.start();
        integrator.start();

        // Ждем 50ms и прерываем потоки
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Прерываем потоки после 50ms...");
        generator.interrupt();
        integrator.interrupt();

        // Ждем завершения потоков после прерывания
        try {
            generator.join(100);
            integrator.join(100);
        } catch (InterruptedException e) {
            System.out.println("Основной поток был прерван");
        }

        // Проверяем, завершились ли потоки
        System.out.println("Generator состояние: " + (generator.isAlive() ? "ЖИВ" : "ЗАВЕРШЕН"));
        System.out.println("Integrator состояние: " + (integrator.isAlive() ? "ЖИВ" : "ЗАВЕРШЕН"));

        System.out.println("========== ComplicatedThreads Завершен ==========");
    }

}