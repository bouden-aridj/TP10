# TP10 PHP REST API & MySQL Setup Instructions

## 1. Local Database Setup (MySQL)
1. Start your local Apache & MySQL server (e.g. using XAMPP, WAMP, or MySQL Service).
2. Open phpMyAdmin or MySQL CLI (`mysql -u root`).
3. Execute the contents of `schema.sql`:
   ```bash
   mysql -u root -p < schema.sql
   ```
   This creates the database `tp10_db` and pre-populates sample data and a default user (`admin` / `password123`).

## 2. Hosting the PHP REST API
You can run the PHP API locally using PHP's built-in CLI web server:

```bash
cd php_server
php -S 0.0.0.0:8000
```

When running Android Emulator, access the API at:
`http://10.0.2.2:8000/`

Or if hosted under XAMPP `htdocs/tp10_api/`:
`http://10.0.2.2/tp10_api/`

## 3. Testing REST Endpoints

### Login Endpoint (`POST /login.php`)
```bash
curl -X POST http://localhost:8000/login.php \
  -H "Content-Type: application/json" \
  -d '{"username":"admin", "password":"password123"}'
```

### Register Endpoint (`POST /register.php`)
```bash
curl -X POST http://localhost:8000/register.php \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser", "password":"mysecretpassword", "full_name":"Jane Doe"}'
```

### List Appointments (`GET /list_appointments.php?user_id=1`)
```bash
curl -X GET "http://localhost:8000/list_appointments.php?user_id=1"
```

### Add Appointment (`POST /add_appointment.php`)
```bash
curl -X POST http://localhost:8000/add_appointment.php \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": 1,
    "first_name": "Emma",
    "last_name": "Watson",
    "email": "emma@example.com",
    "phone": "555-9876",
    "age": 32,
    "sex": "Female",
    "doctor_name": "Dr. House",
    "specialty": "Neurology",
    "appointment_date": "2025-05-10",
    "appointment_time": "11:00:00",
    "status": "Scheduled",
    "notes": "Headache evaluation"
  }'
```

### Delete Appointment (`POST /delete_appointment.php`)
```bash
curl -X POST http://localhost:8000/delete_appointment.php \
  -H "Content-Type: application/json" \
  -d '{"id": 1}'
```

## 4. Web Access Page (Part D)
Navigate to `http://localhost:8000/index.php` in any web browser to view the appointments stored in MySQL with dark theme styling.
