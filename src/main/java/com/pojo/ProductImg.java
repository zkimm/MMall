package com.pojo;

import com.util.PropertiesUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductImg {
    private Integer id;

    private Integer productId;

    private String imgDesc;

    private String imgAddr;

    private Date createTime;

    private Date updateTime;

}