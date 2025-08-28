package com.HuangYe.WildNest.controller;

import com.HuangYe.WildNest.common.Result;
import com.HuangYe.WildNest.entity.DrinkCategory;
import com.HuangYe.WildNest.service.DrinkCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 酒品分类控制器
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "酒品分类管理", description = "酒品分类相关接口")
public class DrinkCategoryController extends BaseController {

    private final DrinkCategoryService drinkCategoryService;

    /**
     * 获取所有启用的分类
     */
    @GetMapping
    @Operation(summary = "获取所有分类", description = "获取所有启用状态的酒品分类")
    public Result<List<DrinkCategory>> getAllCategories() {
        try {
            List<DrinkCategory> categories = drinkCategoryService.getAllEnabledCategories();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取所有分类失败", e);
            return Result.error("获取分类失败");
        }
    }

    /**
     * 获取顶级分类
     */
    @GetMapping("/top")
    @Operation(summary = "获取顶级分类", description = "获取所有顶级分类（父分类ID为0）")
    public Result<List<DrinkCategory>> getTopLevelCategories() {
        try {
            List<DrinkCategory> categories = drinkCategoryService.getTopLevelCategories();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取顶级分类失败", e);
            return Result.error("获取顶级分类失败");
        }
    }

    /**
     * 根据父分类ID获取子分类
     */
    @GetMapping("/children/{parentId}")
    @Operation(summary = "获取子分类", description = "根据父分类ID获取子分类列表")
    public Result<List<DrinkCategory>> getCategoriesByParentId(
            @Parameter(description = "父分类ID", required = true)
            @PathVariable Long parentId) {
        try {
            List<DrinkCategory> categories = drinkCategoryService.getCategoriesByParentId(parentId);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取子分类失败，parentId: {}", parentId, e);
            return Result.error("获取子分类失败");
        }
    }

    /**
     * 搜索分类
     */
    @GetMapping("/search")
    @Operation(summary = "搜索分类", description = "根据分类名称搜索分类")
    public Result<List<DrinkCategory>> searchCategories(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam String keyword) {
        try {
            List<DrinkCategory> categories = drinkCategoryService.searchCategoriesByName(keyword);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("搜索分类失败，keyword: {}", keyword, e);
            return Result.error("搜索分类失败");
        }
    }

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    @Operation(summary = "获取分类树", description = "获取完整的分类树结构")
    public Result<List<DrinkCategory>> getCategoryTree() {
        try {
            List<DrinkCategory> categoryTree = drinkCategoryService.getCategoryTree();
            return Result.success(categoryTree);
        } catch (Exception e) {
            log.error("获取分类树失败", e);
            return Result.error("获取分类树失败");
        }
    }

    /**
     * 根据ID获取分类详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情", description = "根据ID获取分类详细信息")
    public Result<DrinkCategory> getCategoryById(
            @Parameter(description = "分类ID", required = true)
            @PathVariable Long id) {
        try {
            DrinkCategory category = drinkCategoryService.getById(id);
            if (category == null || category.getStatus() == 0) {
                return Result.error("分类不存在");
            }
            return Result.success(category);
        } catch (Exception e) {
            log.error("获取分类详情失败，id: {}", id, e);
            return Result.error("获取分类详情失败");
        }
    }
}