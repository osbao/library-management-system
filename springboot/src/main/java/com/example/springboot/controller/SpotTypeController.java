package com.example.springboot.controller;

import cn.hutool.core.collection.CollUtil;
import com.example.springboot.common.Result;
import com.example.springboot.controller.request.*;
import com.example.springboot.entity.SpotType;
import com.example.springboot.service.ISpotTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/spotType")
public class SpotTypeController {

    @Autowired
    ISpotTypeService spotTypeService;

    @PostMapping("/save")
    public Result save(@RequestBody SpotType obj) {
        spotTypeService.save(obj);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        spotTypeService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody SpotType obj) {
        spotTypeService.update(obj);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        SpotType obj = spotTypeService.getById(id);
        return Result.success(obj);
    }

    @GetMapping("/list")
    public Result list() {
        List<SpotType> list = spotTypeService.list();
        return Result.success(list);
    }

    @GetMapping("/tree")
    public Result tree() {
        List<SpotType> list = spotTypeService.list();
        return Result.success(createTree(null, list));
    }

    private List<SpotType> createTree(Integer pid, List<SpotType> types) {
        List<SpotType> treeList = new ArrayList<>();
        for (SpotType type : types) {
            if (pid == null) {
                if (type.getPid() == null) {
                    treeList.add(type);
                    type.setChildren(createTree(type.getId(), types));
                }
            } else {
                if (pid.equals(type.getPid())) {
                    treeList.add(type);
                    type.setChildren(createTree(type.getId(), types));
                }
            }
            if (CollUtil.isEmpty(type.getChildren())) {
                type.setChildren(null);
            }
        }
        return treeList;
    }

    @GetMapping("/page")
    public Result page(CategoryPageRequest baseRequest) {
        return Result.success(spotTypeService.page(baseRequest));
    }
}