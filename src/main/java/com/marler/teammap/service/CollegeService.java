package com.marler.teammap.service;

import com.marler.teammap.pojo.College;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CollegeService {
    List<College> findAll();
}
