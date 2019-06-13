package org.seckill.service;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置spring和Junit的整合，Junit启动时加载springIOC
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private Logger logger = Logger.getLogger(this.getClass());

    //注入service实现类依赖
    @Autowired
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList(){
        List<Seckill> seckillList = seckillService.getSeckillList();
//        System.out.println(seckillList);
        logger.info("list="+seckillList);
    }

    @Test
    public void testGetById(){
        long id = 1000L;
        Seckill seckill = seckillService.getById(id);
        logger.info("getById-->"+seckill);
    }

    //集成测试代码完整逻辑，注意可重复执行。应将接口暴露和秒杀两个方法放在一起测试
    @Test
    public void testSeckillLogic(){
        long id = 1000L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            //秒杀已经开启
            logger.info("exposer-> "+exposer);
            long userPhone = 17790000000L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(id, userPhone, md5);
                logger.info("seckillExecution-> "+execution);
            } catch (RepeatKillException e1){
                logger.error(e1.getMessage());
            } catch (SeckillCloseException e2){
                logger.error(e2.getMessage());
            }
        } else {
            //秒杀未开启
            logger.warn("exposer->"+exposer);
        }

    }

   /* @Test
    public void testExportSeckillUrl(){
        long id = 1000L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer-> "+exposer);

    }

    @Test
    public void testExecuteSeckill(){
        //org.seckill.exception.RepeatKillException: seckill repeated
        //业务允许的异常应trycatch，不应该抛给Junit，因为出现业务允许的异常单元测试也应是通过的
        long seckillId = 1000L;
        long userPhone = 13893131313L;
        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, "c0fbb0d352304d8c9fbdece45943e5a1");
            logger.info("seckillExecution-> "+execution);
        } catch (RepeatKillException e1){
            logger.error(e1.getMessage());
        } catch (SeckillCloseException e2){
            logger.error(e2.getMessage());
        }
    }*/

}