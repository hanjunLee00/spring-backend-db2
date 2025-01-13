package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
//JPA에서 발생하는 예외는 스프링과 통합되지 않음 => 애노테이션으로 예외 변환 필요(AOP) 프록시 활용
@Repository //컴포넌트 스캔의 대상, 예외 변환 AOP의 적용 대상 => AOP 프록시를 만들고 예외를 변환시켜 호출한 쪽으로 넘겨줌
@Transactional //@Transactional 애노테이션도 동일
public class JpaItemRepository implements ItemRepository {

    private final EntityManager em;

    public JpaItemRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        //JPA가 내부에 로직으로 인하여 @Transactional 으로 커밋되는 순간에 업데이트 쿼리를 만들어 DB에 날린다.
        //롤백되면 실행하지 않음 & 변경사항이 없어도 실행하지 않음 => 성능 최적화
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id); //하나를 조회할 때
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {

        //jpql에서도 여전히 남아있는 동적 쿼리의 복잡성 => Querydsl 사용하여 해결
        String jpql = "select i from Item i"; //여러개를 조회할 때

        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
        }
        log.info("jpql={}", jpql);
        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }

        return query.getResultList();
    }
}
