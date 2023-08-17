ALTER TABLE usuarios ADD COLUMN alias VARCHAR(100);

UPDATE usuarios SET alias = 'jperez' WHERE id = 1;
UPDATE usuarios SET alias = 'aramirez' WHERE id = 2;
UPDATE usuarios SET alias = 'cgomez' WHERE id = 3;
UPDATE usuarios SET alias = 'mtorrez' WHERE id = 4;
UPDATE usuarios SET alias = 'lfernandez' WHERE id = 5;


ALTER TABLE usuarios MODIFY COLUMN alias VARCHAR(100) NOT NULL;

ALTER TABLE usuarios ADD CONSTRAINT unique_alias_constraint UNIQUE (alias);

