<?php
// Part D — Web Access: PHP Web Page displaying appointments stored in MySQL
$host = 'localhost';
$db_name = 'tp10_db';
$username = 'root';
$password = '';

$appointments = [];
$errorMsg = null;

try {
    $pdo = new PDO("mysql:host=$host;dbname=$db_name;charset=utf8mb4", $username, $password, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
    ]);

    $stmt = $pdo->query("SELECT a.*, u.full_name as user_full_name FROM appointments a LEFT JOIN users u ON a.user_id = u.id ORDER BY a.appointment_date DESC, a.appointment_time DESC");
    $appointments = $stmt->fetchAll();
} catch (Exception $e) {
    $errorMsg = $e->getMessage();
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Medical Appointments Web Portal (TP10)</title>
    <style>
        body {
            background-color: #121116;
            color: #E5E1EC;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 24px;
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
            padding-bottom: 16px;
            border-bottom: 2px solid #D0B5FF;
        }
        h1 {
            color: #D0B5FF;
            margin-bottom: 8px;
        }
        p.subtitle {
            color: #CAC4D0;
            margin-top: 0;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background-color: #1D1B20;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
        }
        th, td {
            padding: 14px 16px;
            text-align: left;
            border-bottom: 1px solid #2B2930;
        }
        th {
            background-color: #2B2930;
            color: #D0B5FF;
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 0.8px;
        }
        tr:hover {
            background-color: #25232A;
        }
        .badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: bold;
        }
        .badge-scheduled { background-color: #D0B5FF; color: #381E72; }
        .badge-confirmed { background-color: #B6EFB5; color: #00390A; }
        .badge-completed { background-color: #C8BFFF; color: #2D126B; }
        .badge-cancelled { background-color: #FFB4AB; color: #690005; }

        .error-banner {
            background-color: #690005;
            color: #FFB4AB;
            padding: 16px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Medical Appointments Portal</h1>
            <p class="subtitle">MySQL Database Web View — TP10 Continuation</p>
        </div>

        <?php if ($errorMsg): ?>
            <div class="error-banner">
                <strong>Database Error:</strong> <?php echo htmlspecialchars($errorMsg); ?>
            </div>
        <?php endif; ?>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Patient Name</th>
                    <th>Contact</th>
                    <th>Age / Sex</th>
                    <th>Doctor</th>
                    <th>Specialty</th>
                    <th>Date & Time</th>
                    <th>Status</th>
                    <th>Notes</th>
                </tr>
            </thead>
            <tbody>
                <?php if (empty($appointments)): ?>
                    <tr>
                        <td colspan="9" style="text-align: center; color: #CAC4D0; padding: 30px;">
                            No appointments found in MySQL database.
                        </td>
                    </tr>
                <?php else: ?>
                    <?php foreach ($appointments as $app): ?>
                        <tr>
                            <td>#<?php echo htmlspecialchars($app['id']); ?></td>
                            <td>
                                <strong><?php echo htmlspecialchars($app['first_name'] . ' ' . $app['last_name']); ?></strong>
                            </td>
                            <td>
                                <?php echo htmlspecialchars($app['email']); ?><br>
                                <small style="color:#CAC4D0;"><?php echo htmlspecialchars($app['phone']); ?></small>
                            </td>
                            <td><?php echo htmlspecialchars($app['age']); ?> / <?php echo htmlspecialchars($app['sex']); ?></td>
                            <td><?php echo htmlspecialchars($app['doctor_name']); ?></td>
                            <td><?php echo htmlspecialchars($app['specialty']); ?></td>
                            <td>
                                <?php echo htmlspecialchars($app['appointment_date']); ?><br>
                                <small style="color:#D0B5FF;"><?php echo htmlspecialchars($app['appointment_time']); ?></small>
                            </td>
                            <td>
                                <?php
                                    $status = strtolower($app['status']);
                                    $class = 'badge-scheduled';
                                    if ($status === 'confirmed') $class = 'badge-confirmed';
                                    else if ($status === 'completed') $class = 'badge-completed';
                                    else if ($status === 'cancelled') $class = 'badge-cancelled';
                                ?>
                                <span class="badge <?php echo $class; ?>"><?php echo htmlspecialchars($app['status']); ?></span>
                            </td>
                            <td><?php echo htmlspecialchars($app['notes'] ?: '-'); ?></td>
                        </tr>
                    <?php endforeach; ?>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</body>
</html>
