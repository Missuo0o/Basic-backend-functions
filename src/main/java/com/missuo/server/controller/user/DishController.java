package com.missuo.server.controller.user;

import com.missuo.common.constant.StatusConstant;
import com.missuo.common.result.Result;
import com.missuo.pojo.entity.Dish;
import com.missuo.pojo.vo.DishVO;
import com.missuo.server.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Tag(name = "Dish Management")
public class DishController {
  @Autowired private DishService dishService;
  @Autowired private RedisTemplate<Object, Object> redisTemplate;

  @GetMapping("/list")
  @Operation
  public Result list(Long categoryId) {
    log.info("categoryId: {}", categoryId);

    String key = "dish_" + categoryId;

    // select from redis
    List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
    if (list != null && !list.isEmpty()) {
      return Result.success(list);
    }

    Dish dish = new Dish();
    dish.setCategoryId(categoryId);
    dish.setStatus(StatusConstant.ENABLE);

    list = dishService.listWithFlavor(dish);
    redisTemplate.opsForValue().set(key, list);

    return Result.success(list);
  }
}
