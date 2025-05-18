package com.iuh.edu.fit.BEJewelry.Architecture.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultPaginationDTO {
    private Meta meta;
    private Object result;
}