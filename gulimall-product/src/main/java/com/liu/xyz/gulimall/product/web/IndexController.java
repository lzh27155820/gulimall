package com.liu.xyz.gulimall.product.web;

import cn.hutool.core.lang.UUID;
import com.liu.xyz.gulimall.product.entity.CategoryEntity;
import com.liu.xyz.gulimall.product.service.CategoryService;
import com.liu.xyz.gulimall.product.web.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * create liu 2022-10-15
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedissonClient redissonClient;
    @GetMapping({"/","/index.html"})
    public String index(Model model){

        //自动拼前后缀

       List<CategoryEntity> categorys =categoryService.getLevelCategorys();
       model.addAttribute("categorys",categorys);
       return "index";
    }

    @GetMapping(value = "/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();

        return catalogJson;

    }

    @GetMapping("hello")
    @ResponseBody
    public String hello(){
        /**
         *  redisson的Lock锁
         *      1.就算不是释放锁，默认也会释放    默认时间30s 可设置
         *      2.它的锁默认式分布式的
         *      3.自动增加时常 他会在默认给锁上新
         */
        RLock ayx = redissonClient.getLock("ayx");

        ayx.lock();
        //1、获取一把锁，只要锁的名字一样，就是同一把锁
        //  RLock myLock = redisson.getLock("my-lock");
        //2、加锁
        //myLock.lock();      //阻塞式等待。默认加的锁都是30s
        //1）、锁的自动续期，如果业务超长，运行期间自动锁上新的30s。不用担心业务时间长，锁自动过期被删掉
        //2）、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认会在30s内自动过期，不会产生死锁问题
        // myLock.lock(10,TimeUnit.SECONDS);   //10秒钟自动解锁,自动解锁时间一定要大于业务执行时间
        //问题：在锁时间到了以后，不会自动续期
        //1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是 我们制定的时间
        //2、如果我们指定锁的超时时间，就使用 lockWatchdogTimeout = 30 * 1000 【看门狗默认时间】
        //只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】,每隔10秒都会自动的再次续期，续成30秒
        // internalLockLeaseTime 【看门狗时间】 / 3， 10s
            try {
                System.out.println("当前在运行"+ Thread.currentThread().getId());
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                System.out.println("解锁");
                ayx.unlock();
            }

        return "hello";
    }

    /**
     * 保证一定能读到最新数据，修改期间，写锁是一个排它锁（互斥锁、独享锁），读锁是一个共享锁
     * 写锁没释放读锁必须等待
     * 读 + 读 ：相当于无锁，并发读，只会在Redis中记录好，所有当前的读锁。他们都会同时加锁成功
     * 写 + 读 ：必须等待写锁释放
     * 写 + 写 ：阻塞方式
     * 读 + 写 ：有读锁。写也需要等待
     * 只要有读或者写的存都必须等待
     *      不会阻塞其他代码运行
     * @return
     */
    @Autowired
    private StringRedisTemplate redisTemplate;
    @RequestMapping("/write")
    @ResponseBody
    public String write(){
        //获取写lock
        RReadWriteLock write = redissonClient.getReadWriteLock("write");
        RLock rLock = write.writeLock();
        String uuid = UUID.randomUUID().toString();
        rLock.lock();
        try {
            System.out.println("当前在运行"+ Thread.currentThread().getId());

            redisTemplate.opsForValue().set("writeValue",uuid);
            System.out.println("wwwwwwwww");
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            System.out.println("写解锁");
           rLock.unlock();
        }
        return uuid;
    }
    @RequestMapping("/read")
    @ResponseBody
    public String read(){
        //获取写lock
        RReadWriteLock write = redissonClient.getReadWriteLock("write");
        RLock rLock = write.readLock();
        String writeValue="";
        rLock.lock();
        try {
            System.out.println("当前在运行"+ Thread.currentThread().getId());

             writeValue = redisTemplate.opsForValue().get("writeValue");
            System.out.println("rrrrrrr");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            System.out.println("读解锁");
            rLock.unlock();
        }
        return writeValue;
    }

    /**
     *  闭锁 ：当执行到对应它时会，必须调用解锁的方式才能运行下去
     *          door.trySetCount(5);
     *    会阻塞其他代码运行
     * @return
     */
    @GetMapping("unlock")
    @ResponseBody
    public String unlock(){
        RCountDownLatch door = redissonClient.getCountDownLatch("door");

        door.trySetCount(2);//参数1 调用多少次解锁才能放行
        try {
            System.out.println("哈哈");
            door.await();//等待闭锁完成

            System.out.println("顶顶顶");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "xx";
    }
    @GetMapping(value = "/gogogo/{id}")
    @ResponseBody
    public String gogogo(@PathVariable("id") Long id) {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown();       //计数-1

        return id + "班的人都走了...";
    }


    /**
     *  生产和消费锁
     *      当消费锁没有时会进入阻塞
     */
    @ResponseBody
    @RequestMapping("/ps")
    public String res() throws InterruptedException {

        RSemaphore semaphore = redissonClient.getSemaphore("myislock");
        System.out.println("开启锁");
        semaphore.acquire();//消费锁   消费不到阻塞
        //boolean acquire = semaphore.tryAcquire(); 消费不到返回false 不阻塞
        System.out.println("释放锁");
        return "";
    }
    @ResponseBody
    @RequestMapping("/psz")
    public String resw() throws InterruptedException {

        RSemaphore semaphore = redissonClient.getSemaphore("myislock");

        semaphore.release();//生产锁
        return "惺惺惜惺惺";
    }


}
