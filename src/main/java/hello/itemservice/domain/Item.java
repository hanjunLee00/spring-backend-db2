package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity //테이블과 관리되는 엔티티
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //테이블의 autoincrement 전략
    private Long id;

    @Column(name = "item_name", length= 10) //컬럼명과 객체명이 일치하면 작성 필요 X + 객체 필드를 카멜 케이스로 자동 변환해준다 = 사실상 생략 가능 애노테이션
    private String itemName;
    private Integer price;
    private Integer quantity;

    //JPA 에서 기본 생성자는 필수이다.
    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
