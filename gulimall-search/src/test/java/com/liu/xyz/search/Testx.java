package com.liu.xyz.search;

import lombok.SneakyThrows;

import java.util.concurrent.*;

/**
 * create liu 2022-10-25
 */
public class Testx {
    /**
     * 交给线程池执行 可执行Runnbable，Thread
     *
     * service.submit() 执行Callbae
     */

    //创建线程池
    static ExecutorService service = Executors.newFixedThreadPool(10);
    @SneakyThrows
    public static void main(String[] args) {
        //异步编排
        System.out.println("线程开启");
        /**
         * 线程串行化方法 带Async都是异步处理，开启新线程
         * thenRun ,不接收数据，不反会数据，只要上面的任务执行完成，就开始执行 thenRun，
         * thenAccept 接收返回数据 ,不返回数据
         * thenApply 接收返回数据 ，能返回数据
         *
         * 两个任务都要完成
         *  runAfterBoth 两个任务完成后，执行，不接收数据，不反会数据
         *  thenAcceptBoth 两个任务完成后，执行，接收数据，不反会数据
         *  thenCombine 两个任务完成后，执行，接收数据，反会数据
         *
         *  两个任务执行一个执行
         *  applyToEither：，获取它的返回值，处理任务并有新的返回值。
         *  acceptEither：，获取它的返回值，处理任务，没有新的返回值。
         *  runAfterEither：，不需要获取 future 的结果，处理任务，也没有返回值。
         *
         *  多任务组合
         *  allOf：等待所有任务完成
         *  anyOf：只要有一个任务完
         */
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前id" + Thread.currentThread().getId());
            System.out.println("1好线程");


            return 10+"";
        }, service);
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前id" + Thread.currentThread().getId());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("2好线程");
            return "hello";
        }, service);

        CompletableFuture<Object> future = CompletableFuture.anyOf(future1, future2);

        System.out.println(future.get());
        System.out.println(future1.get()+future2.get());


    }


    @SneakyThrows
    public void test(){

        /**
         *
         *           线程串行化方法 带Async都是异步处理，开启新线程
         *           thenRun ,不接收数据，不反会数据，只要上面的任务执行完成，就开始执行 thenRun，
         *           thenAccept 接收返回数据 ,不返回数据
         *           thenApply 接收返回数据 ，能返回数据
         *
         *           两个任务都要完成
         *            runAfterBoth 两个任务完成后，执行，不接收数据，不反会数据
         *            thenAcceptBoth 两个任务完成后，执行，接收数据，不反会数据
         *            thenCombine 两个任务完成后，执行，接收数据，反会数据
         *
         */

        CompletableFuture<String> xx = CompletableFuture.supplyAsync(() -> {
            System.out.println("xx");
            return 10;
        }).thenApply((result) -> {
            return "xx"+result;
        });

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前id" + Thread.currentThread().getId());
            System.out.println("1好线程");
            return 10;
        }, service);
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前id" + Thread.currentThread().getId());
            System.out.println("2好线程");
            return "hello";
        }, service);

        CompletableFuture<String> future = future1.thenCombine(future2, (result1, result2) -> {
            System.out.println("第一个的数据\t" + result1);
            System.out.println("第二个的数据\t" + result2);
            return result1 + result2;
        });

        System.out.println(future.get());
    }

    @SneakyThrows
    public void com(){
        CompletableFuture<Integer> future1=CompletableFuture.supplyAsync(()->{
            System.out.println("当前id"+Thread.currentThread().getId());
            int i=10/0;
            System.out.println(i);
            return i;
            /**
             * 不能数据经行处理
             * 参数1.返回值，参数2.异常值
             */
        },service).whenComplete((resulu,ex)->{
            System.out.println("结果是"+resulu+"异常是"+ex);
            //处理异常 能返回值
        }).exceptionally((ex)->{
            ex.printStackTrace();
            return 10;
            /**
             * 能够对数据经行处理 加工
             */
        }).handle((result,ex)->{
            if(result!=null){
                return result*2;
            }
            if(ex!=null){
                return 0;
            }
            return 0;
        });
        Integer integer = future1.get();
        System.out.println("获取返回值"+integer);
    }

    public void docs(){
        /**
         * 线程的创建方式
         *  1.通过继承Thread
         *        Thread1 thread1 = new Thread1();
         *         thread1.start();
         *         缺点 每个对象只能调用start一次,其它两个只需创建start
         *  2.实现Runnable
         *         Runnable1 runnable1 = new Runnable1();
         *         new Thread(runnable1).start();
         *
         *  3.实现Callble
         *         Callable1 callable1 = new Callable1();
         *         FutureTask<Integer> task = new FutureTask<>(callable1);
         *         new Thread(task).start();
         *         Integer integer = task.get();
         *  4.交给线程池执行
         *      创建线程池
         *         1.Executors
         *         2.手动创建
         *
         */
        System.out.println("当前线程开始");

        /**
         * 参数
         *  1.corePoolSize :当前线程池的活动线程数量 ，会一直存在
         *  2.maximumPoolSize: 线程池最大线程数
         *  3.keepAliveTime: 线程存活时间，释放maximumPoolSize-corePoolSize的线程
         *  4.unit: 存活时间单位
         *  5.workQueue: 阻塞队列，如果有很多线程任务就会存储在队列中，等corePoolSize中的线程取，
         *              如果超过队列长度了就开启线程(maximumPoolSize),
         *  6.threadFactory: 线程工厂
         *  7.handler: 如果队列满了,线程次中的线程都有在执行，在进来 我们可以指端策略拒绝执行任务
         */
        new ThreadPoolExecutor(100,
                200,3, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        //创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
        Executors.newFixedThreadPool(10);
        //创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
        Executors.newCachedThreadPool();
        //创建一个定长线程池，支持定时及周期性任务执行
        Executors.newScheduledThreadPool(10);
        //创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
        Executors.newSingleThreadExecutor();
        System.out.println("当期线程结束");
    }
    /**
     * 线程 创建方式1
     */
    public static class Thread1 extends Thread{
        @Override
        public void run() {
            System.out.println("当前线程"+Thread.currentThread().getId());

            int i=10/2;
            System.out.println(i);
        }
    }

    /**
     * 线程 创建方式2
     */
    public static class Runnable1 implements Runnable{

        @Override
        public void run() {

            System.out.println("当前线程"+Thread.currentThread().getId());

            int i=10/2;
            System.out.println(i);
        }
    }

    public static  class Callable1 implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {

            System.out.println("当前线程"+Thread.currentThread().getId());

            int i=10/2;
            System.out.println("xxxx");
            return i;

        }
    }


}
