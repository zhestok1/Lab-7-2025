import functions.*;
import functions.basic.Cos;
import functions.basic.Exp;
import functions.basic.Log;
import functions.basic.Sin;
import threads.*;


import java.io.*;
import java.util.concurrent.Semaphore;

import static functions.Functions.integral;

public class Main {
    public static void main(String[] args) throws Exception {

//        Function exp = new Exp();
//        double theoryIntegral = Math.E - 1; // Значение интеграла от exp на [0,1]
//
//        double[] steps = {0.1, 0.01, 0.001, 0.0001 };
//
//        System.out.println("Шаг\t\tИнтеграл\t\tПогрешность");
//        System.out.println("----------------------------------------");
//
//        for (double step : steps) {
//            double myIntegral = integral(exp, 0, 1, step);
//            double error = Math.abs(myIntegral - theoryIntegral);
//
//            System.out.printf("%.6f\t%.8f\t%.2e", step, myIntegral, error);
//
//            if (error < 1e-7) {
//                System.out.println("  <- Нужный шаг");
//                break;
//            } else {
//                System.out.println();
//            }
//        }

//         nonThread();
//         complicatedThreads();
//         simpleThread();

        System.out.println("---------------The First Task---------------");
        TabulatedFunction f2 = new ArrayTabulatedFunction( 0, 10, 9);
        for (FunctionPoint p : f2) {
            System.out.println(p);
        }

        System.out.println("---------------The Second Task---------------");
        Function f1 = new Cos();
        TabulatedFunction tf;
        tf = TabulatedFunctions.tabulate(f1, 0, Math.PI, 11);
        System.out.println(tf.getClass());
        TabulatedFunctions.setTabulatedFunctionFactory(new
                LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f1, 0, Math.PI, 11);
        System.out.println(tf.getClass());
        TabulatedFunctions.setTabulatedFunctionFactory(new
                ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f1, 0, Math.PI, 11);
        System.out.println(tf.getClass());

        System.out.println("---------------The Third Task---------------");
        TabulatedFunction f;

        f = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, 3);
        System.out.println(f.getClass());
        System.out.println(f);

        f = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, new double[] {0, 10});
        System.out.println(f.getClass());
        System.out.println(f);

        f = TabulatedFunctions.createTabulatedFunction(
                LinkedListTabulatedFunction.class,
                new FunctionPoint[] {
                        new FunctionPoint(0, 0),
                        new FunctionPoint(10, 10)
                }
        );
        System.out.println(f.getClass());
        System.out.println(f);

        f = TabulatedFunctions.tabulate(
                LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 11);
        System.out.println(f.getClass());
        System.out.println(f);


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
    }

    public static void complicatedThreads() {
        System.out.println("----------complicatedThreads----------");

        //создаём общие объекты (задания и семафора) и устанавливаем количество заданий
        Task task = new Task(100);
        Semaphore semaphore = new Semaphore(1, true);

        //создаем потоки
        Generator generator = new Generator(task, semaphore);
        Integrator integrator = new Integrator(task, semaphore);

        //запускаем потоки
        generator.start();
        integrator.start();

        try {
            Thread.sleep(50);

            //прерываем потоки
            generator.interrupt();
            integrator.interrupt();

            //ждем завершения потоков
            generator.join();
            integrator.join();
        } catch (InterruptedException e) {System.out.println("Главный поток был прерван!");}
        System.out.println();
    }

}