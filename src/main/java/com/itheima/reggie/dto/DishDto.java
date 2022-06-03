package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * DishDto
 * DTO 是 Data Transfer Object的缩写，用于显示层和服务层之间的数据传输
 * DishDto继承Dish，也就是继承了Dish的所有属性，并且自己新增了3个
 */
@Data
public class DishDto extends Dish {

    //菜品对应的口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
