CREATE TABLE IF NOT EXISTS reservations (
  id           BIGINT PRIMARY KEY,
  client_id    BIGINT NOT NULL,
  event_id     BIGINT NOT NULL,
  ticket_count INT    NOT NULL,
  expiry_date  DATE  NOT NULL
);

CREATE TABLE IF NOT EXISTS reservation_counter (
  event_id   BIGINT  PRIMARY KEY,
  max_tickets INT NOT NULL,
  reserved_tickets INT NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
  id                     BIGINT PRIMARY KEY,
  name                   VARCHAR(50) NOT NULL,
  ticket_count           BIGINT NOT NULL,
  max_tickets_for_client INT NOT NULL
);
