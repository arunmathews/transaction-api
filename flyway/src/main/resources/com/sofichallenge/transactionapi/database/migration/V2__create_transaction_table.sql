CREATE TABLE "transaction" (
"id"                     SERIAL PRIMARY KEY,
"transaction_id"         INTEGER NOT NULL,
"user_id"                INTEGER NOT NULL,
"merchant_id"            INTEGER NOT NULL,
"merchant_name"          VARCHAR NOT NULL,
"price"                  NUMERIC(17, 6) NOT NULL,
"purchase_date"          TIMESTAMP NOT NULL,
"void"                   BOOLEAN NOT NULL,
"created"                TIMESTAMP NOT NULL,
"modified"               TIMESTAMP
);

CREATE UNIQUE INDEX "transaction_idx1" ON "transaction"("transaction_id");

CREATE INDEX "transaction_idx2" ON "transaction"("user_id");

CREATE INDEX "transaction_idx3" ON "transaction"("merchant_id");

CREATE TRIGGER "transaction_table_trigger_1"
BEFORE INSERT ON "transaction"
FOR EACH ROW EXECUTE PROCEDURE insert_created_column();

CREATE TRIGGER "transaction_table_trigger_2"
BEFORE UPDATE ON "transaction"
FOR EACH ROW EXECUTE PROCEDURE update_modified_column();