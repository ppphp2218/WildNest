package com.HuangYe.WildNest.controller.admin;

import com.HuangYe.WildNest.common.Result;
import com.HuangYe.WildNest.controller.BaseController;
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

import jakarta.validation.Valid;
import java.util.List;

/**
 * 管理员酒品管理控制器
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@RestController
@RequestMapping("/admin/drinks")
@RequiredArgsConstructor
@Validated
@Tag(name = "管理员酒品管理", description = "管理员酒品管理相关接口")

public class AdminDrinkController extends BaseController {

    private final DrinkService drinkService;

    /**
     * 管理员分页查询酒品（包含下架的）
     */
    @GetMapping
    @Operation(summary = "管理员获取酒品列表", description = "分页查询所有酒品，包含下架的酒品")
    public Result<IPage<Drink>> getAllDrinks(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "分类ID")
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "可售状态")
            @RequestParam(required = false) Integer isAvailable) {
        try {
            // 这里需要创建一个管理员专用的查询方法，可以查询所有状态的酒品
            IPage<Drink> drinks = drinkService.getAvailableDrinks(current, size, categoryId);
            return Result.success(drinks);
        } catch (Exception e) {
            log.error("管理员获取酒品列表失败", e);
            return Result.error("获取酒品列表失败");
        }
    }

    /**
     * 创建酒品
     */
    @PostMapping
    @Operation(summary = "创建酒品", description = "创建新的酒品")
    public Result<String> createDrink(
            @Parameter(description = "酒品信息", required = true)
            @Valid @RequestBody Drink drink) {
        try {
            boolean success = drinkService.createDrink(drink);
            if (success) {
                return Result.success("创建酒品成功");
            } else {
                return Result.error("创建酒品失败");
            }
        } catch (Exception e) {
            log.error("创建酒品失败", e);
            return Result.error("创建酒品失败");
        }
    }

    /**
     * 更新酒品
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新酒品", description = "更新酒品信息")
    public Result<String> updateDrink(
            @Parameter(description = "酒品ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "酒品信息", required = true)
            @Valid @RequestBody Drink drink) {
        try {
            drink.setId(id);
            boolean success = drinkService.updateDrink(drink);
            if (success) {
                return Result.success("更新酒品成功");
            } else {
                return Result.error("更新酒品失败");
            }
        } catch (Exception e) {
            log.error("更新酒品失败，id: {}", id, e);
            return Result.error("更新酒品失败");
        }
    }

    /**
     * 删除酒品
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除酒品", description = "软删除酒品（设置为不可售）")
    public Result<String> deleteDrink(
            @Parameter(description = "酒品ID", required = true)
            @PathVariable Long id) {
        try {
            boolean success = drinkService.deleteDrink(id);
            if (success) {
                return Result.success("删除酒品成功");
            } else {
                return Result.error("删除酒品失败");
            }
        } catch (Exception e) {
            log.error("删除酒品失败，id: {}", id, e);
            return Result.error("删除酒品失败");
        }
    }

    /**
     * 上架/下架酒品
     */
    @PutMapping("/{id}/availability")
    @Operation(summary = "上架/下架酒品", description = "修改酒品的可售状态")
    public Result<String> updateDrinkAvailability(
            @Parameter(description = "酒品ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "可售状态（1-可售，0-下架）", required = true)
            @RequestParam Integer isAvailable) {
        try {
            boolean success = drinkService.updateDrinkAvailability(id, isAvailable);
            if (success) {
                String action = isAvailable == 1 ? "上架" : "下架";
                return Result.success(action + "酒品成功");
            } else {
                return Result.error("操作失败");
            }
        } catch (Exception e) {
            log.error("更新酒品可售状态失败，id: {}, isAvailable: {}", id, isAvailable, e);
            return Result.error("操作失败");
        }
    }

    /**
     * 设置/取消推荐酒品
     */
    @PutMapping("/{id}/featured")
    @Operation(summary = "设置/取消推荐酒品", description = "修改酒品的推荐状态")
    public Result<String> updateDrinkFeatured(
            @Parameter(description = "酒品ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "推荐状态（1-推荐，0-取消推荐）", required = true)
            @RequestParam Integer isFeatured) {
        try {
            boolean success = drinkService.updateDrinkFeatured(id, isFeatured);
            if (success) {
                String action = isFeatured == 1 ? "设置" : "取消";
                return Result.success(action + "推荐成功");
            } else {
                return Result.error("操作失败");
            }
        } catch (Exception e) {
            log.error("更新酒品推荐状态失败，id: {}, isFeatured: {}", id, isFeatured, e);
            return Result.error("操作失败");
        }
    }

    /**
     * 批量更新酒品排序
     */
    @PutMapping("/batch/sort-order")
    @Operation(summary = "批量更新酒品排序", description = "批量修改酒品的排序值")
    public Result<String> batchUpdateSortOrder(
            @Parameter(description = "酒品ID列表", required = true)
            @RequestParam List<Long> drinkIds,
            @Parameter(description = "对应的排序值列表", required = true)
            @RequestParam List<Integer> sortOrders) {
        try {
            boolean success = drinkService.batchUpdateSortOrder(drinkIds, sortOrders);
            if (success) {
                return Result.success("批量更新排序成功");
            } else {
                return Result.error("批量更新排序失败");
            }
        } catch (Exception e) {
            log.error("批量更新酒品排序失败", e);
            return Result.error("批量更新排序失败");
        }
    }
}