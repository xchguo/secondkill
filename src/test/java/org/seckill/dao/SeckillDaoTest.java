package org.seckill.dao;

import org.apache.ibatis.reflection.SystemMetaObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.sound.midi.Soundbank;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置spring和Junit的整合，Junit启动时加载springIOC
 */
@RunWith(SpringJUnit4ClassRunner.class)//Junit启动时加载springIOC
@ContextConfiguration({"classpath:spring/spring-dao.xml"})//告诉Junit spring配置文件
public class SeckillDaoTest {
    //注入dao实现类依赖
    @Resource
    private SeckillDao seckillDao;
    @Test
    public void testQueryById(){
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void testQueryAll(){
        List<Seckill> seckills = seckillDao.queryAll(0,100);
        for (Seckill seckill:seckills) {
            System.out.println(seckill);
        }
    }
    @Test
    public void testReduceNumber() throws Exception{
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L,killTime);
        System.out.println(updateCount);
    }
}