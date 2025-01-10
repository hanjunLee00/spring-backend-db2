package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 인터페이스가 아님에도 스프링 빈으로 등록되는 이유 (과정)
 * 1. MyBatis 스프링 연동 모둘이 @Mapper 조회
 * 2. ItemMapper를 찾아 동적 프록시 객체(구현체) 생성
 * 3. 생성된 동적 프록시 객체를 스프링 빈으로 등록
 *
 * <<인터페이스 하나로 깔끔하게 사용>>
 */
@Mapper
public interface ItemMapper {

    void save(Item item);

    void update(@Param("id") Long id, @Param("updateParam")ItemUpdateDto updateDto);

    List<Item> findAll(ItemSearchCond itemSearch);

    Optional<Item> findById(Long id);

}
