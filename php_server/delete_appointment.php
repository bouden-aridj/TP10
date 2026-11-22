<?php
require_once 'config.php';

$data = getJsonInput();
$id = isset($_GET['id']) ? (int)$_GET['id'] : 0;
if ($id <= 0 && isset($data['id'])) {
    $id = (int)$data['id'];
}

if ($id <= 0) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Appointment ID is required'
    ]);
    exit();
}

$stmt = $pdo->prepare("DELETE FROM appointments WHERE id = ?");
if ($stmt->execute([$id])) {
    echo json_encode([
        'status' => 'success',
        'message' => 'Appointment deleted successfully'
    ]);
} else {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to delete appointment'
    ]);
}
