import traceback
from datetime import datetime
from faker import Faker
import asyncpg
import asyncio
import os
from dotenv import load_dotenv
from faker.generator import random

load_dotenv()  # Загружаем переменные из .env

def fake_phone_number() -> str:
    """Функция генерации рандомного номера телефона"""
    return f'79{random.randint(100000000, 999999999)}'

async def generate_data_user(count:int=1, role:str='visitor') -> None:
    conn = None
    try:
        fake = Faker('ru_RU')
        # Соединение с базой данных
        conn = await asyncpg.connect(
            user=os.getenv('DB_USER'),
            password=os.getenv('DB_PASSWORD'),
            database=os.getenv('DB_NAME'),
            host=os.getenv('DB_HOST'),
            port=os.getenv('DB_PORT')
        )

        # Генерация и вставка фейковых записей
        for _ in range(count):
            first_name = fake.first_name()
            last_name = fake.last_name()
            city = fake.city()
            phone_number = fake_phone_number()
            email = fake.email()
            role = role
            telegram_id = random.randint(100000000, 999999999)
            chat_id = random.randint(100000000, 999999999)
            await conn.execute(''' 
                INSERT INTO users(first_name, last_name, city, phone_number, email, role, telegram_id, chat_id, updated_at, created_at) 
                VALUES($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
            ''', first_name, last_name, city, phone_number, email, role, telegram_id, chat_id, datetime.now(), fake.date_this_year())

    except asyncpg.exceptions.UniqueViolationError:
        print('Не уникальное значение, пропускаем')
    except Exception as e:
        print(f"Ошибка: {traceback.format_exc()}")
    finally:
        if conn:
            await conn.close()
            print("Соединение с базой данных закрыто")

async def generate_data_applications():
    conn = None
    try:
        # Соединение с базой данных
        conn = await asyncpg.connect(
            user=os.getenv('DB_USER'),
            password=os.getenv('DB_PASSWORD'),
            database=os.getenv('DB_NAME'),
            host=os.getenv('DB_HOST'),
            port=os.getenv('DB_PORT')
        )
        fake = Faker('ru_RU')

        # Получаем всех пользователей с ролями 'external_user' или 'candidate'
        users = await conn.fetch(''' 
            SELECT id FROM users 
            WHERE role IN ('external_user', 'candidate')
        ''')

        # Получаем все направления
        directions = await conn.fetch('SELECT id FROM directions WHERE is_active = TRUE')

        # Генерация и вставка заявок
        for user in users:
            user_id = user['id']
            direction_id = random.choice(directions)['id']  # Выбираем случайное направление
            application_type = random.choice(['apply', 'pre_apply'])  # Случайный тип заявки
            if application_type == 'apply':  # Если apply, то текущий год
                submission_date = fake.date_this_year()
            else:  # Если pre_apply, то прошлый год
                submission_date = fake.date_this_year(before_today=True)
            status = random.choice(['Принята', 'Отклонена', 'На рассмотрении'])  # Пример статуса

            await conn.execute(''' 
                INSERT INTO applications(user_id, direction_id, type, submission_date, status)
                VALUES($1, $2, $3, $4, $5)
            ''', user_id, direction_id, application_type, submission_date, status)

    except Exception as e:
        print(f"Ошибка: {traceback.format_exc()}")
    finally:
        if conn:
            await conn.close()
            print("Соединение с базой данных закрыто")

# async def generate_data_parallel():
#     # Генерация данных пользователей и заявок одновременно
#     await asyncio.gather(
#         generate_data_user(count=867,role='visitor'),
#         generate_data_user(count=1070, role='candidate'),
#         generate_data_user(count=1500, role='external_user'),
#     )
#     await generate_data_applications()

asyncio.run(generate_data_applications())
