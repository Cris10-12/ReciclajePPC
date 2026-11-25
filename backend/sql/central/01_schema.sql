-- Esquema comun a los 3 nodos. Solo tablas e indices (sin datos).

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

CREATE TABLE connections (
  id CHAR(36) PRIMARY KEY,
  pair_key VARCHAR(80) NOT NULL UNIQUE,
  requester_id CHAR(36) NOT NULL,
  receiver_id CHAR(36) NOT NULL,
  status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
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

CREATE INDEX idx_posts_author ON posts (author_id, created_at);
CREATE INDEX idx_conn_requester ON connections (requester_id);
CREATE INDEX idx_conn_receiver ON connections (receiver_id);
CREATE INDEX idx_outbox_status ON outbox (status, id);
