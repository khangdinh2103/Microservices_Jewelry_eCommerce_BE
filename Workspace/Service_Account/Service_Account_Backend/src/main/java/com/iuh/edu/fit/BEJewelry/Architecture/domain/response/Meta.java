package com.iuh.edu.fit.BEJewelry.Architecture.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meta {
    private int page;
    private int pageSize;
    private int pages;
    private long total;
}
