
-- Crear la tabla de usuarios (modificada para incluir el nombre)
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    pin INT NOT NULL,
    saldo DOUBLE NOT NULL
);

-- Crear la tabla de historico
CREATE TABLE IF NOT EXISTS historico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    tipo_operacion VARCHAR(50) NOT NULL,
    cantidad DOUBLE,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

/*
-- Crear la tabla de Historico Contraseñas
CREATE TABLE IF NOT EXISTS historicoContrasenias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    contraseniaAnterior INT,
    contraseniaNueva INT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
*/

-- Insertar datos de ejemplo
-- Insertar datos de ejemplo en usuarios
INSERT INTO usuarios (nombre, pin, saldo) VALUES 
('Juan Perez', 1234, 1000.0),
('Ana Ramirez', 5678, 2500.0),
('Carlos Gomez', 9012, 500.0),
('Marta Torres', 3456, 750.0),
('Luisa Fernandez', 7890, 3000.0);

-- Insertar datos de ejemplo en historico (asumiendo que los IDs de los usuarios coinciden con los valores insertados anteriormente)
-- Juan Perez hizo un depósito de 200.0
INSERT INTO historico (usuario_id, tipo_operacion, cantidad) VALUES (1, 'depósito', 1000.0);
INSERT INTO historico (usuario_id, tipo_operacion, cantidad) VALUES (2, 'depósito', 2500.0);
INSERT INTO historico (usuario_id, tipo_operacion, cantidad) VALUES (3, 'depósito', 500.0);
INSERT INTO historico (usuario_id, tipo_operacion, cantidad) VALUES (4, 'depósito', 750.0);
INSERT INTO historico (usuario_id, tipo_operacion, cantidad) VALUES (5, 'depósito', 3000.0);


