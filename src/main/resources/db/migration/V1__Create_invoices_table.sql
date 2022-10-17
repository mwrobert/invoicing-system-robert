CREATE TABLE public.invoices
(
    id          bigserial               NOT NULL,
    date        date                    NOT NULL,
    number      character varying(50)   NOT NULL,
    PRIMARY KEY (id)
)