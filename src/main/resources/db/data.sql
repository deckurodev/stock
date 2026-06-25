-- 앱 기동 시 자동 적재되는 초기 데이터 (spring.sql.init).
-- 재기동해도 중복/오류가 없도록 ON CONFLICT 로 멱등 처리한다.

insert into product (name, created_at, updated_at)
values ('아메리카노', now(), now()),
       ('카페라떼', now(), now()),
       ('콜드브루', now(), now())
on conflict (name) do nothing;

insert into stock (product_id, quantity, created_at, updated_at)
select p.id, v.quantity, now(), now()
from product p
join (values ('아메리카노', 100),
             ('카페라떼', 50),
             ('콜드브루', 0)) as v(name, quantity) on p.name = v.name
on conflict (product_id) do nothing;
