package com.missuo.server.mapper;

import com.github.pagehelper.Page;
import com.missuo.pojo.dto.GoodsSalesDTO;
import com.missuo.pojo.dto.OrdersPageQueryDTO;
import com.missuo.pojo.entity.Orders;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {
  void insert(Orders orders);

  @Select("select * from orders where number = #{orderNumber}")
  Orders getByNumber(String orderNumber);

  void update(Orders orders);

  Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

  @Select("select * from orders where id = #{id}")
  Orders getById(Long id);

  @Select("select count(id) from orders where status = #{toBeConfirmed}")
  Integer countStatus(Integer toBeConfirmed);

  List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

  @Select(
      "select sum(amount) from orders where status = #{status} and order_time >= #{beginTime} and order_time <= #{endTime}")
  BigDecimal sumByMap(Map<String, Object> map);

  Integer countByMap(Map<String, Object> map);

  List<GoodsSalesDTO> getGoodsSales(Map<String, Object> map);
}
