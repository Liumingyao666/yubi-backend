package com.liumingyao.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.liumingyao.springbootinit.model.dto.chart.ChartQueryRequest;
import com.liumingyao.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author LiuMingyao
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2023-10-05 13:31:43
*/
public interface ChartService extends IService<Chart> {

    Wrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);
}
