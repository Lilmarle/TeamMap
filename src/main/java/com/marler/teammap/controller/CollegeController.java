package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import com.marler.teammap.pojo.College;
import com.marler.teammap.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/colleges")
public class CollegeController {

    @Autowired
    private CollegeService collegeService;

    /**
     * 获取所有学院列表
     */
    @GetMapping
    public Result<List<College>> list() {
        List<College> colleges = collegeService.findAll();
        return Result.success(colleges);
    }
}
