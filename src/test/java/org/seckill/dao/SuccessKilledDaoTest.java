package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * 配置spring和Junit的整合，Junit启动时加载springIOC
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void testInsertSuccessKilled(){
        int i = successKilledDao.insertSuccessKilled(1001L, 12306L);
        System.out.println(i);
    }

    @Test
    public void testQueryByIdWithSecKill(){
        SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(1001L,12306L);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());

    }

}