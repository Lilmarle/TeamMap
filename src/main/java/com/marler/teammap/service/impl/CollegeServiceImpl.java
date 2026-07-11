package com.marler.teammap.service.impl;

import com.marler.teammap.mapper.CollegeMapper;
import com.marler.teammap.pojo.College;
import com.marler.teammap.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollegeServiceImpl implements CollegeService {

    @Autowired
    private CollegeMapper collegeMapper;

    @Override
    public List<College> findAll() {
        return collegeMapper.selectAll();
    }
}
