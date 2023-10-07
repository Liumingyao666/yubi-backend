package com.liumingyao.springbootinit.mapper;

import com.liumingyao.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
* @author LiuMingyao
* @description 针对表【chart(图表信息表)】的数据库操作Mapper
* @createDate 2023-10-05 13:31:43
* @Entity com.liumingyao.springbootinit.model.entity.Chart
*/
public interface ChartMapper extends BaseMapper<Chart> {

    public List<Map<String, Object>> queryChartData(String querySql);

}




