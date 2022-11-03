package com.liu.xyz.gulimall.product.service.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.Query;
import com.liu.xyz.gulimall.product.dao.CategoryDao;
import com.liu.xyz.gulimall.product.entity.CategoryEntity;
import com.liu.xyz.gulimall.product.service.CategoryBrandRelationService;
import com.liu.xyz.gulimall.product.service.CategoryService;
import com.liu.xyz.gulimall.product.web.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;
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
    public List<CategoryEntity> listWithTree() {

        List<CategoryEntity> categoryEntityList = baseMapper.selectList(new QueryWrapper<>());

        List<CategoryEntity> list = categoryEntityList.stream()
                .filter(meun -> meun.getParentCid().equals(0L))
                .map(meun -> searchChildren(meun, categoryEntityList))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public void removeByIdAll(Long[] catIds){
        //TODO 如果别的地方要用就不能删除
        List<Long> collect = Arrays.stream(catIds).collect(Collectors.toList());
        baseMapper.deleteBatchIds(collect);
    }

    @Override
    public Long[] getByIdCatelogPath(Long catelogId) {
        ArrayList<Long> longs = new ArrayList<>();
        get(catelogId,longs);

        Collections.reverse(longs);
        return longs.toArray(new Long[longs.size()]);
    }

    /**
     * 级联更新所有关联的数据
     *
     * @CacheEvict:失效模式
     * @CachePut:双写模式，需要有返回值
     * 1、同时进行多种缓存操作：@Caching
     * 2、指定删除某个分区下的所有数据 @CacheEvict(value = "category",allEntries = true)
     * 3、存储同一类型的数据，都可以指定为同一分区
     * @param category
     */
    // @Caching(evict = {
    //         @CacheEvict(value = "category",key = "'getLevel1Categorys'"),
    //         @CacheEvict(value = "category",key = "'getCatalogJson'")
    // })
    @CacheEvict(value = "categorys",allEntries = true)       //删除某个分区下的所有数据
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateByIdDeatil(CategoryEntity category) {

        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("catalogJson-lock");
        RLock rLock = readWriteLock.writeLock();
        rLock.lock();
        try {
            baseMapper.updateById(category);
            //修改成功后 删除缓存
            redisTemplate.delete("category");
        }catch (Exception e){
           e.printStackTrace();
        }  finally{
            rLock.unlock();
        }
        if(!StringUtils.isEmpty(category.getName())){
            categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
        }
    }

    /**
     *  重点 ：在spring 2.18 中 导入的redis 在并发情况下 会导致
     *       TODO 产生堆外内存溢出OutOfDirectMemoryError:
     *      1)、springboot2.0以后默认使用lettuce操作redis的客户端，它使用通信
     *      2)、lettuce的bug导致netty堆外内存溢出   可设置：-Dio.netty.maxDirectMemory
     *     解决方案：不能直接使用-Dio.netty.maxDirectMemory去调大堆外内存
     *     1)、升级lettuce客户端。      2）、切换使用jedis
     *      在spring2.7 不会出现
     *  在高并发情况下redis会出现
     *      缓存穿透
     *          key对应的value在数据不存在，每次key就获取不到，请求的请求就给数据库了 就会压跨它
     *      缓存击穿
     *          key对应的数据存在，但redis中过期 高并发过来就会 请求的请求就给数据库了 就会压跨它
     *      缓存雪崩
     *
     *  分布式锁
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonRedisson(){
        String category = redisTemplate.opsForValue().get("category");
        if(StringUtils.isEmpty(category)){
            System.out.println("准备查询数据库");
            Map<String, List<Catelog2Vo>> catalogJsonForDB =getCatalogJsonForDBRedisson();//getCatalogJsonForDBRedis(); //getCatalogJsonForDB();

            return  catalogJsonForDB;
        }
        //System.out.println("缓存中有");
        TypeReference<  Map<String, List<Catelog2Vo>>> typeReference =
                new TypeReference<  Map<String, List<Catelog2Vo>>>() {};
        Map<String, List<Catelog2Vo>> map = JSON.parseObject(category, typeReference);

        return map;

    }

    /**
     *  本地锁
     * @return
     */
    @Cacheable(value = {"categorys"},key = "'getCatalogJson'",sync = true)
    @Override
    public  Map<String, List<Catelog2Vo>> getCatalogJson(){

        //获取所有数据
        List<CategoryEntity> ls = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>());

        //1.获取1及父类信息
        List<CategoryEntity> categorys = getLevelCategorys();
        //2.封装数据
        Map<String, List<Catelog2Vo>> map = categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {

            //2.1 获取每个数据的2及数据
            List<CategoryEntity> categoryEntityList = getCateList(ls,v.getCatId());

            // baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            List<Catelog2Vo> catelog2VoList = null;
            if (categoryEntityList != null) {
                //封装二级分类
                catelog2VoList = categoryEntityList.stream().map(item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());

                    List<CategoryEntity> catelog3 =getCateList(ls,item.getCatId());
                    //this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", item.getCatId()));
                    //封装三级分类
                    List<Catelog2Vo.Category3Vo> collect = catelog3.stream().map(obj -> {
                        Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo();
                        category3Vo.setCatalog2Id(item.getCatId().toString());
                        category3Vo.setName(obj.getName());
                        category3Vo.setId(obj.getCatId().toString());
                        return category3Vo;
                    }).collect(Collectors.toList());
                    catelog2Vo.setCatalog3List(collect);
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2VoList;
        }));


        return map;
    }




    /**
     * @Cacheable 缓存方法返回结果 它的是基于aop的 不能缓存本类调用的时缓存同一个类 a 调b b有缓存 但缓存不生效
     *         key是 缓存的key  root.methodName  以方法名作为key
     *         value、cacheNames：两个等同的参数（cacheNames为Spring 4新增，作为value的别名），用于指定缓存存储的集合名。由于Spring 4中新增了@CacheConfig，因此在Spring 3中原本必须有的value属性，也成为非必需项了
     *         key：缓存对象存储在Map集合中的key值，非必需，缺省按照函数的所有参数组合作为key值，若自己配置需使用SpEL表达式，比如：@Cacheable(key = "#p0")：使用函数第一个参数作为缓存的key值，更多关于SpEL表达式的详细内容可参考官方文档
     *         condition：缓存对象的条件，非必需，也需使用SpEL表达式，只有满足表达式条件的内容才会被缓存，比如：@Cacheable(key = "#p0", condition = "#p0.length() < 3")，表示只有当第一个参数的长度小于3的时候才会被缓存，若做此配置上面的AAA用户就不会被缓存，读者可自行实验尝试。
     *         unless：另外一个缓存条件参数，非必需，需使用SpEL表达式。它不同于condition参数的地方在于它的判断时机，该条件是在函数被调用之后才做判断的，所以它可以通过对result进行判断。
     *         keyGenerator：用于指定key生成器，非必需。若需要指定一个自定义的key生成器，我们需要去实现org.springframework.cache.interceptor.KeyGenerator接口，并使用该参数来指定。需要注意的是：该参数与key是互斥的
     *         cacheManager：用于指定使用哪个缓存管理器，非必需。只有当有多个时才需要使用
     *         cacheResolver：用于指定使用那个缓存解析器，非必需。需通过org.springframework.cache.interceptor.CacheResolver接口来实现自己的缓存解析器，并用该参数指定。
     *      它默认保存的数据是java序列化之后的
     */
    @Cacheable(value = {"categorys"},key = "'getLevelCategorys'")
    @Override
    public List<CategoryEntity> getLevelCategorys() {

        System.out.println("缓存了");
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    /**
     * 重点2
     *  上分布式锁
     * @return
     */
    public  Map<String, List<Catelog2Vo>> getCatalogJsonForDBRedisson() {
        /**
         *
         * 缓存后当redis和数据库不一致时的时产生问题
         *  可以修改数据库后 缓存删除              简称  失效模式
         *      延出问题
         *  可以修改数据库后 重新查询设置到redis   简称  双写模式
         *      延出问题如果 在1号线程读写数据库时 2号线程运行较快执行完后 1 在写入redis就会出现脏读
         *              解决 1.在修改缓存数据时加锁
         *              解决 2.如果数据可以容错，可以等数据ttl过期
         */
        //上分布式锁 使用读写锁  默认时间30s
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("Catalogory-JSON");
        RLock rLock = readWriteLock.readLock();
        rLock.lock();

        Map<String, List<Catelog2Vo>> dataFromDb;
            try{
                dataFromDb = getCatalogDB();
            }finally {
                //解锁
                rLock.unlock();
            }

            return dataFromDb;
    }

    /**
     * 重点1
     * 使用redis的机制枷锁 防止雪崩 击穿
     * @return
     */
    @Override
    public  Map<String, List<Catelog2Vo>> getCatalogJsonForDBRedis() {
        /**
         *
         *   0.为什么使用redis 的setnx 的机制 当成一把锁是synchronized 不行吗
         *   1.为什么要设置过期时间
         *      如果不设置，程序在if(lock)死机别人永远进不去，它也出不来，会导致死锁，必须是一致性的
         *   2.为什么要设置uuId
         *      如果业务时间执行很长，key自动过期了,删的就是别人的锁
         *          如果正好判断是当前值，正要删除锁的时候，锁已经过期， 别人已经设置到了新的值。那么我们删除的是别人的锁
         *      解决使用: 删除锁必须保证原子性。使用redis+Lua脚本完成
         *
         */
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("hello", uuid,300,TimeUnit.SECONDS);
        if(lock){

            Map<String, List<Catelog2Vo>> dataFromDb;
            try{
                dataFromDb = getCatalogDB();
            }finally {
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//删除锁
                Long lock1 = redisTemplate.execute(new
                                DefaultRedisScript<Long>(script, Long.class)
                        , Arrays.asList("hello"), uuid);
            }

            return  dataFromDb;
        }else {
            //System.out.println("获取分布式锁失败...等待重试");
            try{
                Thread.sleep(200);
            }catch (Exception e){
            }
            return getCatalogJsonForDBRedis();
        }

    }
    //获取三级分类数据

    /**
     * 重点2
     *  应对的是分布式锁情况下
     * @return
     */
    public  Map<String, List<Catelog2Vo>> getCatalogDB(){


        String category = redisTemplate.opsForValue().get("category");
        if(!StringUtils.isEmpty(category)){

            TypeReference<Map<String, List<Catelog2Vo>>> typeReference =
                    new TypeReference<  Map<String, List<Catelog2Vo>>>() {};
            Map<String, List<Catelog2Vo>> map = JSON.parseObject(category, typeReference);

            return map;
        }

        System.out.println("在查询数据库");
        //获取所有数据
        List<CategoryEntity> ls = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>());

        //1.获取1及父类信息
        List<CategoryEntity> categorys = getLevelCategorys();
        //2.封装数据
        Map<String, List<Catelog2Vo>> map = categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {

            //2.1 获取每个数据的2及数据
            List<CategoryEntity> categoryEntityList = getCateList(ls,v.getCatId());

            // baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            List<Catelog2Vo> catelog2VoList = null;
            if (categoryEntityList != null) {
                //封装二级分类
                catelog2VoList = categoryEntityList.stream().map(item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());

                    List<CategoryEntity> catelog3 =getCateList(ls,item.getCatId());
                    //this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", item.getCatId()));
                    //封装三级分类
                    List<Catelog2Vo.Category3Vo> collect = catelog3.stream().map(obj -> {
                        Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo();
                        category3Vo.setCatalog2Id(item.getCatId().toString());
                        category3Vo.setName(obj.getName());
                        category3Vo.setId(obj.getCatId().toString());
                        return category3Vo;
                    }).collect(Collectors.toList());
                    catelog2Vo.setCatalog3List(collect);
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2VoList;
        }));

        String jsonString = JSON.toJSONString(map);
        redisTemplate.opsForValue().set("category",jsonString,1, TimeUnit.DAYS);
        return map;
    }

    //获取三级分类数据
    public Map<String, List<Catelog2Vo>> getCatalogJsonForDB() {

        //(单台) 枷锁防止高并发查询数据库 一次就放一个进去
        synchronized (this){
          //查询redis 是否保存了数据
            String category = redisTemplate.opsForValue().get("category");
            if(!StringUtils.isEmpty(category)){


                TypeReference<Map<String, List<Catelog2Vo>>> typeReference =
                        new TypeReference<  Map<String, List<Catelog2Vo>>>() {};
                Map<String, List<Catelog2Vo>> map = JSON.parseObject(category, typeReference);

                return map;
            }
            System.out.println("在查数据库");

            //获取所有数据
            List<CategoryEntity> ls = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>());

            //1.获取1及父类信息
            List<CategoryEntity> categorys = getLevelCategorys();
            //2.封装数据
            Map<String, List<Catelog2Vo>> map = categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {

                //2.1 获取每个数据的2及数据
                List<CategoryEntity> categoryEntityList = getCateList(ls,v.getCatId());

                // baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
                List<Catelog2Vo> catelog2VoList = null;
                if (categoryEntityList != null) {
                    //封装二级分类
                    catelog2VoList = categoryEntityList.stream().map(item -> {
                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());

                        List<CategoryEntity> catelog3 =getCateList(ls,item.getCatId());
                        //this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", item.getCatId()));
                        //封装三级分类
                        List<Catelog2Vo.Category3Vo> collect = catelog3.stream().map(obj -> {
                            Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo();
                            category3Vo.setCatalog2Id(item.getCatId().toString());
                            category3Vo.setName(obj.getName());
                            category3Vo.setId(obj.getCatId().toString());
                            return category3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                        return catelog2Vo;
                    }).collect(Collectors.toList());
                }
                return catelog2VoList;
            }));

            /**
             *  在锁中存放redis数据  避免 高并发时redis和java通信时其他线程查询不到数据
             */
            String jsonString = JSON.toJSONString(map);
            redisTemplate.opsForValue().set("category",jsonString,1, TimeUnit.DAYS);
            return map;
        }

    }

    /**
     *   处理数据
     * @return
     */
    public List<CategoryEntity> getCateList(List<CategoryEntity> list,Long catId){

       return list.stream().filter(obj->{
           return obj.getParentCid().equals(catId);
        }).collect(Collectors.toList());
    }

    /**
     *  获取三级 分类 id
     * @param catelogId
     * @param longs
     * @return
     */
    public List<Long> get(Long catelogId ,List<Long> longs){

        longs.add(catelogId);
        CategoryEntity categoryEntity = baseMapper.selectById(catelogId);
        if(categoryEntity.getParentCid()!=0){
            get(categoryEntity.getParentCid(),longs);
        }
        return longs;

    }

    /**
     *  三级分类 封装子类
     * @param meun
     * @param all
     * @return
     */
    public CategoryEntity searchChildren(CategoryEntity meun,List<CategoryEntity> all){

        List<CategoryEntity> list = all.stream()
                .filter(allmeun -> allmeun.getParentCid().equals(meun.getCatId()))
                .map(allmeun -> {
                    return searchChildren(allmeun, all);
                })
                .collect(Collectors.toList());

        meun.setChildren(list);
        return meun;
    }

//    @Override
//    public List<CategoryEntity> listWithTree() {
//
//        List<CategoryEntity> root = baseMapper.selectList(new QueryWrapper<>());
//
//        List<CategoryEntity> list = root.stream().filter(menu -> menu.getParentCid().equals(0L)).map(meun1 -> {
//                    meun1.setChildren(searchChildren(meun1, root));
//                    return meun1;
//                }).sorted((m1, m2) -> {
//                    return (m1.getSort() == null ? 0 : m1.getSort()) - (m2.getSort() == null ? 0 : m2.getSort());
//                })
//                .collect(Collectors.toList());
//
//
//        return list;
//    }
//
//
//    public List<CategoryEntity> searchChildren(CategoryEntity meun,List<CategoryEntity> all){
//
//        List<CategoryEntity> list = all.stream().filter(category1 -> {
//            return meun.getCatId().equals(category1.getParentCid());
//        }).map(category2 -> {
//            category2.setChildren(searchChildren(category2, all));
//            return category2;
//        }).sorted((m1, m2) -> {
//            return (m1.getSort() == null ? 0 : m1.getSort()) - (m2.getSort() == null ? 0 : m2.getSort());
//        }).collect(Collectors.toList());
//        return list;
//    }
}