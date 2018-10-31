package com.pojo;

import com.util.PropertiesUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductWithBLOBs extends Product {
    private String subImages;

    private String detail;

}