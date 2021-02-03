package com.atguigu.search.thread;

import java.util.concurrent.*;

/**
 * @ClassName ThreadTest
 * @Description 多线程
 * @Author lwq
 * @Date 2021/1/12 11:43
 * @Version 1.0
 */
public class ThreadTest {

    public static ThreadFactory threadFactory =  Executors.defaultThreadFactory();

   public static ExecutorService executorService = new ThreadPoolExecutor(10,15,
           1L, TimeUnit.SECONDS,
           new LinkedBlockingQueue<>(5),
           threadFactory,
           new ThreadPoolExecutor.AbortPolicy());


    /**
     * CompletableFuture 异步编排
     * @param args
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {


        System.out.println("main方法开始====");


       /* CompletableFuture.runAsync(()->{

            System.out.println("当前线程:::"+Thread.currentThread().getId());
            int i = 10/2;
            System.out.println("运行结果:::"+i);

        },executorService);*/

       /* CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {

            System.out.println("当前线程:::" + Thread.currentThread().getId());
            int i = 10 / 0;
            System.out.println("运行结果:::" + i);
            return i;

        }, executorService).whenCompleteAsync((result,exception)->{
            System.out.println("异步任务成功完成.....结果.."+result+"异常："+exception);
        }).exceptionally(throwable->{
            return 10;
        });;*/
//        Integer integer = integerCompletableFuture.get();
//        System.out.println(integer);

//        integerCompletableFuture.whenCompleteAsync((result,exception)->{
//            System.out.println("异步任务成功完成.....结果.."+result+"异常："+exception);
//        }).exceptionally(throwable->{
//            return 10;
//        });

        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {

            System.out.println("当前线程:::" + Thread.currentThread().getId());
            int i = 10 / 0;
            System.out.println("运行结果:::" + i);
            return i;

        }, executorService).whenCompleteAsync((result,exception)->{
            System.out.println("异步任务成功完成.....结果.."+result+"异常："+exception);
        }).handleAsync((t,u)->{
            System.out.println("t"+t);
            System.out.println("u"+u);

            return 1;
        });

        Integer integer = integerCompletableFuture.get();
        System.out.println("integer==="+integer);

        System.out.println("main方法结束====");
    }





    public static void test111(String[] args) throws ExecutionException, InterruptedException {
        //线程的四大应用方式

        System.out.println("main方法开始====");
        //1、继承Thread类
//        Thread thread1 = new Thread1();
//        //启动线程
//        thread1.start();
//

//        2、Runable接口
//        new Thread(new Runable1()).start();

        //3、Callable
//        FutureTask<Integer> futureTask = new FutureTask<>(new CallAble1());
//        new Thread(futureTask).start();
//        //get 等待线程执行完成 获取返回结果  堵塞等待
//        Integer integer = futureTask.get();
//        System.out.println("结果。。。。"+integer);

//        4、给线程池提交任务 以上3种可能导致资源耗尽 交给线程池执行
        //当前系统池子只有一两个 每个异步任务提交给池
        executorService.execute(new Runable1());

        //1、2、 没有返回值
        // 3、4有返回值

//        4、性能稳定 控制资源


        System.out.println("main方法结束====");


//        Executors.newCachedThreadPool();

//        Executors.newFixedThreadPool()

//        Executors.newScheduledThreadPool()

        Executors.newSingleThreadExecutor();

    }

    public static class Thread1 extends Thread{
        @Override
        public void run() {
            System.out.println("当前线程:::"+Thread.currentThread().getId());
            int i = 10/2;
            System.out.println("运行结果:::"+i);
        }
    }

    public static class Runable1 implements Runnable{

        @Override
        public void run() {
            System.out.println("当前线程:::"+Thread.currentThread().getId());
            int i = 10/2;
            System.out.println("运行结果:::"+i);
        }
    }

    public static class CallAble1 implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程:::"+Thread.currentThread().getId());
            int i = 10/2;
            Thread.sleep(3000);
            System.out.println("运行结果:::"+i);
            return i;
        }
    }




}
