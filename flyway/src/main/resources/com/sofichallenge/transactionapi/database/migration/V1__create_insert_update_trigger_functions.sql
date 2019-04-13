CREATE OR REPLACE FUNCTION insert_created_column()
  RETURNS TRIGGER AS $insert_created_column$
BEGIN
  NEW.created = now();
  RETURN NEW;
END;
$insert_created_column$ language 'plpgsql';

CREATE OR REPLACE FUNCTION update_modified_column()
  RETURNS TRIGGER AS $$
BEGIN
  NEW.modified = now();
  RETURN NEW;
END;
$$ language 'plpgsql';