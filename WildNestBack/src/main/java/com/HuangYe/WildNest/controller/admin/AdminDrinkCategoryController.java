package com.HuangYe.WildNest.controller.admin;

import com.HuangYe.WildNest.common.Result;
import com.HuangYe.WildNest.controller.BaseController;
import com.HuangYe.WildNest.entity.DrinkCategory;
import com.HuangYe.WildNest.service.DrinkCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 管理员分类管理控制器
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "管理员分类管理", description = "管理员分类管理相关接口")

public class AdminDrinkCategoryController extends BaseController {

    private final DrinkCategoryService drinkCategoryService;

    /**
     * 获取所有分类（包含禁用的）
     */
    @GetMapping
    @Operation(summary = "管理员获取所有分类", description = "获取所有分类，包含禁用的分类")
    public Result<List<DrinkCategory>> getAllCategories() {
        try {
            List<DrinkCategory> categories = drinkCategoryService.list();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("管理员获取所有分类失败", e);
            return Result.error("获取分类失败");
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
            if (category == null) {
                return Result.error("分类不存在");
            }
            return Result.success(category);
        } catch (Exception e) {
            log.error("获取分类详情失败，id: {}", id, e);
            return Result.error("获取分类详情失败");
        }
    }

    /**
     * 创建分类
     */
    @PostMapping
    @Operation(summary = "创建分类", description = "创建新的酒品分类")
    public Result<String> createCategory(
            @Parameter(description = "分类信息", required = true)
            @Valid @RequestBody DrinkCategory category) {
        try {
            boolean success = drinkCategoryService.createCategory(category);
            if (success) {
                return Result.success("创建分类成功");
            } else {
                return Result.error("创建分类失败");
            }
        } catch (Exception e) {
            log.error("创建分类失败", e);
            return Result.error("创建分类失败");
        }
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新分类", description = "更新分类信息")
    public Result<String> updateCategory(
            @Parameter(description = "分类ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "分类信息", required = true)
            @Valid @RequestBody DrinkCategory category) {
        try {
            category.setId(id);
            boolean success = drinkCategoryService.updateCategory(category);
            if (success) {
                return Result.success("更新分类成功");
            } else {
                return Result.error("更新分类失败");
            }
        } catch (Exception e) {
            log.error("更新分类失败，id: {}", id, e);
            return Result.error("更新分类失败");
        }
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类", description = "软删除分类（设置为禁用状态）")
    public Result<String> deleteCategory(
            @Parameter(description = "分类ID", required = true)
            @PathVariable Long id) {
        try {
            boolean success = drinkCategoryService.deleteCategory(id);
            if (success) {
                return Result.success("删除分类成功");
            } else {
                return Result.error("删除分类失败，可能存在子分类");
            }
        } catch (Exception e) {
            log.error("删除分类失败，id: {}", id, e);
            return Result.error("删除分类失败");
        }
    }

    /**
     * 启用/禁用分类
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "启用/禁用分类", description = "修改分类的启用状态")
    public Result<String> updateCategoryStatus(
            @Parameter(description = "分类ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "状态（1-启用，0-禁用）", required = true)
            @RequestParam Integer status) {
        try {
            boolean success = drinkCategoryService.updateCategoryStatus(id, status);
            if (success) {
                String action = status == 1 ? "启用" : "禁用";
                return Result.success(action + "分类成功");
            } else {
                return Result.error("操作失败");
            }
        } catch (Exception e) {
            log.error("更新分类状态失败，id: {}, status: {}", id, status, e);
            return Result.error("操作失败");
        }
    }

    /**
     * 调整分类排序
     */
    @PutMapping("/{id}/sort-order")
    @Operation(summary = "调整分类排序", description = "修改分类的排序值")
    public Result<String> updateCategorySortOrder(
            @Parameter(description = "分类ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "排序值", required = true)
            @RequestParam Integer sortOrder) {
        try {
            boolean success = drinkCategoryService.updateCategorySortOrder(id, sortOrder);
            if (success) {
                return Result.success("调整排序成功");
            } else {
                return Result.error("调整排序失败");
            }
        } catch (Exception e) {
            log.error("调整分类排序失败，id: {}, sortOrder: {}", id, sortOrder, e);
            return Result.error("调整排序失败");
        }
    }
}