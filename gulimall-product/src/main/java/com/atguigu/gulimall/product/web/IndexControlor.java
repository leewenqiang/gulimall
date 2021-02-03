package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Category2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName IndexControlor
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/4 8:52
 * @Version 1.0
 */
@Slf4j
@Controller
public class IndexControlor {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        //查出一级分类
        List<CategoryEntity> categoryEntities = categoryService.getAllLevel1Categoryies();
        model.addAttribute("categoryEntities",categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public  Map<String, List<Category2Vo>> getCategoryJson(){
        Map<String, List<Category2Vo>> catelogJson = categoryService.getCatelogJson();
        return catelogJson;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        //获取锁,主要锁的名称一样，就是同一把锁
        RLock lock = redissonClient.getLock("my-lock");
        //加锁
//        lock.lock(); //阻塞式等待

        //10秒自动解锁，锁超时不会自动续期
        lock.lock(10, TimeUnit.SECONDS);

        try{
            log.info("加锁成功,执行业务..."+Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //测试解锁代码没有成功执行。。。
            lock.unlock();
            log.info("解锁成功..."+Thread.currentThread().getId());
        }
        return "world";
    }


    @GetMapping("/write")
    @ResponseBody
    public String writeLock(){
        //获取读写锁
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        //获取读锁
        RLock writeLock = readWriteLock.writeLock();
        String value ="" ;
        try{
            //上锁
            writeLock.lock();
            //执行业务
            Thread.sleep(30000);
            //写入数据
            value = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set("writeLock", value);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //释放锁
            writeLock.unlock();
        }
        return value;
    }

    @GetMapping("/read")
    @ResponseBody
    public String readLock(){
        //获取读写锁
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        //获取读锁
        RLock readLock = readWriteLock.readLock();
        String value = "";
        try{
            //上锁
            readLock.lock();
            value = stringRedisTemplate.opsForValue().get("writeLock");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //释放锁
            readLock.unlock();
        }
        return value;
    }

    /**
     * 信号量
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        //停车
        RSemaphore semaphore = redissonClient.getSemaphore("park-lock");
        semaphore.acquire();
        return "ok!";
    }

    @GetMapping("/go")
    @ResponseBody
    public String gogogo() throws InterruptedException {
        //停车
        RSemaphore semaphore = redissonClient.getSemaphore("park-lock");
        semaphore.release();
        return "ok!";
    }


    /**
     * 闭锁
     * @return
     * @throws InterruptedException
     */
    @ResponseBody
    @GetMapping("/door")
    public String door() throws InterruptedException {

        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("door-lock");
        countDownLatch.trySetCount(5);
        countDownLatch.await();

        return "放假了....";
    }

    @ResponseBody
    @GetMapping("lockdoor/{number}")
    public String lockdoor(@PathVariable("number") Long number) throws InterruptedException {

        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("door-lock");

        countDownLatch.countDown();

        return number+"班走完...";
    }

    @GetMapping("test1/{id}")
    @ResponseBody
    public String test1(@PathVariable("id") Integer id){
        if(id==1){
            try {
                Thread.sleep(5000);
//                stringRedisTemplate.opsForValue().set("test1","1");
//                BrandEntity brandEntity = new BrandEntity();
                CategoryEntity categoryEntity = new CategoryEntity();
                categoryEntity.setCatId(1433L);
                //1433
                categoryEntity.setName("测试111/test1/"+id);
                categoryService.updateById(categoryEntity);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setCatId(1433L);
            //1433
            categoryEntity.setName("测试111/test1/"+id);
            categoryService.updateById(categoryEntity);
        }
        return "ok";
    }


}
