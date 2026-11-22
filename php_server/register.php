<?php
require_once 'config.php';

$data = getJsonInput();

$username = trim($data['username'] ?? '');
$password = trim($data['password'] ?? $data['password_hash'] ?? '');
$full_name = trim($data['full_name'] ?? '');

if (empty($username) || empty($password) || empty($full_name)) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Username, password and full name are required'
    ]);
    exit();
}

// Check if username already exists
$stmt = $pdo->prepare("SELECT id FROM users WHERE username = ?");
$stmt->execute([$username]);
if ($stmt->fetch()) {
    http_response_code(409);
    echo json_encode([
        'status' => 'error',
        'message' => 'Username is already taken'
    ]);
    exit();
}

// Hash password using SHA-256 if not already hashed
if (strlen($password) !== 64) {
    $password_hash = hash('sha256', $password);
} else {
    $password_hash = $password;
}

$insertStmt = $pdo->prepare("INSERT INTO users (username, password_hash, full_name) VALUES (?, ?, ?)");
if ($insertStmt->execute([$username, $password_hash, $full_name])) {
    $userId = (int)$pdo->lastInsertId();
    echo json_encode([
        'status' => 'success',
        'message' => 'User registered successfully',
        'user' => [
            'id' => $userId,
            'username' => $username,
            'full_name' => $full_name
        ]
    ]);
} else {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Registration failed'
    ]);
}
