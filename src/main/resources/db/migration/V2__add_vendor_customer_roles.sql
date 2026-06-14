-- Add VENDOR and CUSTOMER to users.role CHECK constraint
BEGIN;

ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

ALTER TABLE users
  ADD CONSTRAINT users_role_check CHECK (role IN ('USER','ADMIN','VENDOR','CUSTOMER'));

COMMIT;

