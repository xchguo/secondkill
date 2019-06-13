package org.seckill.web;

import org.apache.log4j.Logger;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 *接收参数，跳转控制
 */

@Controller
@RequestMapping("/seckill")//url:/模块/资源/{id}/细分
public class SeckillController {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model) {
        //获取列表页
        //list.jsp + model(数据) = ModelAndView
       List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list",list);
        return "list";// 即返回/WEB-INF/jsp/"list".jsp
    }

    /**
     * 秒杀详情
     * @param seckillId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail (@PathVariable("seckillId") Long seckillId, Model model){
        if(seckillId == null){
            //请求重定向到/seckill/list
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if(seckillId == null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }

    //ajax  json

    /**
     * 暴露秒杀接口
     * @param seckillId
     * @return
     */
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})//post,直接在浏览器输入无效
    @ResponseBody   //告诉返回的是json类型而非跳转，spring-MVC会封装成json
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){

        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<>(true,exposer);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = new SeckillResult<>(false,e.getMessage());
        }
        return result;
    }

    /**
     * 执行秒杀
     * @param seckillId
     * @param md5
     * @param phone
     * @return
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution>  execute(@PathVariable("seckillId") Long seckillId,
                                                    @PathVariable("md5") String md5,
                                                    @CookieValue(value = "killPhone",required = false) Long phone){
        //也可以才用springMVC的数据验证
        if(phone==null){
            return new SeckillResult<SeckillExecution>(true,"未登录");
        }
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        } catch (RepeatKillException e) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
            //系统允许异常，无需打印日志
            //logger.error(e.getMessage());
        } catch (SeckillCloseException e){
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        } catch (Exception e){
            logger.error(e.getMessage());
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }
    }

    /**
     * 请求当前时间
     * @return
     */
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult<Long>(true,now.getTime());
    }
}
