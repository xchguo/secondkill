package org.seckill.service.impl;

import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import org.apache.log4j.Logger;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = Logger.getLogger(SeckillServiceImpl.class);

    //注入service依赖
    @Autowired  //@Resource @Inject
    private SeckillDao seckillDao;

    //注入service依赖
    @Autowired  //@Resource @Inject
    private SuccessKilledDao successKilledDao;

    //MD5盐值字符串，用于混淆MD5
    private final String slat = "pse$ap0!`.,&^fo;efvmslkw*ri3934p+25%34";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,3);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if(seckill == null){
            return new Exposer(false,seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime()<startTime.getTime() || nowTime.getTime() > endTime.getTime()){
            //秒杀结束
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }
        //转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    @Override
    @Transactional
    /*
    使用注解控制事务方法的优点
    1.保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求或剥离到事务方法外部，将事务方法设置为private再去调用
    2.不是所有方法都需要事务，如只有一条修改操作、只读操作不需要事务
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill date rewrite");
        }
        //执行秒杀逻辑：减库存-->记录购买行为
        //在执行过程中除了抛出自定义的异常外还有可能出现数据库连接异常等等，应catch
        try {
            int updateCount = seckillDao.reduceNumber(seckillId, new Date());
            if(updateCount<=0){
                //更新库存未成功
                throw new SeckillCloseException("seckill is closed");
            } else {
                //记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
                //唯一验证：id+userPhone
                if (insertCount <= 0){
                    //重复秒杀
                    throw new RepeatKillException("seckill repeated");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
                }
            }
        } catch (SeckillCloseException e1){
            throw e1;
        } catch (RepeatKillException e2){
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage());
            //将所有的编译型异常转换为运行时异常
            throw new SeckillException("seckill inner error:"+e.getMessage());
        }
    }


    /**
     * 生成MD5
     * @param seckillId
     * @return
     */
    private String getMD5(long seckillId){
        //只对id做MD5可以通过固定MD5跑出固定值，加上盐值
        String base = seckillId + slat;
        //根据spring的工具类生成MD5
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}
