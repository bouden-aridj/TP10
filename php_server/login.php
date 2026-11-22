<?php
require_once 'config.php';

$data = getJsonInput();

$username = trim($data['username'] ?? '');
$password = trim($data['password'] ?? $data['password_hash'] ?? '');

if (empty($username) || empty($password)) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Username and password are required'
    ]);
    exit();
}

if (strlen($password) !== 64) {
    $password_hash = hash('sha256', $password);
} else {
    $password_hash = $password;
}

$stmt = $pdo->prepare("SELECT id, username, password_hash, full_name FROM users WHERE username = ? AND password_hash = ?");
$stmt->execute([$username, $password_hash]);
$user = $stmt->fetch();

if ($user) {
    echo json_encode([
        'status' => 'success',
        'message' => 'Login successful',
        'user' => [
            'id' => (int)$user['id'],
            'username' => $user['username'],
            'full_name' => $user['full_name']
        ]
    ]);
} else {
    http_response_code(401);
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid username or password'
    ]);
}
