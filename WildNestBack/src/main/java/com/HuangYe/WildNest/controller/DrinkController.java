package com.HuangYe.WildNest.controller;

import com.HuangYe.WildNest.common.Result;
import com.HuangYe.WildNest.entity.Drink;
import com.HuangYe.WildNest.service.DrinkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 酒品控制器
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@RestController
@RequestMapping("/drinks")
@RequiredArgsConstructor
@Validated
@Tag(name = "酒品管理", description = "酒品相关接口")
public class DrinkController extends BaseController {

    private final DrinkService drinkService;

    /**
     * 分页查询酒品列表
     */
    @GetMapping
    @Operation(summary = "获取酒品列表", description = "分页查询可售酒品，支持分类筛选")
    public Result<IPage<Drink>> getDrinks(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "分类ID")
            @RequestParam(required = false) Long categoryId) {
        try {
            IPage<Drink> drinks = drinkService.getAvailableDrinks(current, size, categoryId);
            return Result.success(drinks);
        } catch (Exception e) {
            log.error("获取酒品列表失败，current: {}, size: {}, categoryId: {}", current, size, categoryId, e);
            return Result.error("获取酒品列表失败");
        }
    }

    /**
     * 根据ID获取酒品详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取酒品详情", description = "根据ID获取酒品详细信息，会增加浏览量")
    public Result<Drink> getDrinkById(
            @Parameter(description = "酒品ID", required = true)
            @PathVariable Long id) {
        try {
            Drink drink = drinkService.getDrinkDetailById(id);
            if (drink == null) {
                return Result.error("酒品不存在或已下架");
            }
            return Result.success(drink);
        } catch (Exception e) {
            log.error("获取酒品详情失败，id: {}", id, e);
            return Result.error("获取酒品详情失败");
        }
    }

    /**
     * 搜索酒品
     */
    @GetMapping("/search")
    @Operation(summary = "搜索酒品", description = "根据关键词搜索酒品（名称、描述、标签）")
    public Result<IPage<Drink>> searchDrinks(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam String keyword) {
        try {
            IPage<Drink> drinks = drinkService.searchDrinks(current, size, keyword);
            return Result.success(drinks);
        } catch (Exception e) {
            log.error("搜索酒品失败，keyword: {}", keyword, e);
            return Result.error("搜索酒品失败");
        }
    }

    /**
     * 获取推荐酒品
     */
    @GetMapping("/featured")
    @Operation(summary = "获取推荐酒品", description = "获取标记为推荐的酒品列表")
    public Result<List<Drink>> getFeaturedDrinks(
            @Parameter(description = "限制数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Drink> drinks = drinkService.getFeaturedDrinks(limit);
            return Result.success(drinks);
        } catch (Exception e) {
            log.error("获取推荐酒品失败，limit: {}", limit, e);
            return Result.error("获取推荐酒品失败");
        }
    }

    /**
     * 获取热门酒品
     */
    @GetMapping("/popular")
    @Operation(summary = "获取热门酒品", description = "根据浏览量获取热门酒品列表")
    public Result<List<Drink>> getPopularDrinks(
            @Parameter(description = "限制数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Drink> drinks = drinkService.getPopularDrinks(limit);
            return Result.success(drinks);
        } catch (Exception e) {
            log.error("获取热门酒品失败，limit: {}", limit, e);
            return Result.error("获取热门酒品失败");
        }
    }

    /**
     * 根据标签查询酒品
     */
    @GetMapping("/tag")
    @Operation(summary = "根据标签查询酒品", description = "根据标签筛选酒品")
    public Result<IPage<Drink>> getDrinksByTag(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "标签", required = true)
            @RequestParam String tag) {
        try {
            IPage<Drink> drinks = drinkService.getDrinksByTag(current, size, tag);
            return Result.success(drinks);
        } catch (Exception e) {
            log.error("根据标签查询酒品失败，tag: {}", tag, e);
            return Result.error("根据标签查询酒品失败");
        }
    }

    /**
     * 根据价格区间查询酒品
     */
    @GetMapping("/price-range")
    @Operation(summary = "根据价格区间查询酒品", description = "根据价格范围筛选酒品")
    public Result<IPage<Drink>> getDrinksByPriceRange(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "最低价格", required = true)
            @RequestParam Double minPrice,
            @Parameter(description = "最高价格", required = true)
            @RequestParam Double maxPrice) {
        try {
            IPage<Drink> drinks = drinkService.getDrinksByPriceRange(current, size, minPrice, maxPrice);
            return Result.success(drinks);
        } catch (Exception e) {
            log.error("根据价格区间查询酒品失败，minPrice: {}, maxPrice: {}", minPrice, maxPrice, e);
            return Result.error("根据价格区间查询酒品失败");
        }
    }

    /**
     * 获取分类下的酒品数量
     */
    @GetMapping("/count/category/{categoryId}")
    @Operation(summary = "获取分类酒品数量", description = "获取指定分类下的酒品数量")
    public Result<Integer> getDrinkCountByCategory(
            @Parameter(description = "分类ID", required = true)
            @PathVariable Long categoryId) {
        try {
            Integer count = drinkService.getDrinkCountByCategory(categoryId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取分类酒品数量失败，categoryId: {}", categoryId, e);
            return Result.error("获取分类酒品数量失败");
        }
    }
}