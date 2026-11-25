CREATE TABLE users (
  id CHAR(36) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  document_type VARCHAR(20) NOT NULL,
  document_number VARCHAR(30) NOT NULL,
  phone VARCHAR(30),
  neighborhood VARCHAR(80),
  home_node VARCHAR(20) NOT NULL,
  bio VARCHAR(300) DEFAULT '',
  password_hash VARCHAR(100) NOT NULL,
  created_at BIGINT NOT NULL,
  UNIQUE (document_type, document_number)
);

-- Grafo social de la app: "Conectar" (solicitud + aceptacion), equivalente a "seguir".
-- Un solo registro por pareja de usuarios, sin importar quien envio la solicitud.
-- pair_key = los dos ids ordenados (menor primero) unidos con "|".
CREATE TABLE connections (
  id CHAR(36) PRIMARY KEY,
  pair_key VARCHAR(80) NOT NULL UNIQUE,
  requester_id CHAR(36) NOT NULL,
  receiver_id CHAR(36) NOT NULL,
  status VARCHAR(10) NOT NULL DEFAULT 'PENDING',  -- PENDING | ACCEPTED | REJECTED
  created_at BIGINT NOT NULL,
  responded_at BIGINT NULL
);

CREATE TABLE posts (
  id CHAR(36) PRIMARY KEY,
  author_id CHAR(36) NOT NULL,
  origin_node VARCHAR(20) NOT NULL,
  text VARCHAR(280) NOT NULL,
  created_at BIGINT NOT NULL
);

CREATE TABLE recycle_requests (
  id CHAR(36) PRIMARY KEY,
  generator_id CHAR(36) NOT NULL,
  home_node VARCHAR(20) NOT NULL,
  material VARCHAR(30) NOT NULL,
  weight_kg DOUBLE NOT NULL,
  address VARCHAR(150),
  neighborhood VARCHAR(80),
  availability VARCHAR(100),
  notes VARCHAR(300),
  status VARCHAR(20) NOT NULL,
  recycler_id CHAR(36) NULL,
  updated_at BIGINT NOT NULL
);

CREATE TABLE messages (
  id CHAR(36) PRIMARY KEY,
  sender_id CHAR(36) NOT NULL,
  recipient_id CHAR(36) NOT NULL,
  text VARCHAR(500) NOT NULL,
  sent_at BIGINT NOT NULL
);

CREATE TABLE outbox (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  event_id CHAR(36) NOT NULL,
  type VARCHAR(30) NOT NULL,
  payload JSON NOT NULL,
  target_node VARCHAR(20) NOT NULL,
  status VARCHAR(15) NOT NULL DEFAULT 'PENDING',
  attempts INT NOT NULL DEFAULT 0,
  last_error VARCHAR(200),
  created_at BIGINT NOT NULL,
  -- Idempotencia de salida: un evento no se encola dos veces para el mismo destino.
  UNIQUE KEY uq_outbox_event_target (event_id, target_node)
);

CREATE TABLE replication_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  ts BIGINT NOT NULL,
  node VARCHAR(20) NOT NULL,
  direction VARCHAR(10) NOT NULL,
  event_id CHAR(36),
  type VARCHAR(30),
  peer VARCHAR(20),
  result VARCHAR(20)
);

-- Indices para acelerar las consultas mas comunes.
CREATE INDEX idx_posts_author ON posts (author_id, created_at);
CREATE INDEX idx_conn_requester ON connections (requester_id);
CREATE INDEX idx_conn_receiver ON connections (receiver_id);
CREATE INDEX idx_outbox_status ON outbox (status, id);

-- ---------------------------------------------------------------------------
-- SEED (Opcion 1): usuarios precargados con home_node FIJO.
-- Esta misma tabla users se aplica igual en los 3 nodos. El home_node indica
-- a que nodo "pertenece" cada usuario (donde se decide la autoridad).
-- Coincide con los datos Fake de la app (u1 = usuario actual).
--   central -> u1, u5, u8
--   norte   -> u4, u6, u9, u10
--   sur     -> u2, u3, u7, u11
-- La contrasena es un placeholder hasta implementar la Fase C (login).
-- ---------------------------------------------------------------------------
INSERT INTO users (id, name, email, document_type, document_number, phone, neighborhood, home_node, bio, password_hash, created_at) VALUES
('u1','Cristian Ramirez','cristian@correo.com','CC','1012345678','300 123 4567','Chapinero','central','',       'seed', 0),
('u2','Panaderia El Trigal','trigal@correo.com','OTHER','9001234561','601 345 6789','Chapinero','sur','',        'seed', 0),
('u3','Restaurante La Esquina','laesquina@correo.com','OTHER','9002345672','601 456 7890','Chapinero','sur','',  'seed', 0),
('u4','Maria Fernanda Gomez','mafe@correo.com','CC','52123456','310 222 3344','Usaquen','norte','',              'seed', 0),
('u5','Oficinas Andina S.A.S.','andina@correo.com','OTHER','9003456783','601 567 8901','Usaquen','central','',   'seed', 0),
('u6','Taller Metalicas Soto','soto@correo.com','OTHER','9004567894','601 678 9012','Puente Aranda','norte','',  'seed', 0),
('u7','Colegio San Rafael','sanrafael@correo.com','OTHER','8605678905','601 789 0123','Teusaquillo','sur','',    'seed', 0),
('u8','Tienda Don Pedro','donpedro@correo.com','CC','79234567','320 333 4455','Teusaquillo','central','',        'seed', 0),
('u9','Cafe Origen','origen@correo.com','OTHER','9006789016','601 890 1234','Chico','norte','',                  'seed', 0),
('u10','Ana Torres','ana@correo.com','CC','1020456789','311 444 5566','Chapinero','norte','',                    'seed', 0),
('u11','Carlos Pineda','carlos@correo.com','OTHER','80345678','315 555 6677','Teusaquillo','sur','',             'seed', 0);