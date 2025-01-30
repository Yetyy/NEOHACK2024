--View для получения общего количества заявок по направлениям
Create
or replace view application_count_by_direction AS
Select d.name      AS direction_name,
       Count(a.id) AS application_count
From applications a
         Join directions d
              on a.direction_id = d.id
group by d.name
order by application_count DESC;

select *
from application_count_by_direction

