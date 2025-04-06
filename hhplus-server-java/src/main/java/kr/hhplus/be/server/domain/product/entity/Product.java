package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
public class Product {

    @Id
    private long id;
    private long seller_id;
    private String name;
    private long price;
    private int stock;
    private String category;
    private LocalDateTime efctStDt;
    private LocalDateTime efctFnsDt;
    private LocalDateTime sysCretDt;
    private LocalDateTime sysChgDt;
}
