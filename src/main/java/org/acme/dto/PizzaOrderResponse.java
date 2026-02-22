package org.acme.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PizzaOrderResponse {

    private Long id;
    private String state;
    private String description;
    private Integer orderNumber;

}
