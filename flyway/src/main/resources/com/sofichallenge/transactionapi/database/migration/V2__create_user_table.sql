CREATE TABLE "user" (
"id"               SERIAL PRIMARY KEY,
"user_id"          VARCHAR(200) NOT NULL,
"active"           BOOLEAN NOT NULL,
"created"          TIMESTAMP NOT NULL,
"modified"         TIMESTAMP
);

CREATE UNIQUE INDEX "user_idx1" ON "user"("user_id");

CREATE TRIGGER "user_table_trigger_1"
BEFORE INSERT ON "user"
FOR EACH ROW EXECUTE PROCEDURE insert_created_column();

CREATE TRIGGER "user_table_trigger_2"
BEFORE UPDATE ON "user"
FOR EACH ROW EXECUTE PROCEDURE update_modified_column();