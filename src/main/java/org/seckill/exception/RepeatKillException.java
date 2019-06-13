package org.seckill.exception;

/**
 * 重复秒杀异常（运行期异常）
 * spring的声明式事务只对运行期异常做回滚
 */
public class RepeatKillException extends SeckillException{
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
