ALTER TABLE invoice_entries
    ADD COLUMN vat_rate_tmp character varying(10);

UPDATE invoice_entries
SET vat_rate_tmp = CONCAT('VAT_', (SELECT name FROM vat WHERE invoice_entries.vat_rate = id));

ALTER TABLE invoice_entries
    DROP vat_rate;

DROP TABLE vat;

ALTER TABLE invoice_entries
    ADD COLUMN vat_rate character varying(10);

UPDATE invoice_entries
SET vat_rate = vat_rate_tmp;

ALTER TABLE invoice_entries
    DROP vat_rate_tmp;

ALTER TABLE invoice_entries
    ALTER COLUMN vat_rate SET NOT NULL;