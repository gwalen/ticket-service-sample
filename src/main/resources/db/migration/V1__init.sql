CREATE TABLE IF NOT EXISTS reservations (
  id           SERIAL PRIMARY KEY,
  client_id    BIGINT NOT NULL,
  event_id     BIGINT NOT NULL,
  ticket_count INT    NOT NULL,
  expiry_date  DATE  NOT NULL
);

CREATE TABLE IF NOT EXISTS reservation_counters (
  event_id   BIGINT  PRIMARY KEY,
  max_tickets INT NOT NULL,
  reserved_tickets INT NOT NULL,
  max_tickets_per_client INT NOT NULL
);

-- unique index to ensure one client will one have one reservation for given event to ensure we will not buy more than max number of tickers per client
-- we could validate it in run-time with summarizing all client reservations in separate query but thean we could have a race condition when
-- multiple requests for reservation would come for the same event and client
create unique index uidx_event_id_client_id on reservations(event_id, client_id);

-- test data
insert into reservation_counters(event_id, max_tickets, reserved_tickets, max_tickets_per_client) VALUES(1000, 500, 0, 5);
insert into reservations(id, client_id, event_id, ticket_count, expiry_date) values(10001, 100, 1000, 1, '2020-06-29 23:38:12');
insert into reservations(id, client_id, event_id, ticket_count, expiry_date) values(10002, 100, 1000, 1, '2020-06-29 23:38:12');
