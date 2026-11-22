<?php
require_once 'config.php';

$userId = isset($_GET['user_id']) ? (int)$_GET['user_id'] : 0;
$data = getJsonInput();
if ($userId <= 0 && isset($data['user_id'])) {
    $userId = (int)$data['user_id'];
}

if ($userId > 0) {
    $stmt = $pdo->prepare("SELECT * FROM appointments WHERE user_id = ? ORDER BY appointment_date DESC, appointment_time DESC");
    $stmt->execute([$userId]);
} else {
    $stmt = $pdo->prepare("SELECT * FROM appointments ORDER BY appointment_date DESC, appointment_time DESC");
    $stmt->execute();
}

$appointments = $stmt->fetchAll();

foreach ($appointments as &$app) {
    $app['id'] = (int)$app['id'];
    $app['user_id'] = (int)$app['user_id'];
    $app['age'] = (int)$app['age'];
}

echo json_encode([
    'status' => 'success',
    'count' => count($appointments),
    'data' => $appointments
]);
