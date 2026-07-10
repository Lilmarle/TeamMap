package com.marler.teammap.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class College {
    private Long id;
    private String fullName;
    private String shortName;
}
