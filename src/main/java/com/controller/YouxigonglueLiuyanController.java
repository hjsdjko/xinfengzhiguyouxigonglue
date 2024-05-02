package com.controller;


import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.StringUtil;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;

import com.entity.YouxigonglueLiuyanEntity;

import com.service.YouxigonglueLiuyanService;
import com.entity.view.YouxigonglueLiuyanView;
import com.service.YonghuService;
import com.entity.YonghuEntity;
import com.service.YouxigonglueService;
import com.entity.YouxigonglueEntity;

import com.utils.PageUtils;
import com.utils.R;

/**
 * 攻略留言
 * 后端接口
 * @author
 * @email
 * @date 2021-04-20
*/
@RestController
@Controller
@RequestMapping("/youxigonglueLiuyan")
public class YouxigonglueLiuyanController {
    private static final Logger logger = LoggerFactory.getLogger(YouxigonglueLiuyanController.class);

    @Autowired
    private YouxigonglueLiuyanService youxigonglueLiuyanService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;



    //级联表service
    @Autowired
    private YonghuService yonghuService;
    @Autowired
    private YouxigonglueService youxigonglueService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isNotEmpty(role) && "用户".equals(role)){
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        }
        params.put("orderBy","id");
        PageUtils page = youxigonglueLiuyanService.queryPage(params);

        //字典表数据转换
        List<YouxigonglueLiuyanView> list =(List<YouxigonglueLiuyanView>)page.getList();
        for(YouxigonglueLiuyanView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        YouxigonglueLiuyanEntity youxigonglueLiuyan = youxigonglueLiuyanService.selectById(id);
        if(youxigonglueLiuyan !=null){
            //entity转view
            YouxigonglueLiuyanView view = new YouxigonglueLiuyanView();
            BeanUtils.copyProperties( youxigonglueLiuyan , view );//把实体数据重构到view中

            //级联表
            YonghuEntity yonghu = yonghuService.selectById(youxigonglueLiuyan.getYonghuId());
            if(yonghu != null){
                BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                view.setYonghuId(yonghu.getId());
            }
            //级联表
            YouxigonglueEntity youxigonglue = youxigonglueService.selectById(youxigonglueLiuyan.getYouxigonglueId());
            if(youxigonglue != null){
                BeanUtils.copyProperties( youxigonglue , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                view.setYouxigonglueId(youxigonglue.getId());
            }
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody YouxigonglueLiuyanEntity youxigonglueLiuyan, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,youxigonglueLiuyan:{}",this.getClass().getName(),youxigonglueLiuyan.toString());
        youxigonglueLiuyan.setInsertTime(new Date());
        youxigonglueLiuyan.setCreateTime(new Date());
        youxigonglueLiuyanService.insert(youxigonglueLiuyan);
        return R.ok();
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody YouxigonglueLiuyanEntity youxigonglueLiuyan, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,youxigonglueLiuyan:{}",this.getClass().getName(),youxigonglueLiuyan.toString());
        youxigonglueLiuyanService.updateById(youxigonglueLiuyan);//根据id更新
        return R.ok();
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        youxigonglueLiuyanService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }



    /**
    * 前端列表
    */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        // 没有指定排序字段就默认id倒序
        if(StringUtil.isEmpty(String.valueOf(params.get("orderBy")))){
            params.put("orderBy","id");
        }
        PageUtils page = youxigonglueLiuyanService.queryPage(params);

        //字典表数据转换
        List<YouxigonglueLiuyanView> list =(List<YouxigonglueLiuyanView>)page.getList();
        for(YouxigonglueLiuyanView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c);
        }
        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        YouxigonglueLiuyanEntity youxigonglueLiuyan = youxigonglueLiuyanService.selectById(id);
            if(youxigonglueLiuyan !=null){
                //entity转view
        YouxigonglueLiuyanView view = new YouxigonglueLiuyanView();
                BeanUtils.copyProperties( youxigonglueLiuyan , view );//把实体数据重构到view中

                //级联表
                    YonghuEntity yonghu = yonghuService.selectById(youxigonglueLiuyan.getYonghuId());
                if(yonghu != null){
                    BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setYonghuId(yonghu.getId());
                }
                //级联表
                    YouxigonglueEntity youxigonglue = youxigonglueService.selectById(youxigonglueLiuyan.getYouxigonglueId());
                if(youxigonglue != null){
                    BeanUtils.copyProperties( youxigonglue , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setYouxigonglueId(youxigonglue.getId());
                }
                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody YouxigonglueLiuyanEntity youxigonglueLiuyan, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,youxigonglueLiuyan:{}",this.getClass().getName(),youxigonglueLiuyan.toString());
        youxigonglueLiuyan.setInsertTime(new Date());
        youxigonglueLiuyan.setCreateTime(new Date());
        youxigonglueLiuyanService.insert(youxigonglueLiuyan);
        return R.ok();
    }


}

