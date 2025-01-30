--Функция для расчётка количества поданных заявок по направлениям за указанные даты

Create or replace function application_count_by_direction(
        start_date DATE,
        end_date DATE
)
Returns table(
    direction_name varchar(255),
    total_applications bigint
) AS $$
Begin
    Return query
    Select
        d.name AS direction_name,
        COUNT(a.id) AS total_applications
    From applications a
    Join directions d
    ON a.direction_id = d.id
    Where a.submission_date BETWEEN start_date AND end_date
    Group by d.name;
end;
$$ language plpgsql;

--Функция для расчётка количества поданных заявок по направлениям за указанные даты в разрезе по дням
CREATE OR REPLACE FUNCTION application_count_by_day_and_direction(
    start_date DATE,
    end_date DATE
)
RETURNS TABLE(
    submission_date DATE,
    direction_name VARCHAR(255),
    total_applications BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        CAST(a.submission_date AS DATE) AS submission_date,
        d.name AS direction_name,
        COUNT(a.id) AS total_applications
    FROM applications a
    JOIN directions d
        ON a.direction_id = d.id
    WHERE a.submission_date BETWEEN start_date AND end_date
    GROUP BY CAST(a.submission_date AS DATE), d.name
    ORDER BY CAST(a.submission_date AS DATE), d.name;
END;
$$ LANGUAGE plpgsql;