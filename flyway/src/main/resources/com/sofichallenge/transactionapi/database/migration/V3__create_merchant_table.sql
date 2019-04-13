CREATE TABLE "merchant" (
"id"                     SERIAL PRIMARY KEY,
"merchant_id"            VARCHAR NOT NULL,
"merchant_name"          VARCHAR NOT NULL,
"active"                 BOOLEAN NOT NULL,
"created"                TIMESTAMP NOT NULL,
"modified"               TIMESTAMP
);

CREATE UNIQUE INDEX "merchant_idx1" ON "merchant"("merchant_id");

CREATE INDEX "merchant_idx2" ON "merchant"("merchant_name");

CREATE TRIGGER "merchant_table_trigger_1"
BEFORE INSERT ON "merchant"
FOR EACH ROW EXECUTE PROCEDURE insert_created_column();

CREATE TRIGGER "merchant_table_trigger_2"
BEFORE UPDATE ON "merchant"
FOR EACH ROW EXECUTE PROCEDURE update_modified_column();