package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Category2Vo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    private Map<String, Object> cache = new HashMap<>();

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> queryTreeList() {


        //1.找到所有的数据
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        //2、组长成树形数据返回(也就是父子关系),并且排序
        List<CategoryEntity> treeList = categoryEntities
                .stream()
                .filter(r -> Objects.equals(r.getParentCid(), 0L))
                .sorted((x, y) -> {
                    if (x.getSort() == null) {
                        return -1;
                    } else if (y.getSort() == null) {
                        return 1;
                    } else {
                        return Integer.compare(x.getSort(), y.getSort());
                    }
                })
                .map(r -> {
                    r.setChildren(getChildren(r, categoryEntities));
                    return r;
                }).collect(Collectors.toList());

        return treeList;
    }

    @Override
    public Long[] getPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        path.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        while (byId.getParentCid() != 0) {
            byId = this.getById(byId.getParentCid());
            path.add(byId.getCatId());
        }
        Collections.reverse(path);
        return path.toArray(new Long[path.size()]);
    }

    /**
     * 缓存失效
     * @param category
     */

    /**
     * 组合
     * @param category
     */
//    @Caching(evict = {
//            @CacheEvict(value = "category",key = "'getAllLevel1Categoryies'"),
//            @CacheEvict(value = "category",key = "'getCatelogJson'")}
//    )

    /**
     * allEntries = true 删除整个分区的数据
     * @param category
     */
    @CacheEvict(value = "category",allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }


    /**
     *  //代表当前的方法需要缓存，如果缓存中有 方法不调用 没有会调用方法将方法的结果放入缓存
     *  指定名字，缓存分区  key指定key spel表达式
     * @return
     */
    @Cacheable(value = {"category"},key = "#root.methodName",sync = true)
    @Override
    public List<CategoryEntity> getAllLevel1Categoryies() {
        log.info("调用getAllLevel1Categoryies==");
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
//        return null;
    }


    @Cacheable(value = "category",key = "#root.methodName")
    @Override
    public Map<String, List<Category2Vo>> getCatelogJson() {

        log.info("调用数据库查询分类-----------------");

        List<CategoryEntity> allLevelCategoryies = baseMapper.selectList(null);
        //一级分类
        List<CategoryEntity> allLevel1Categoryies = getCategoryEntities(allLevelCategoryies, 0L);

        //封装
        Map<String, List<Category2Vo>> category2voMap = allLevel1Categoryies.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查到一级分类的二级分类
            List<CategoryEntity> categoryEntities = getCategoryEntities(allLevelCategoryies, v.getCatId());

            List<Category2Vo> category2Vos = null;

            if (!CollectionUtils.isEmpty(categoryEntities)) {
                category2Vos = categoryEntities.stream().map(item -> {
                    Category2Vo category2Vo = new Category2Vo();
                    category2Vo.setCatalog1Id(v.getCatId().toString());
                    category2Vo.setId(item.getCatId().toString());
                    category2Vo.setName(item.getName());


                    List<CategoryEntity> category3Entities = getCategoryEntities(allLevelCategoryies, item.getCatId());

                    if (!CollectionUtils.isEmpty(category3Entities)) {
                        List<Category2Vo.Category3Vo> collect = category3Entities.stream().map(r -> {
                            Category2Vo.Category3Vo category3Vo = new Category2Vo.Category3Vo();
                            category3Vo.setCatalog2Id(category2Vo.getId());
                            category3Vo.setId(r.getCatId().toString());
                            category3Vo.setName(r.getName());
                            return category3Vo;
                        }).collect(Collectors.toList());
                        category2Vo.setCatalog3List(collect);
                    }

                    return category2Vo;
                }).collect(Collectors.toList());
            }
            return category2Vos;
        }));
        return category2voMap;

    }

    /*public Map<String, List<Category2Vo>> getCatelogJson2() {
//        Object catalogJson = cache.get("catalogJson");
//        if(catalogJson==null){
//            return getCatelogJsonFromDb1();
//        }else{
//            log.info("缓存读取数据=-=======");
//            return (Map<String,List<Category2Vo>>)catalogJson;
//        }

        //加入redis缓存
        //查询缓存
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            //查库
            log.info("线程" + Thread.currentThread().getId() + "===" + Thread.currentThread().getName() + "执行业务缓存不命中，将要查询数据库======");
            Map<String, List<Category2Vo>> catelogJsonFromDb2 = getCatelogJsonFromDbWithRedisonLock();
            return catelogJsonFromDb2;
        } else {
            //缓存返回
            log.info("线程" + Thread.currentThread().getId() + "===" + Thread.currentThread().getName() + "执行业务缓存命中,缓存直接返回======");
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Category2Vo>>>() {
            });
        }

    }*/


    public Map<String, List<Category2Vo>> getCatelogJsonFromDbWithRedisonLock() {

        //获取锁
        RLock lock = redissonClient.getLock("catalogJson-lock");
        Map<String, List<Category2Vo>> catelogJsonFromDbWithRedisLock;
        //加锁
        lock.lock();
        try {
            //执行业务
            catelogJsonFromDbWithRedisLock = getDataFromDb();
        } finally {
            //解锁
            lock.unlock();
        }
        return catelogJsonFromDbWithRedisLock;
    }

    public Map<String, List<Category2Vo>> getCatelogJsonFromDbWithRedisLock() {
        //1、占分布式锁  设置过期时间 必须是原子的（和setIfAbsent一起设置）  加锁和设置过期时间必须是原子的
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            //占锁成功
            log.info("线程" + Thread.currentThread().getId() + "===" + Thread.currentThread().getName() + "获取分布式锁成功======");
            // 1、业务出现异常或者断电 导致删除锁没有执行怎么办（死锁）？ 给锁设置一个超时时间
            Map<String, List<Category2Vo>> catelogJsonFromDbWithRedisLock;
            try {
                //执行业务
                catelogJsonFromDbWithRedisLock = getDataFromDb();
            } finally {
                String unlokcScript = " if redis.call('get',KEYS[1]) == ARGV[1]  then  return redis.call('del',KEYS[1]) else  return 0 end";
                log.info("线程" + Thread.currentThread().getId() + "===" + Thread.currentThread().getName() + "删除分布式锁成功======");
                stringRedisTemplate.execute(new DefaultRedisScript<>(unlokcScript, Long.class), Arrays.asList("lock"), uuid);
            }
            //删除锁 ？
            // 问题1、如果业务执行时间很长，锁自己过期了，我们直接删除，有可能把别人正在持有的锁给删除了(导致越来越来的线程能拿到锁进来）
            //        占锁的时候，指定为UUID，每个人匹配自己的锁才能删除
            // 问题2、但是在获取锁到删除的过程中（网络交互），锁失效，别人获取到了锁，删除可能导致别人的锁
            //      获取锁的值和删除应该是一个原子操作才可以
            // 解锁脚本  if redis.call("get",KEYS[1]) == ARGV[1]  then  return redis.call("del",KEYS[1]) else  return 0 end
//            String unlokcScript = " if redis.call('get',KEYS[1]) == ARGV[1]  then  return redis.call('del',KEYS[1]) else  return 0 end";
//            String lockValue = stringRedisTemplate.opsForValue().get("lock");
//            if(uuid.equals(lockValue)){
//                stringRedisTemplate.delete("lock");
//            }
            //原子删除锁
//            stringRedisTemplate.execute(new DefaultRedisScript<>(unlokcScript, Long.class),Arrays.asList("lock"),uuid);
//            return catelogJsonFromDbWithRedisLock;
            return catelogJsonFromDbWithRedisLock;
        } else {

            log.info("线程" + Thread.currentThread().getId() + "===" + Thread.currentThread().getName() + "获取分布式锁失败，等待100毫秒重试======");
            //重试 休眠100ms重试
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatelogJson();
        }
    }

    private Map<String, List<Category2Vo>> getDataFromDb() {


        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            //缓存返回
            log.info("缓存读取数据=-=======");
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Category2Vo>>>() {
            });
        }

        log.info("调用数据库查询分类-----------------");

        List<CategoryEntity> allLevelCategoryies = baseMapper.selectList(null);
        //一级分类
        List<CategoryEntity> allLevel1Categoryies = getCategoryEntities(allLevelCategoryies, 0L);

        //封装
        Map<String, List<Category2Vo>> category2voMap = allLevel1Categoryies.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查到一级分类的二级分类
            List<CategoryEntity> categoryEntities = getCategoryEntities(allLevelCategoryies, v.getCatId());

            List<Category2Vo> category2Vos = null;

            if (!CollectionUtils.isEmpty(categoryEntities)) {
                category2Vos = categoryEntities.stream().map(item -> {
                    Category2Vo category2Vo = new Category2Vo();
                    category2Vo.setCatalog1Id(v.getCatId().toString());
                    category2Vo.setId(item.getCatId().toString());
                    category2Vo.setName(item.getName());


                    List<CategoryEntity> category3Entities = getCategoryEntities(allLevelCategoryies, item.getCatId());

                    if (!CollectionUtils.isEmpty(category3Entities)) {
                        List<Category2Vo.Category3Vo> collect = category3Entities.stream().map(r -> {
                            Category2Vo.Category3Vo category3Vo = new Category2Vo.Category3Vo();
                            category3Vo.setCatalog2Id(category2Vo.getId());
                            category3Vo.setId(r.getCatId().toString());
                            category3Vo.setName(r.getName());
                            return category3Vo;
                        }).collect(Collectors.toList());
                        category2Vo.setCatalog3List(collect);
                    }

                    return category2Vo;
                }).collect(Collectors.toList());
            }
            return category2Vos;
        }));
        log.info("放入缓存===");
        stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(category2voMap));
        return category2voMap;
    }

    public Map<String, List<Category2Vo>> getCatelogJsonFromDb2() {
        return getDataFromDb();
    }

    public Map<String, List<Category2Vo>> getCatelogJsonFromDb1() {

        synchronized (this) {

            //再次判断缓存数据
//            Object catalogJson = cache.get("catalogJson");
//            if (catalogJson != null) {
//                log.info("缓存读取数据=-=======");
//                return (Map<String, List<Category2Vo>>) catalogJson;
//            }

            String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
            if (!StringUtils.isEmpty(catalogJson)) {
                //缓存返回
                log.info("缓存读取数据=-=======");
                return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Category2Vo>>>() {
                });
            }

            log.info("调用数据库查询分类-----------------");

            List<CategoryEntity> allLevelCategoryies = baseMapper.selectList(null);
            //一级分类
            List<CategoryEntity> allLevel1Categoryies = getCategoryEntities(allLevelCategoryies, 0L);

            //封装
            Map<String, List<Category2Vo>> category2voMap = allLevel1Categoryies.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                //查到一级分类的二级分类
                List<CategoryEntity> categoryEntities = getCategoryEntities(allLevelCategoryies, v.getCatId());

                List<Category2Vo> category2Vos = null;

                if (!CollectionUtils.isEmpty(categoryEntities)) {
                    category2Vos = categoryEntities.stream().map(item -> {
                        Category2Vo category2Vo = new Category2Vo();
                        category2Vo.setCatalog1Id(v.getCatId().toString());
                        category2Vo.setId(item.getCatId().toString());
                        category2Vo.setName(item.getName());


                        List<CategoryEntity> category3Entities = getCategoryEntities(allLevelCategoryies, item.getCatId());

                        if (!CollectionUtils.isEmpty(category3Entities)) {
                            List<Category2Vo.Category3Vo> collect = category3Entities.stream().map(r -> {
                                Category2Vo.Category3Vo category3Vo = new Category2Vo.Category3Vo();
                                category3Vo.setCatalog2Id(category2Vo.getId());
                                category3Vo.setId(r.getCatId().toString());
                                category3Vo.setName(r.getName());
                                return category3Vo;
                            }).collect(Collectors.toList());
                            category2Vo.setCatalog3List(collect);
                        }

                        return category2Vo;
                    }).collect(Collectors.toList());
                }
                return category2Vos;
            }));
            log.info("放入缓存-----------------");
//            cache.put("catalogJson", category2voMap);

            stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(category2voMap));

            return category2voMap;
        }
    }

    private List<CategoryEntity> getCategoryEntities(List<CategoryEntity> allLevelCategoryies, Long pid) {
        return allLevelCategoryies.stream().filter(item -> item.getParentCid() == pid).collect(Collectors.toList());
    }

    @Override
    public void removeMenus(List<Long> asList) {
        //TODO 处理关联的逻辑
        this.removeByIds(asList);
    }

    /**
     * 获取子元素
     *
     * @param r
     * @param categoryEntities
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity parent, List<CategoryEntity> categoryEntities) {
        //
        List<CategoryEntity> treeList = categoryEntities
                .stream()
                .filter(r -> Objects.equals(r.getParentCid(), parent.getCatId()))
                .sorted((x, y) -> {
                    if (x.getSort() == null) {
                        return -1;
                    } else if (y.getSort() == null) {
                        return 1;
                    } else {
                        return Integer.compare(x.getSort(), y.getSort());
                    }
                })
                .map(r -> {
                    r.setChildren(getChildren(r, categoryEntities));
                    return r;
                }).collect(Collectors.toList());
        return treeList;
    }

}