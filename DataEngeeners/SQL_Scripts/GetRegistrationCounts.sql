--Функция для расчёта витрины по успешным и неуспешным заявкам, поданным за определенный период
CREATE OR REPLACE FUNCTION GetRegistrationCounts(start_date DATE, end_date DATE)
RETURNS TABLE (successful_registrations bigint, unsuccessful_registrations bigint) AS $$
BEGIN
    RETURN QUERY
    SELECT
        COUNT(CASE WHEN status = 'Принята' THEN 1 END) AS successful_registrations,
        COUNT(CASE WHEN status = 'Отклонена' THEN 1 END) AS unsuccessful_registrations
    FROM applications
    WHERE submission_date BETWEEN start_date AND end_date;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM GetRegistrationCounts('2025-01-01', '2025-01-01');


--Функция для расчёта витрины по успешным и неуспешным заявкам, поданным за определенный период в разрезе по дням
CREATE OR REPLACE FUNCTION GetRegistrationCountsByDate(start_date DATE, end_date DATE)
RETURNS TABLE (registration_date DATE, successful_registrations bigint, unsuccessful_registrations bigint) AS $$
BEGIN
    RETURN QUERY
    SELECT
        submission_date::DATE AS registration_date,
        COUNT(CASE WHEN status = 'Принята' THEN 1 END) AS successful_registrations,
        COUNT(CASE WHEN status = 'Отклонена' THEN 1 END) AS unsuccessful_registrations
    FROM applications
    WHERE submission_date BETWEEN start_date AND end_date
    GROUP BY submission_date
    ORDER BY submission_date;
END;
$$ LANGUAGE plpgsql;


SELECT * FROM GetRegistrationCountsByDate('2024-01-01', '2025-01-20');

