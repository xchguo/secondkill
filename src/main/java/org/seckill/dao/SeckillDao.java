package org.seckill.dao;

import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;

public interface SeckillDao {

    /**
     * 减库存
     * @param seckillId 减库存的id
     * @param killTime  执行减库存的时间，对应数据库的create_time
     * @return 如果影响行数>`，表示更新的记录
     */
    int reduceNumber(long seckillId, Date killTime);

    /**
     * 根据sid查询秒杀商品对象
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(int offset,int limit);
}
