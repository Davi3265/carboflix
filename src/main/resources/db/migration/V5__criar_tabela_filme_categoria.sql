CREATE TABLE filme_categoria (
    filme_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    CONSTRAINT pk_filme_categoria PRIMARY KEY (filme_id, categoria_id),
    CONSTRAINT fk_filme_categoria_filme FOREIGN KEY (filme_id) REFERENCES filme (id),
    CONSTRAINT fk_filme_categoria_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id)
);
