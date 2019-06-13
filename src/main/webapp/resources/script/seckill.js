//存放主要交互逻辑代码
/*
javascript模块化
seckill.detail.init(param)
模拟出java分包概念
 */

var seckill = {
    //封装秒杀相关ajax的url
    url:{
        now:function () {
            return '/seckill/time/now';
        },
        exposer:function (seckillId) {
            return '/seckill/'+seckillId+'/exposer';
        },
        execution:function (seckillId,md5) {
            return '/seckill/'+seckillId+'/'+md5+'/execution';
        }
    },

    //验证手机号
    validatePhone:function(phone){
        //isNaN是否是非数字，是非数字返回true
        if(phone && phone.length == 11 && !isNaN(phone)){
            return true;
        }else {
            return false;
        }

    },

    //计时
    countdown:function(seckillId,nowTime,startTime,endTime){
        //时间判断
        var seckillBox = $('#seckill-box');
        if(nowTime > endTime){
            //秒杀结束
            seckillBox.html('秒杀结束');
        }else if(nowTime < startTime){
            //秒杀未开始,计时时间绑定
            var killTime = new Date(startTime+1000);//加1000防止计时过程中时间偏移
            //countdown以killtime作为基准时间，每一次时间变化都回调毁掉函数，根据格式输出到box
            seckillBox.countdown(killTime,function (event) {
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒');
                seckillBox.html(format);
                //计时结束时回调时间
            }).on('finish.countdown',function () {
                //获取秒杀地址，控制显示逻辑，执行秒杀
                seckill.handlerSeckillKill(seckillId,seckillBox);
            });

        }else {
            //秒杀开始
            seckill.handlerSeckillKill(seckillId,seckillBox);
        }
    },

    //处理秒杀逻辑
    handlerSeckillKill:function(seckillId,node){
        //获取秒杀地址，控制显示逻辑，执行秒杀
        //控制节点的操作应该在操作内容前隐藏
        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.url.exposer(seckillId),{},function(result){
            //在回调函数中执行交互流程
            if(result && result['success']){
                var exposer = result['data'];
                if(exposer['exposed']){
                    //秒杀开启
                    //1.获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.url.execution(seckillId,md5);
                    console.log('killUrl='+killUrl);//TODO
                    //click()一直绑定，每次点击都会触发
                    //one()只绑定一次点击事件，防止用户连续点击让服务器同一时间接到大量相同的服务请求
                    $('#killBtn').one('click',function () {
                       //执行秒杀请求的操作
                       //1.先禁用按钮
                       $(this).addClass('disabled');
                       //2.发送秒杀请求
                        $.post(killUrl,{},function (result) {
                            if(result && result['success']){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //2.隐藏秒杀按钮,显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+'</span>');

                            }
                        });
                    });
                    node.show();
                }else {
                    //未开启
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    seckill.countdown(seckillId,now,start,end);
                }
            }else {
                console.log('result='+result);//TODO
            }
        });
    },

    //详情页秒杀逻辑
    detail:{
        init:function (params) {
            //手机验证和登录
            //在cookie中查找手机
            var killPhone = $.cookie('killPhone');

            //验证手机号
            if(!seckill.validatePhone(killPhone)){
                //绑定手机号
                //控制输出
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    //显示弹出层
                    show:true,
                    //禁止位置关闭
                    backdrop:false,
                    //关闭键盘事件
                    keyboard:false
                });
                //点击按钮认为填写了手机号
                $('#killPhoneBtn').click(function () {
                    //获取填写的手机号并做验证
                    var inputPhone = $('#killPhoneKey').val();
                    console.log('inputPhone='+inputPhone);//TODO
                    if(seckill.validatePhone(inputPhone)){
                        //电话写入cookie，并刷新页面
                        $.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'});//有效期7天，路径只在seckill下有效
                        window.location.reload();
                    }else {
                        //先隐藏再往里面填充内容
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>').show(300);

                    }
                });
            }
            //已经登录
            //计时交互
            $.get(seckill.url.now(),{},function (result) {
                //jquery访问json数据的一种方式
                var startTime = params['startTime'];
                var endTime = params['endTime'];
                var seckillId = params['seckillId'];

                if(result && result['success']){
                    //返回成功,时间判断
                    var nowTime = result['data'];
                    seckill.countdown(seckillId,nowTime,startTime,endTime);

                }else {
                    console.log('result='+result);//TODO
                }
            });


        }
    }
}