CREATE TABLE "transaction" (
"id"                     SERIAL PRIMARY KEY,
"transaction_id"         VARCHAR NOT NULL,
"user_id"                INTEGER NOT NULL,
"merchant_id"            INTEGER NOT NULL,
"price"                  NUMERIC(17, 6) NOT NULL,
"purchase_date"          TIMESTAMP NOT NULL,
"active"                 BOOLEAN NOT NULL,
"created"                TIMESTAMP NOT NULL,
"modified"               TIMESTAMP
);

ALTER TABLE "transaction" ADD CONSTRAINT "user_fk" FOREIGN KEY("user_id") REFERENCES "user"("id") ON UPDATE NO ACTION
ON DELETE NO ACTION;

ALTER TABLE "transaction" ADD CONSTRAINT "merchant_fk" FOREIGN KEY("merchant_id") REFERENCES "merchant"("id")
ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE UNIQUE INDEX "transaction_idx1" ON "transaction"("transaction_id");

CREATE INDEX "transaction_idx2" ON "transaction"("user_id");

CREATE INDEX "transaction_idx3" ON "transaction"("merchant_id");

CREATE TRIGGER "transaction_table_trigger_1"
BEFORE INSERT ON "transaction"
FOR EACH ROW EXECUTE PROCEDURE insert_created_column();

CREATE TRIGGER "transaction_table_trigger_2"
BEFORE UPDATE ON "transaction"
FOR EACH ROW EXECUTE PROCEDURE update_modified_column();