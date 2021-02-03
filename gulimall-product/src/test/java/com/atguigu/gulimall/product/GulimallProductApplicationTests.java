package com.atguigu.gulimall.product;

//@Slf4j
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class GulimallProductApplicationTests {



   /* @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public void contextLoads() {
        System.out.println(redissonClient);
    }

    @Test
    public void testRedis(){

//        stringRedisTemplate.opsForValue().set("aaa","bbb");
//
//        System.out.println(stringRedisTemplate.opsForValue().get("aaa"));

//        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", UUID.randomUUID().toString(),300, TimeUnit.SECONDS);
//        List<AttrGroppVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(111L, 225L);
        List<SkuSaleAttrVo> saleAttrValuesBySpuId = skuSaleAttrValueDao.getSaleAttrValuesBySpuId(4L);
        System.out.println(saleAttrValuesBySpuId);

    }*/



}
