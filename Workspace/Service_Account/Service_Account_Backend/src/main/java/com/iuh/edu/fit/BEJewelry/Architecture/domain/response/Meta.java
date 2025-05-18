package com.iuh.edu.fit.BEJewelry.Architecture.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meta {
    private int page;
    private int pageSize;
    private int pages;
    private long total;
}