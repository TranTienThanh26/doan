CREATE TABLE IF NOT EXISTS gio_hang (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    san_pham_id INT NOT NULL,
    size VARCHAR(20),
    mau VARCHAR(20),
    so_luong INT NOT NULL DEFAULT 1,
    UNIQUE KEY (username, san_pham_id, size, mau)
);
