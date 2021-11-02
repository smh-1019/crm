package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.mapper.SaleChanceMapper;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance, Integer> {

    @Autowired(required = false)
    private SaleChanceMapper saleChanceMapper;


    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery query) {
        Map<String, Object> map = new HashMap<>();
        PageHelper.startPage(query.getPage(), query.getLimit());
        PageInfo<SaleChance> pageInfo = new PageInfo<>(saleChanceMapper.selectByParams(query));
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }


    /**
     * 营销机会数据添加
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance) {
        // 1.参数校验
        checkParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        // 2.设置相关参数默认值
        // 未选择分配人
        if (StringUtils.isBlank(saleChance.getAssignMan())) {
            saleChance.setState(0);
            saleChance.setDevResult(0);
        }
        // 选择分配人
        if (StringUtils.isNotBlank(saleChance.getAssignMan())) {
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
        saleChance.setIsValid(1);
        saleChance.setUpdateDate(new Date());
        saleChance.setCreateDate(new Date());

        // 3.执行添加 判断结果
        AssertUtil.isTrue(insertSelective(saleChance) < 1,"数据添加失败！");
    }

    public void checkParams(String customerName, String link_man, String link_phone) {
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "请输入客户名");
        AssertUtil.isTrue(StringUtils.isBlank(link_man), "请输入联系人");
        AssertUtil.isTrue(StringUtils.isBlank(link_phone), "请输入电话");
        AssertUtil.isTrue(!PhoneUtil.isMobile(link_phone), "请输入合法电话");
    }


    /**
     * 营销机会数据修改
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance) {
        // 验证
        SaleChance temp =selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(temp == null,"待修改数据不存在");
        checkParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        // 2.设置相关参数默认值
        // 未选择分配人
        //没有原分配人，有新分配人
        if (StringUtils.isNotBlank(saleChance.getAssignMan()) && StringUtils.isBlank(temp.getAssignMan())) {
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());

        } else if (StringUtils.isBlank(saleChance.getAssignMan()) && StringUtils.isNotBlank(temp.getAssignMan())) {//有原分配人，没有新分配人
            saleChance.setState(0);
            saleChance.setDevResult(0);
            saleChance.setAssignTime(null);
            saleChance.setAssignMan("");
        }
        //有原分配人，有新分配人   没有原分配人，没有新分配人  状态无需改变
        //修改时间变更
        saleChance.setUpdateDate(new Date());
        System.out.println(saleChance.toString());
        // 3.执行添加 判断结果
        AssertUtil.isTrue(updateByPrimaryKeySelective(saleChance) < 1,"数据添加失败！");
    }

    /**
     * 批量删除方法
     * @param ids
     */
    public void deleteSaleChance(Integer[] ids){
        //判断是否选择数据
        AssertUtil.isTrue(ids.length == 0 || ids == null,"请选择要删除的数据");
        //判断删除后返回数量是否与要删除数量一致
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids) != ids.length, "数据删除失败！！！");
    }
}
