<?php
require_once 'config.php';

$data = getJsonInput();

$userId = (int)($data['user_id'] ?? 0);
$firstName = trim($data['first_name'] ?? '');
$lastName = trim($data['last_name'] ?? '');
$email = trim($data['email'] ?? '');
$phone = trim($data['phone'] ?? '');
$age = (int)($data['age'] ?? 0);
$sex = trim($data['sex'] ?? 'Male');
$doctorName = trim($data['doctor_name'] ?? '');
$specialty = trim($data['specialty'] ?? 'General Practice');
$date = trim($data['appointment_date'] ?? '');
$time = trim($data['appointment_time'] ?? '');
$status = trim($data['status'] ?? 'Scheduled');
$notes = trim($data['notes'] ?? '');

if ($userId <= 0 || empty($firstName) || empty($lastName) || empty($doctorName) || empty($date) || empty($time)) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Missing required appointment fields'
    ]);
    exit();
}

$sql = "INSERT INTO appointments (user_id, first_name, last_name, email, phone, age, sex, doctor_name, specialty, appointment_date, appointment_time, status, notes)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
$stmt = $pdo->prepare($sql);
$success = $stmt->execute([$userId, $firstName, $lastName, $email, $phone, $age, $sex, $doctorName, $specialty, $date, $time, $status, $notes]);

if ($success) {
    $newId = (int)$pdo->lastInsertId();
    echo json_encode([
        'status' => 'success',
        'message' => 'Appointment created successfully',
        'id' => $newId
    ]);
} else {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to create appointment'
    ]);
}
